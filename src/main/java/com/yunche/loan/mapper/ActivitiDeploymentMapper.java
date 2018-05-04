package com.yunche.loan.mapper;

import org.apache.ibatis.annotations.*;

/**
 * @author liuzhe
 * @date 2018/5/4
 */
@Mapper
public interface ActivitiDeploymentMapper {

    /**
     * 获取上个版本的部署ID
     *
     * @return
     */
    @Select("SELECT `DEPLOYMENT_ID_` FROM `act_re_procdef` ORDER BY `VERSION_` DESC LIMIT 1,1")
    Long getLastVersionDeploymentId();

    /**
     * 获取(当前部署的)最新版本的部署ID
     *
     * @return
     */
    @Select("SELECT `DEPLOYMENT_ID_` FROM `act_re_procdef` ORDER BY `VERSION_` DESC LIMIT 1")
    Long getNewVersionDeploymentId();

    /**
     * 仅保留新版本        - 将旧流程图的ACT_GE_BYTEARRAY表数据删除          资源文件(bpmn/png)
     *
     * @param newVersionDeploymentId
     * @return
     */
    @Delete("DELETE FROM `act_ge_bytearray` WHERE `DEPLOYMENT_ID_` IN " +
            "( SELECT DISTINCT(`DEPLOYMENT_ID_`)  FROM `act_re_procdef`  WHERE `DEPLOYMENT_ID_` != #{newVersionDeploymentId} )")
    Integer deleteAllBpmnAndPngExcludeNewVersion(@Param("newVersionDeploymentId") Long newVersionDeploymentId);

    /**
     * 仅保留上个版本       - 删除除了上个版本以外的所有版本                  流程定义
     *
     * @param lastVersionDeploymentId
     * @return
     */
    @Delete("DELETE FROM `act_re_procdef` WHERE `DEPLOYMENT_ID_` != #{lastVersionDeploymentId}")
    Integer deleteAllProcessDefinitionExcludeLastVersion(@Param("lastVersionDeploymentId") Long lastVersionDeploymentId);

    /**
     * 删除最新版本         - 将新流程图的ACT_RE_PROCDEF表数据删除           流程定义
     *
     * @param newVersionDeploymentId
     * @return
     */
    @Delete("DELETE FROM `act_re_procdef` WHERE `DEPLOYMENT_ID_` = #{newVersionDeploymentId}")
    Integer deleteNewVersionProcessDefinition(@Param("newVersionDeploymentId") Long newVersionDeploymentId);

    /**
     * 将新流程图的ACT_GE_BYTEARRAY表数据里的DEPLOYMENT_ID_修改成旧流程图ACT_RE_PROCDEF表数据的DEPLOYMENT_ID_
     *
     * @param lastVersionDeploymentId
     * @param newVersionDeploymentId
     */
    @Update("UPDATE `act_ge_bytearray`  SET `DEPLOYMENT_ID_` = #{lastVersionDeploymentId} WHERE `DEPLOYMENT_ID_` = #{newVersionDeploymentId}")
    Integer replaceDeploymentId(@Param("lastVersionDeploymentId") Long lastVersionDeploymentId, @Param("newVersionDeploymentId") Long newVersionDeploymentId);
}
