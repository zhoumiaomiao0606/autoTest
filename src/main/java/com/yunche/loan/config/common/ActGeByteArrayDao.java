package com.yunche.loan.config.common;

import org.apache.shiro.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.AbstractLobStreamingResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author liuzhe
 * @date 2018/4/19
 */
@Component
public class ActGeByteArrayDao {

    private static final Logger logger = LoggerFactory.getLogger(ActGeByteArrayDao.class);

    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 取得流程定义的XML
     *
     * @param deployId
     * @return
     */
    public String getDefXmlByDeployId(String deployId) {

        String sql = "select a.* from ACT_GE_BYTEARRAY a where NAME_ LIKE '%loan_process.bpmn' and DEPLOYMENT_ID_= ? ";

        // reusable
        final LobHandler lobHandler = new DefaultLobHandler();
        final ByteArrayOutputStream contentOs = new ByteArrayOutputStream();

        String defXml = null;
        try {
            jdbcTemplate.query(sql, new Object[]{deployId}, new AbstractLobStreamingResultSetExtractor<Object>() {
                        @Override
                        public void streamData(ResultSet rs) throws SQLException, IOException {
                            FileCopyUtils.copy(lobHandler.getBlobAsBinaryStream(rs, "BYTES_"), contentOs);
                        }
                    }
            );
            defXml = new String(contentOs.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            logger.error("getDefXmlByDeployId error", ex);
        }
        return defXml;
    }


    /**
     * 把修改过的xml更新至回流程定义中
     *
     * @param deployId
     * @param defXml
     */
    public void writeDefXml(final String deployId, String defXml) {
        try {

            LobHandler lobHandler = new DefaultLobHandler();
            final byte[] btyesXml = defXml.getBytes("UTF-8");

            String sql = "update ACT_GE_BYTEARRAY set BYTES_=? where NAME_ LIKE '%loan_process.bpmn' and DEPLOYMENT_ID_= ? ";

            jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                @Override
                protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                    lobCreator.setBlobAsBytes(ps, 1, btyesXml);
                    ps.setString(2, deployId);
                }
            });
        } catch (Exception ex) {
            logger.error("writeDefXml error", ex);
        }
    }
}
