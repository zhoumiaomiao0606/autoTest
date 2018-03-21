package com.yunche.loan.web.aop;

import org.apache.ibatis.type.*;


import java.math.BigDecimal;
import java.sql.*;

@MappedJdbcTypes(value = {JdbcType.NUMERIC,JdbcType.DECIMAL})
public class AmountTypeHandler extends StringTypeHandler {

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if(obj instanceof BigDecimal){
            return ((BigDecimal) obj).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        }
        return rs.getString(columnName);
    }
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if(obj instanceof BigDecimal){
            return ((BigDecimal) obj).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        }
        return rs.getString(columnIndex);
    }
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        if(obj instanceof BigDecimal){
            return ((BigDecimal) obj).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        }
        return cs.getString(columnIndex);
    }


}
