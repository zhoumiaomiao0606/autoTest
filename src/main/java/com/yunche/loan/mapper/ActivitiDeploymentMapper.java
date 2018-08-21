package com.yunche.loan.mapper;

import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author liuzhe
 * @date 2018/5/4
 */
public interface ActivitiDeploymentMapper {

    /**
     * 获取所有部署中流程的  RESOURCE_NAME_
     *
     * @return
     */
    @Select("SELECT DISTINCT(`RESOURCE_NAME_`) FROM `act_re_procdef`")
    Set<String> getAllResourceName();

    /**
     * 获取上个版本的部署ID
     *
     * @param resourceName
     * @return
     */
    @Select("SELECT `DEPLOYMENT_ID_` FROM `act_re_procdef` WHERE `RESOURCE_NAME_` = #{resourceName} " +
            " ORDER BY `VERSION_` DESC LIMIT 1,1")
    Long getLastVersionDeploymentId(String resourceName);

    /**
     * 获取(当前部署的)最新版本的部署ID
     *
     * @param resourceName
     * @return
     */
    @Select("SELECT `DEPLOYMENT_ID_` FROM `act_re_procdef` WHERE `RESOURCE_NAME_` = #{resourceName} " +
            " ORDER BY `VERSION_` DESC LIMIT 1")
    Long getNewVersionDeploymentId(String resourceName);

    /**
     * 仅保留新版本        - 将旧流程图的ACT_GE_BYTEARRAY表数据删除          资源文件(bpmn/png)
     *
     * @param newVersionDeploymentId
     * @param resourceName
     * @return
     */
    @Delete("DELETE FROM `act_ge_bytearray` WHERE `DEPLOYMENT_ID_` IN " +
            " ( SELECT DISTINCT(`DEPLOYMENT_ID_`)  FROM `act_re_procdef`  WHERE `DEPLOYMENT_ID_` != #{newVersionDeploymentId} " +
            " AND `RESOURCE_NAME_` = #{resourceName} )")
    Integer deleteAllBpmnAndPngExcludeNewVersion(@Param("newVersionDeploymentId") Long newVersionDeploymentId,
                                                 @Param("resourceName") String resourceName);

    /**
     * 仅保留上个版本       - 删除除了上个版本以外的所有版本                  流程定义
     *
     * @param lastVersionDeploymentId
     * @param resourceName
     * @return
     */
    @Delete("DELETE FROM `act_re_procdef` WHERE `DEPLOYMENT_ID_` != #{lastVersionDeploymentId} AND `RESOURCE_NAME_` = #{resourceName}")
    Integer deleteAllProcessDefinitionExcludeLastVersion(@Param("lastVersionDeploymentId") Long lastVersionDeploymentId,
                                                         @Param("resourceName") String resourceName);

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

    /**
     * 执行中的-[代偿任务]列表
     *
     * @param orderId
     * @return
     */
    @Results(id = "taskResultMap",
            value = {@Result(column = "ID_", property = "id", javaType = String.class),
                    @Result(column = "REV_", property = "revision", javaType = Integer.class),
                    @Result(column = "NAME_", property = "name", javaType = String.class),
                    @Result(column = "PARENT_TASK_ID_", property = "parentTaskId", javaType = String.class),
                    @Result(column = "DESCRIPTION_", property = "description", javaType = String.class),
                    @Result(column = "PRIORITY_", property = "priority", javaType = Integer.class),
                    @Result(column = "CREATE_TIME_", property = "createTime", javaType = Date.class),
                    @Result(column = "OWNER_", property = "owner", javaType = String.class),
                    @Result(column = "ASSIGNEE_", property = "assignee", javaType = String.class),
                    @Result(column = "DELEGATION_", property = "delegationStateString", javaType = String.class),
                    @Result(column = "EXECUTION_ID_", property = "executionId", javaType = String.class),
                    @Result(column = "PROC_INST_ID_", property = "processInstanceId", javaType = String.class),
                    @Result(column = "PROC_DEF_ID_", property = "processDefinitionId", javaType = String.class),
                    @Result(column = "TASK_DEF_KEY_", property = "taskDefinitionKey", javaType = String.class),
                    @Result(column = "DUE_DATE_", property = "dueDate", javaType = Date.class),
                    @Result(column = "CATEGORY_", property = "category", javaType = String.class),
                    @Result(column = "SUSPENSION_STATE_", property = "suspensionState", javaType = Integer.class),
                    @Result(column = "TENANT_ID_", property = "tenantId", javaType = String.class),
                    @Result(column = "FORM_KEY_", property = "formKey", javaType = String.class),
                    @Result(column = "CLAIM_TIME_", property = "claimTime", javaType = Date.class)
            })
    @Select("SELECT * FROM `act_ru_task` WHERE `PROC_INST_ID_` IN " +
            " ( SELECT `process_inst_id` FROM `loan_process_instead_pay` WHERE `order_id` = #{orderId} ) ")
    List<TaskEntityImpl> listInsteadPayTaskByOrderId(Long orderId);

    /**
     * 执行中的-[催收任务]列表
     *
     * @param orderId
     * @return
     */
    @ResultMap("taskResultMap")
    @Select("SELECT * FROM `act_ru_task` WHERE `PROC_INST_ID_` IN " +
            " ( SELECT `process_inst_id` FROM `loan_process_collection` WHERE `order_id` = #{orderId} ) ")
    List<TaskEntityImpl> listCollectionTaskByOrderId(Long orderId);
}