SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS  `act_evt_log`;
CREATE TABLE `act_evt_log` (
  `LOG_NR_` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TIME_STAMP_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DATA_` longblob,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `IS_PROCESSED_` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`LOG_NR_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ge_bytearray`;
CREATE TABLE `act_ge_bytearray` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BYTES_` longblob,
  `GENERATED_` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ge_property`;
CREATE TABLE `act_ge_property` (
  `NAME_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `VALUE_` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  PRIMARY KEY (`NAME_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_actinst`;
CREATE TABLE `act_hi_actinst` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `ACT_ID_` varchar(255) COLLATE utf8_bin NOT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ACT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`),
  KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_attachment`;
CREATE TABLE `act_hi_attachment` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `URL_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `CONTENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TIME_` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_comment`;
CREATE TABLE `act_hi_comment` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TIME_` datetime(3) NOT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACTION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MESSAGE_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `FULL_MSG_` longblob,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_detail`;
CREATE TABLE `act_hi_detail` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VAR_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TIME_` datetime(3) NOT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
  KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
  KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_identitylink`;
CREATE TABLE `act_hi_identitylink` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_procinst`;
CREATE TABLE `act_hi_procinst` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `END_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_taskinst`;
CREATE TABLE `act_hi_taskinst` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `CLAIM_TIME_` datetime(3) DEFAULT NULL,
  `END_TIME_` datetime(3) DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `PRIORITY_` int(11) DEFAULT NULL,
  `DUE_DATE_` datetime(3) DEFAULT NULL,
  `FORM_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_hi_varinst`;
CREATE TABLE `act_hi_varinst` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VAR_TYPE_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME_` datetime(3) DEFAULT NULL,
  `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`),
  KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_id_group`;
CREATE TABLE `act_id_group` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_id_info`;
CREATE TABLE `act_id_info` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `USER_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VALUE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PASSWORD_` longblob,
  `PARENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_id_membership`;
CREATE TABLE `act_id_membership` (
  `USER_ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `GROUP_ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`USER_ID_`,`GROUP_ID_`),
  KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`),
  CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`),
  CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_id_user`;
CREATE TABLE `act_id_user` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `FIRST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LAST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EMAIL_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PWD_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PICTURE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_procdef_info`;
CREATE TABLE `act_procdef_info` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `INFO_JSON_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
  CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
  CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `act_ge_bytearray` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_re_deployment`;
CREATE TABLE `act_re_deployment` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL,
  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_re_model`;
CREATE TABLE `act_re_model` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LAST_UPDATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `VERSION_` int(11) DEFAULT NULL,
  `META_INFO_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EDITOR_SOURCE_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
  KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
  KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_re_procdef`;
CREATE TABLE `act_re_procdef` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VERSION_` int(11) NOT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DGRM_RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `HAS_START_FORM_KEY_` tinyint(4) DEFAULT NULL,
  `HAS_GRAPHICAL_NOTATION_` tinyint(4) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_deadletter_job`;
CREATE TABLE `act_ru_deadletter_job` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_event_subscr`;
CREATE TABLE `act_ru_event_subscr` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `EVENT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EVENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACTIVITY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CONFIGURATION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
  KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
  CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_execution`;
CREATE TABLE `act_ru_execution` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `SUPER_EXEC_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ROOT_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE_` tinyint(4) DEFAULT NULL,
  `IS_CONCURRENT_` tinyint(4) DEFAULT NULL,
  `IS_SCOPE_` tinyint(4) DEFAULT NULL,
  `IS_EVENT_SCOPE_` tinyint(4) DEFAULT NULL,
  `IS_MI_ROOT_` tinyint(4) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `CACHED_ENT_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(3) DEFAULT NULL,
  `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `IS_COUNT_ENABLED_` tinyint(4) DEFAULT NULL,
  `EVT_SUBSCR_COUNT_` int(11) DEFAULT NULL,
  `TASK_COUNT_` int(11) DEFAULT NULL,
  `JOB_COUNT_` int(11) DEFAULT NULL,
  `TIMER_JOB_COUNT_` int(11) DEFAULT NULL,
  `SUSP_JOB_COUNT_` int(11) DEFAULT NULL,
  `DEADLETTER_JOB_COUNT_` int(11) DEFAULT NULL,
  `VAR_COUNT_` int(11) DEFAULT NULL,
  `ID_LINK_COUNT_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
  KEY `ACT_IDC_EXEC_ROOT` (`ROOT_PROC_INST_ID_`),
  KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
  KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
  KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
  CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE,
  CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_identitylink`;
CREATE TABLE `act_ru_identitylink` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
  KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
  KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
  CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
  CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `act_ru_task` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_job`;
CREATE TABLE `act_ru_job` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_suspended_job`;
CREATE TABLE `act_ru_suspended_job` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_task`;
CREATE TABLE `act_ru_task` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DELEGATION_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PRIORITY_` int(11) DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `DUE_DATE_` datetime(3) DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `FORM_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CLAIM_TIME_` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
  KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_timer_job`;
CREATE TABLE `act_ru_timer_job` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_TIMER_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_TIMER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_TIMER_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `act_ru_variable`;
CREATE TABLE `act_ru_variable` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
  KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
  CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `app_version`;
CREATE TABLE `app_version` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `version_code` varchar(32) DEFAULT NULL COMMENT '版本号',
  `version_name` varchar(32) DEFAULT NULL COMMENT '版本名称',
  `info` text COMMENT '功能更新介绍	',
  `download_url` varchar(200) DEFAULT NULL COMMENT '下载链接',
  `terminal_type` tinyint(1) DEFAULT NULL COMMENT '终端类型(1:IOS;2:Android)',
  `update_type` tinyint(1) DEFAULT NULL COMMENT '更新类型(1:可选;2:强制;)',
  `release_date` datetime DEFAULT NULL COMMENT '发布日期',
  `is_latest_version` tinyint(1) DEFAULT NULL COMMENT '是否最新版本(0:否;1:是)',
  `store_type` tinyint(1) DEFAULT NULL COMMENT '应用商店类型',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 (0:有效;1:无效;2:已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='APP版本信息';

DROP TABLE IF EXISTS  `apply_license_plate_deposit_info`;
CREATE TABLE `apply_license_plate_deposit_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_license_plate_deposit_date` date NOT NULL COMMENT '上牌抵押日期',
  `registration_certificate_number` varchar(20) NOT NULL COMMENT '登记证书号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-正常，1-停用，2-删除',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='上牌信息';

DROP TABLE IF EXISTS  `apply_license_plate_record`;
CREATE TABLE `apply_license_plate_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `vehicle_identification_number` varchar(20) NOT NULL COMMENT '车架号',
  `license_plate_number` varchar(20) NOT NULL COMMENT '车牌号',
  `apply_license_plate_date` date NOT NULL COMMENT '上牌日期',
  `engine_number` varchar(20) NOT NULL COMMENT '发动机号',
  `license_plate_type` tinyint(1) NOT NULL COMMENT '牌证类型: 1-公牌; 2-私牌;',
  `apply_license_plate_area_id` bigint(20) NOT NULL COMMENT '上牌地',
  `registration_certificate_number` varchar(20) NOT NULL COMMENT '登记证书号',
  `qualified_certificate_number` varchar(20) NOT NULL COMMENT '合格证号',
  `car_model` varchar(20) NOT NULL COMMENT '车品牌',
  `transfer_ownership_date` date NOT NULL COMMENT '过户日期',
  `license_plate_readiness_date` date NOT NULL COMMENT '牌证齐全日期',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='上牌记录';

DROP TABLE IF EXISTS  `auth`;
CREATE TABLE `auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `source_id` bigint(20) DEFAULT NULL COMMENT '关联的权限实体ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '权限类型(1:menu权限;2:page权限;3:operation权限;)',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8 COMMENT='权限表';

DROP TABLE IF EXISTS  `base_area`;
CREATE TABLE `base_area` (
  `area_id` bigint(20) NOT NULL COMMENT '地区编码',
  `parent_area_id` bigint(20) DEFAULT NULL COMMENT '上级地区编码',
  `area_name` varchar(100) DEFAULT NULL COMMENT '地区名称',
  `parent_area_name` varchar(100) DEFAULT NULL COMMENT '上级地区名称',
  `level` tinyint(4) DEFAULT NULL COMMENT '地区等级(0:全国;1:省;2:市)',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='地区表';

DROP TABLE IF EXISTS  `biz_area`;
CREATE TABLE `biz_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上一级区域',
  `employee_id` bigint(20) NOT NULL COMMENT '部门负责人ID(employee表)',
  `level` int(11) DEFAULT NULL COMMENT '区域等级',
  `info` text COMMENT '说明',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='业务区域表';

DROP TABLE IF EXISTS  `biz_area_rela_area`;
CREATE TABLE `biz_area_rela_area` (
  `biz_area_id` bigint(20) NOT NULL COMMENT '业务区域ID',
  `area_id` bigint(20) NOT NULL COMMENT '区域ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`biz_area_id`,`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务区域-区域关联表';

DROP TABLE IF EXISTS  `biz_model`;
CREATE TABLE `biz_model` (
  `biz_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务产品ID',
  `title` varchar(32) DEFAULT NULL COMMENT '业务产品名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `scene` varchar(32) DEFAULT NULL COMMENT '业务场景',
  `cust_target` varchar(32) DEFAULT NULL COMMENT '贷款对象',
  `car_type` tinyint(4) DEFAULT NULL COMMENT '车辆状况:0-新车/1-二手车/2-不限',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态：0-正常，1-停用，2-删除',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`biz_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8 COMMENT='业务产品配置表';

DROP TABLE IF EXISTS  `biz_model_rela_area_partners`;
CREATE TABLE `biz_model_rela_area_partners` (
  `biz_id` bigint(20) NOT NULL COMMENT '业务产品ID',
  `area_id` bigint(20) NOT NULL COMMENT '区域ID',
  `group_id` bigint(20) NOT NULL COMMENT '合伙人ID',
  `prov` varchar(32) DEFAULT NULL,
  `city` varchar(32) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`biz_id`,`area_id`,`group_id`),
  KEY `rela_groupId_fk_2` (`group_id`),
  KEY `rela_areaId_fk_2` (`area_id`),
  CONSTRAINT `rela_areaId_fk_2` FOREIGN KEY (`area_id`) REFERENCES `base_area` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务产品覆盖区域和合伙人';

DROP TABLE IF EXISTS  `biz_model_rela_financial_prod`;
CREATE TABLE `biz_model_rela_financial_prod` (
  `biz_id` bigint(20) NOT NULL,
  `prod_id` bigint(20) NOT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`biz_id`,`prod_id`),
  KEY `rela_prodId_fk_2` (`prod_id`),
  CONSTRAINT `rela_bizId_fk_1` FOREIGN KEY (`biz_id`) REFERENCES `biz_model` (`biz_id`),
  CONSTRAINT `rela_prodId_fk_2` FOREIGN KEY (`prod_id`) REFERENCES `financial_product` (`prod_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务产品关联金融产品';

DROP TABLE IF EXISTS  `car_brand`;
CREATE TABLE `car_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '车型品牌ID',
  `name` varchar(32) DEFAULT NULL COMMENT '品牌名称',
  `initial` varchar(1) DEFAULT NULL COMMENT '首字母',
  `logo` varchar(255) DEFAULT NULL COMMENT 'logo图片URL',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41915 DEFAULT CHARSET=utf8 COMMENT='汽车品牌表';

DROP TABLE IF EXISTS  `car_detail`;
CREATE TABLE `car_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '车型ID',
  `model_id` bigint(20) DEFAULT NULL COMMENT '所属车系ID',
  `name` varchar(64) DEFAULT NULL COMMENT '车款名称',
  `initial` varchar(1) DEFAULT NULL COMMENT '首字母',
  `logo` varchar(255) DEFAULT NULL COMMENT 'logo图片URL',
  `price` varchar(32) DEFAULT NULL COMMENT '厂家指导价',
  `sale_price` varchar(32) DEFAULT NULL COMMENT '商家报价',
  `seat_num` int(11) DEFAULT NULL COMMENT '座位数',
  `door_num` int(11) DEFAULT NULL COMMENT '车门数量',
  `year_type` varchar(32) DEFAULT NULL COMMENT '年款',
  `production_state` tinyint(1) DEFAULT NULL COMMENT '生产状态(1:在产;2:停产)',
  `sale_state` tinyint(1) DEFAULT NULL COMMENT '销售状态（1:在售; 2:停售)',
  `size_type` varchar(32) DEFAULT NULL COMMENT '尺寸类型(eg:紧凑型车)',
  `fuel_type` tinyint(1) DEFAULT NULL COMMENT '燃油类型（1:汽油;2:柴油）',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`),
  KEY `idx_model_id` (`model_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=42853 DEFAULT CHARSET=utf8 COMMENT='车款表';

DROP TABLE IF EXISTS  `car_model`;
CREATE TABLE `car_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '车系ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '所属品牌ID',
  `name` varchar(32) DEFAULT NULL COMMENT '车型简称',
  `full_name` varchar(32) DEFAULT NULL COMMENT '车型全称',
  `initial` varchar(1) DEFAULT NULL COMMENT '首字母',
  `logo` varchar(255) DEFAULT NULL COMMENT 'logo图片URL',
  `price` varchar(32) DEFAULT NULL COMMENT '厂家指导价（min-max,取子车型的极值）',
  `seat_num` varchar(100) DEFAULT NULL COMMENT '座位数(min/max，取子车型的极值)',
  `sale_state` tinyint(1) DEFAULT NULL COMMENT '销售状态（1:在售; 2:停售）',
  `series_code` varchar(32) DEFAULT NULL COMMENT '车系编码',
  `mnemonic_code` varchar(32) DEFAULT NULL COMMENT '助记码',
  `production_firm` varchar(32) DEFAULT NULL COMMENT '生产厂商',
  `production_type` tinyint(1) DEFAULT NULL COMMENT '生产类型（1:国产;2:合资）',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`),
  KEY `idx_brand_id` (`brand_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=42732 DEFAULT CHARSET=utf8 COMMENT='车系表';

DROP TABLE IF EXISTS  `cost_details`;
CREATE TABLE `cost_details` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_fee` decimal(10,6) DEFAULT NULL COMMENT '服务费',
  `apply_license_plate_deposit_fee` decimal(10,6) DEFAULT NULL COMMENT '上牌押金',
  `performance_fee` decimal(10,6) DEFAULT NULL COMMENT '履约金',
  `install_gps_fee` decimal(10,6) DEFAULT NULL COMMENT '安装gps费用',
  `risk_fee` decimal(10,6) DEFAULT NULL COMMENT '风险费用',
  `fair_assess_fee` decimal(10,6) DEFAULT NULL COMMENT '公正评估费',
  `apply_license_plate_out_province_fee` decimal(10,6) DEFAULT NULL COMMENT '上省外牌费用',
  `based_margin_fee` decimal(10,6) DEFAULT NULL COMMENT '基础保证金',
  `service_fee_type` tinyint(10) DEFAULT NULL COMMENT '服务费 			1 打款内扣 2 返利内扣 3 实收',
  `apply_license_plate_deposit_fee_type` tinyint(1) DEFAULT NULL COMMENT '上牌押金 			1 打款内扣 2 返利内扣 3 实收',
  `performance_fee_type` tinyint(1) DEFAULT NULL COMMENT '履约金			1 打款内扣 2 返利内扣 3 实收',
  `install_gps_fee_type` tinyint(1) DEFAULT NULL COMMENT '安装gps费用		1 打款内扣 2 返利内扣 3 实收',
  `risk_fee_type` tinyint(1) DEFAULT NULL COMMENT '风险费用			1 打款内扣 2 返利内扣 3 实收',
  `fair_assess_fee_type` tinyint(1) DEFAULT NULL COMMENT '公正评估费		1 打款内扣 2 返利内扣 3 实收',
  `apply_license_plate_out_province_fee_type` tinyint(1) DEFAULT NULL COMMENT '上省外牌费用		1 打款内扣 2 返利内扣 3 实收',
  `based_margin_fee_type` tinyint(1) DEFAULT NULL COMMENT '基础保证金		1 打款内扣 2 返利内扣 3 实收',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-有效;1-无效;',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='费用明细';

DROP TABLE IF EXISTS  `department`;
CREATE TABLE `department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(32) NOT NULL COMMENT '部门名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门ID',
  `leader_id` bigint(20) DEFAULT NULL COMMENT '部门负责人(员工)ID',
  `area_id` bigint(20) DEFAULT NULL COMMENT '区域ID（省/市/区）',
  `level` int(11) DEFAULT NULL COMMENT '部门等级',
  `tel` varchar(32) DEFAULT NULL COMMENT '部门电话',
  `fax` varchar(32) DEFAULT NULL COMMENT '部门传真',
  `address` varchar(100) DEFAULT NULL COMMENT '具体地址',
  `open_bank` varchar(64) DEFAULT NULL COMMENT '开户行',
  `receive_unit` varchar(64) DEFAULT NULL COMMENT '收款单位',
  `bank_account` varchar(32) DEFAULT NULL COMMENT '银行账号',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 (0:有效;1:无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='部门表（用户组）';

DROP TABLE IF EXISTS  `department_rela_user_group`;
CREATE TABLE `department_rela_user_group` (
  `department_id` bigint(20) NOT NULL COMMENT '部门ID',
  `user_group_id` bigint(20) NOT NULL COMMENT '用户组ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`department_id`,`user_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门-用户组 关联表';

DROP TABLE IF EXISTS  `employee`;
CREATE TABLE `employee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name` varchar(32) DEFAULT NULL COMMENT '员工姓名',
  `password` varchar(48) DEFAULT NULL COMMENT '密码',
  `id_card` char(18) DEFAULT NULL COMMENT '身份证号码',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `email` varchar(32) DEFAULT NULL COMMENT '电子邮箱',
  `ding_ding` varchar(32) DEFAULT NULL COMMENT '钉钉账号',
  `department_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '直接主管ID',
  `title` varchar(32) DEFAULT NULL COMMENT '职位',
  `entry_date` datetime DEFAULT NULL COMMENT '入职时间',
  `level` int(11) DEFAULT NULL COMMENT '员工等级',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型(1:正式员工;2:外包员工;)',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) unsigned DEFAULT '1' COMMENT '状态 (0:有效;1:无效)',
  `machine_id` varchar(64) DEFAULT NULL COMMENT '设备id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8 COMMENT='员工工号表（用户表）';

DROP TABLE IF EXISTS  `employee_rela_department`;
CREATE TABLE `employee_rela_department` (
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `department_id` bigint(20) NOT NULL COMMENT '部门ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`employee_id`,`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='员工-部门 关联表';

DROP TABLE IF EXISTS  `employee_rela_user_group`;
CREATE TABLE `employee_rela_user_group` (
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `user_group_id` bigint(20) NOT NULL COMMENT '用户组ID',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`employee_id`,`user_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='员工-用户组关系表（用户-角色表）';

DROP TABLE IF EXISTS  `financial_product`;
CREATE TABLE `financial_product` (
  `prod_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT ' 产品ID',
  `prod_name` varchar(100) NOT NULL COMMENT '金融产品名称',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称',
  `mnemonic_code` varchar(100) DEFAULT NULL COMMENT '助记码',
  `account` varchar(100) DEFAULT NULL COMMENT '对应账户',
  `sign_phone` varchar(32) DEFAULT NULL COMMENT '面签接收人手机',
  `sign_bank_code` varchar(32) DEFAULT NULL COMMENT '视频面签银行编码',
  `biz_type` tinyint(4) DEFAULT NULL COMMENT '0-新车/1-二手车/2-不限',
  `category_superior` varchar(32) DEFAULT NULL COMMENT '产品大类',
  `category_junior` varchar(32) DEFAULT NULL COMMENT '产品小类',
  `rate` varchar(32) DEFAULT NULL COMMENT '费率',
  `mortgage_term` tinyint(4) DEFAULT NULL COMMENT '按揭期限',
  `area_id` bigint(20) DEFAULT NULL COMMENT '覆盖区域',
  `prov` varchar(32) DEFAULT NULL COMMENT '省',
  `city` varchar(32) DEFAULT NULL COMMENT '市',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态：0-正常，1-失效',
  `formula_id` int(11) DEFAULT NULL COMMENT '公式编号',
  PRIMARY KEY (`prod_id`),
  KEY `finprod_area_fk_1` (`area_id`),
  CONSTRAINT `finprod_area_fk_1` FOREIGN KEY (`area_id`) REFERENCES `base_area` (`area_id`) ON DELETE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=100050 DEFAULT CHARSET=utf8 COMMENT='金融产品配置表';

DROP TABLE IF EXISTS  `flow_operation_msg`;
CREATE TABLE `flow_operation_msg` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` bigint(20) NOT NULL COMMENT '员工id',
  `order_id` bigint(20) NOT NULL COMMENT '单号',
  `title` varchar(32) NOT NULL COMMENT '标题',
  `prompt` varchar(200) NOT NULL COMMENT '提示',
  `msg` varchar(1000) NOT NULL COMMENT '消息内容',
  `sender` varchar(20) NOT NULL COMMENT '发送人',
  `process_key` varchar(64) NOT NULL COMMENT '流程key',
  `send_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建日期',
  `read_status` tinyint(1) NOT NULL COMMENT '0 否 1 是',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-有效;1-无效;',
  `type` tinyint(1) NOT NULL COMMENT '0 未知 1 正常 2 提示  3 错误 4 警告 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8 COMMENT='工作流';

DROP TABLE IF EXISTS  `install_gps`;
CREATE TABLE `install_gps` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `gps_number` varchar(32) NOT NULL COMMENT 'gps编号',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-有效;1-无效;',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='g p s 安装清单';

DROP TABLE IF EXISTS  `insurance_company`;
CREATE TABLE `insurance_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `mnemonic_code` varchar(32) DEFAULT NULL COMMENT '助记码',
  `contact` varchar(32) DEFAULT NULL COMMENT '联系人',
  `tel` varchar(32) DEFAULT NULL COMMENT '手机号',
  `office_phone` varchar(32) NOT NULL COMMENT '办公室电话',
  `fax` varchar(32) NOT NULL COMMENT '传真',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `business_insurance_rebate` decimal(10,5) DEFAULT NULL COMMENT '商业险返还利率',
  `traffic_insurance_rebate` decimal(10,5) DEFAULT NULL COMMENT '交强险返还利率',
  `cooperation_policy` text COMMENT '合作政策',
  `bank` varchar(32) NOT NULL COMMENT '开户行',
  `account_name` varchar(32) DEFAULT NULL COMMENT '开户名',
  `bank_account` varchar(32) NOT NULL COMMENT '银行账号',
  `file` varchar(1000) DEFAULT NULL COMMENT '文件上传路径',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(500) DEFAULT NULL COMMENT '扩展字段',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='保险公司表';

DROP TABLE IF EXISTS  `insurance_info`;
CREATE TABLE `insurance_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint(20) NOT NULL COMMENT '订单id ',
  `issue_bills_date` date NOT NULL COMMENT '出单日期',
  `insurance_year` tinyint(1) NOT NULL COMMENT '年度',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='保险信息';

DROP TABLE IF EXISTS  `insurance_relevance`;
CREATE TABLE `insurance_relevance` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `insurance_info_id` bigint(20) NOT NULL COMMENT '保险信息id ',
  `insurance_company_name` varchar(20) NOT NULL COMMENT '保险公司名称',
  `insurance_number` varchar(32) NOT NULL COMMENT '保单号',
  `insurance_amount` decimal(10,2) NOT NULL COMMENT '保险金额',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `insurance_type` tinyint(1) NOT NULL COMMENT '1 商业险 2 交强险 3 车船税',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`),
  KEY `idx_insurance_info_id` (`insurance_info_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COMMENT='保险信息关联表';

DROP TABLE IF EXISTS  `loan_base_info`;
CREATE TABLE `loan_base_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `partner_id` bigint(20) DEFAULT NULL COMMENT '业务员所属合伙人ID',
  `salesman_id` bigint(20) DEFAULT NULL COMMENT '业务员ID',
  `area_id` bigint(20) DEFAULT NULL COMMENT '区域ID（业务员当前所在区域）',
  `car_type` tinyint(1) DEFAULT NULL COMMENT '所贷车辆类型：1-新车; 2-二手车; 3-不限;',
  `bank` varchar(64) DEFAULT NULL COMMENT '贷款银行',
  `loan_amount` tinyint(1) DEFAULT NULL COMMENT '贷款额度档次：1 - 13W以下; 2 - 13至20W; 3 - 20W以上;',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态: 0-有效; 1-无效;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8 COMMENT='贷款基本信息表';

DROP TABLE IF EXISTS  `loan_car_info`;
CREATE TABLE `loan_car_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `car_detail_id` bigint(20) DEFAULT NULL COMMENT '车型ID',
  `car_detail_name` varchar(32) DEFAULT NULL COMMENT '车型名称',
  `car_type` tinyint(1) DEFAULT NULL COMMENT '车辆类型：1-新车; 2-二手车; 3-不限;',
  `gps_num` int(11) DEFAULT NULL COMMENT 'GPS个数',
  `car_key` tinyint(1) DEFAULT NULL COMMENT '是否留备用钥匙：0-否;1-是;',
  `partner_id` bigint(20) DEFAULT NULL COMMENT '业务员所属合伙人ID',
  `partner_name` varchar(32) DEFAULT NULL COMMENT '合伙人名称',
  `open_bank` varchar(64) DEFAULT NULL COMMENT '收款银行',
  `account_name` varchar(64) DEFAULT NULL COMMENT '收款账户',
  `bank_account` varchar(32) DEFAULT NULL COMMENT '收款账号',
  `pay_month` tinyint(1) DEFAULT NULL COMMENT '是否月结：0-否;1-是;',
  `info` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0-有效;1-无效;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 COMMENT='贷款车辆信息';

DROP TABLE IF EXISTS  `loan_credit_info`;
CREATE TABLE `loan_credit_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `result` tinyint(1) DEFAULT NULL COMMENT '征信结果: 0-不通过;1-通过;2-关注;',
  `info` varchar(500) DEFAULT NULL COMMENT '备注',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型: 1-银行征信;2-社会征信;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0-有效;1-无效;',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COMMENT='贷款征信信息表';

DROP TABLE IF EXISTS  `loan_customer`;
CREATE TABLE `loan_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '客户id',
  `name` varchar(32) DEFAULT NULL COMMENT '客户名称',
  `nation` varchar(32) DEFAULT NULL COMMENT '民族',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `identity_validity` date DEFAULT NULL COMMENT '身份证有效期',
  `id_card` char(18) DEFAULT NULL COMMENT '身份证号码',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `age` tinyint(3) DEFAULT NULL COMMENT '年龄',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别: 1-男;2-女;',
  `apply_date` date DEFAULT NULL COMMENT '申请日期',
  `address` varchar(100) DEFAULT NULL COMMENT '现住地址',
  `marry` tinyint(1) DEFAULT NULL COMMENT '婚姻状态:0-未婚；1-已婚；',
  `identity_address` varchar(100) DEFAULT NULL COMMENT '身份证地址',
  `mobile_area` varchar(32) DEFAULT NULL COMMENT '手机号归属地',
  `education` tinyint(1) DEFAULT NULL COMMENT '学历：1-高中及以下;2-专科;3-本科;4-硕士;5-博士;',
  `company_name` varchar(100) DEFAULT NULL COMMENT '单位名称',
  `company_phone` varchar(32) DEFAULT NULL COMMENT '单位电话',
  `company_address` varchar(100) DEFAULT NULL COMMENT '单位地址',
  `month_income` decimal(12,2) DEFAULT NULL COMMENT '月收入',
  `house_type` tinyint(1) DEFAULT NULL COMMENT '房产情况:1-自有商品房有贷款;2-自有商品房无贷款;',
  `house_owner` tinyint(1) DEFAULT NULL COMMENT '房产所有人:1-本人所有;2-夫妻共有;',
  `house_feature` tinyint(1) DEFAULT NULL COMMENT '房产性质：1-商品房有贷款;2-商品房无贷款;',
  `house_address` varchar(100) DEFAULT NULL COMMENT '房产地址',
  `info` varchar(200) DEFAULT NULL COMMENT '备注',
  `cust_type` tinyint(1) DEFAULT NULL COMMENT '客户类型: 1-主贷人;2-共贷人;3-担保人;4-紧急联系人;',
  `principal_cust_id` bigint(20) DEFAULT NULL COMMENT '主贷人ID',
  `cust_relation` tinyint(2) DEFAULT NULL COMMENT '与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0-有效;1-无效;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展属性',
  `postcode` varchar(20) DEFAULT NULL COMMENT '邮编',
  `working_years` bigint(20) DEFAULT NULL COMMENT '工作年限',
  `duty` varchar(20) DEFAULT NULL COMMENT '职务',
  PRIMARY KEY (`id`),
  KEY `idx_principal_cust_id` (`principal_cust_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8 COMMENT='贷款客户信息';

DROP TABLE IF EXISTS  `loan_file`;
CREATE TABLE `loan_file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `name` varchar(64) DEFAULT NULL,
  `path` text COMMENT '文件存储路径',
  `type` tinyint(2) DEFAULT NULL COMMENT '文件类型：1-身份证;2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;6-驾驶证;7- 户口本;8- 银行流水;9-结婚证;10-房产证;11-定位照;12-合影照片;13-家访视频; 14-收入证明; 15-面签照; 16-家访照片; 17-车辆照片; 18-其他资料;19-发票;20-合格证/登记证书;21-保单;22-提车合影;',
  `upload_type` tinyint(1) DEFAULT NULL COMMENT '1-常规上传;2-资料增补上传',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0-有效;1-无效;',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=941 DEFAULT CHARSET=utf8 COMMENT='客户贷款文件信息';

DROP TABLE IF EXISTS  `loan_financial_plan`;
CREATE TABLE `loan_financial_plan` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `car_price` decimal(10,2) DEFAULT NULL COMMENT '车辆价格',
  `financial_product_id` bigint(20) DEFAULT NULL COMMENT '金融产品ID',
  `financial_product_name` varchar(64) DEFAULT NULL COMMENT '金融方案名称',
  `bank` varchar(64) DEFAULT NULL COMMENT '银行名称',
  `sign_rate` decimal(10,5) DEFAULT NULL COMMENT '签约利率',
  `loan_amount` decimal(10,2) DEFAULT NULL COMMENT '实际贷款额',
  `loan_time` int(11) DEFAULT NULL COMMENT '贷款期数',
  `down_payment_ratio` decimal(10,5) DEFAULT NULL COMMENT '首付比例',
  `down_payment_money` decimal(16,6) DEFAULT NULL COMMENT '首付额',
  `bank_period_principal` decimal(16,6) DEFAULT NULL COMMENT '银行分期本金',
  `bank_fee` decimal(16,6) DEFAULT NULL COMMENT '银行手续费',
  `principal_interest_sum` decimal(16,6) DEFAULT NULL COMMENT '本息合计',
  `first_month_repay` decimal(16,6) DEFAULT NULL COMMENT '首月还款',
  `each_month_repay` decimal(16,6) DEFAULT NULL COMMENT '每月还款',
  `status` tinyint(1) unsigned DEFAULT NULL COMMENT '状态: 0-有效;1-无效;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8 COMMENT='贷款金融方案表';

DROP TABLE IF EXISTS  `loan_home_visit`;
CREATE TABLE `loan_home_visit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `visit_salesman_id` bigint(20) DEFAULT NULL COMMENT '上门人员ID',
  `visit_date` date DEFAULT NULL COMMENT '上门调查日期',
  `survey_report` varchar(500) DEFAULT NULL COMMENT '调查报告',
  `visit_address` varchar(100) DEFAULT NULL COMMENT '上门地址',
  `files` varchar(1000) DEFAULT NULL COMMENT '上传文件路径详情',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态: 0-有效;1-无效;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='上门家访表';

DROP TABLE IF EXISTS  `loan_info_supplement`;
CREATE TABLE `loan_info_supplement` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '增补单ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '资料增补类型(1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;)',
  `content` varchar(255) DEFAULT NULL COMMENT '资料增补内容',
  `info` varchar(255) DEFAULT NULL COMMENT '资料增补说明',
  `origin_task` varchar(64) DEFAULT NULL COMMENT '增补源头任务节点：从哪个节点发起的增补',
  `initiator_id` bigint(20) DEFAULT NULL COMMENT '发起人ID',
  `initiator_name` varchar(32) DEFAULT NULL COMMENT '发起人姓名',
  `start_time` datetime DEFAULT NULL COMMENT '发起增补时间',
  `supplementer_id` bigint(20) DEFAULT NULL COMMENT '增补人ID',
  `supplementer_name` varchar(32) DEFAULT NULL COMMENT '增补人name',
  `end_time` datetime DEFAULT NULL COMMENT '增补提交时间',
  `status` tinyint(1) DEFAULT '0' COMMENT '增补单状态(默认值0-未执行到此节点;1-已提交;2-未提交;)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='资料增补表';

DROP TABLE IF EXISTS  `loan_order`;
CREATE TABLE `loan_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '贷款业务单号',
  `process_inst_id` varchar(64) DEFAULT NULL COMMENT '关联activiti流程实体ID',
  `loan_base_info_id` bigint(20) DEFAULT NULL COMMENT '贷款基本信息ID',
  `loan_customer_id` bigint(20) DEFAULT NULL COMMENT '主贷人ID',
  `loan_car_info_id` bigint(20) DEFAULT NULL COMMENT '贷款车辆信息ID',
  `loan_financial_plan_id` bigint(20) DEFAULT NULL COMMENT '贷款金融方案ID',
  `loan_home_visit_id` bigint(20) DEFAULT NULL COMMENT '上门家访资料信息ID',
  `material_audit_id` bigint(20) DEFAULT NULL COMMENT '资料审核id',
  `cost_details_id` bigint(20) DEFAULT NULL COMMENT '费用明细id',
  `remit_details_id` bigint(20) DEFAULT NULL COMMENT '打款明细id',
  `apply_license_plate_record_id` bigint(20) DEFAULT NULL COMMENT '上牌记录id',
  `apply_license_plate_deposit_info_id` bigint(20) DEFAULT NULL COMMENT '上牌抵押信息id',
  `current_task_def_key` varchar(255) DEFAULT NULL COMMENT '当前任务节点的taskDefinitionKey',
  `previous_task_def_key` varchar(255) DEFAULT NULL COMMENT '上一个执行任务节点的taskDefinitionKey',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0-有效;1-无效;',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2018032714224093697 DEFAULT CHARSET=utf8 COMMENT='贷款流程业务单(关联关系表)';

DROP TABLE IF EXISTS  `loan_process`;
CREATE TABLE `loan_process` (
  `order_id` bigint(20) unsigned NOT NULL COMMENT '业务单ID',
  `credit_apply` tinyint(1) DEFAULT '0' COMMENT '征信申请状态(1-已提交;2-未提交;3-打回修改;)',
  `credit_apply_verify` tinyint(1) DEFAULT '0' COMMENT '征信申请单审核状态(1-已提交;2-未提交;3-打回修改;)',
  `bank_credit_record` tinyint(1) DEFAULT '0' COMMENT '银行征信录入(1-已提交;2-未提交;3-打回修改;)',
  `social_credit_record` tinyint(1) DEFAULT '0' COMMENT '社会征信录入(1-已提交;2-未提交;3-打回修改;)',
  `loan_apply` tinyint(1) DEFAULT '0' COMMENT '业务申请',
  `visit_verify` tinyint(1) DEFAULT '0' COMMENT '上门调查',
  `telephone_verify` tinyint(1) DEFAULT '0' COMMENT '电审状态(1-已提交;2-未提交;3-打回修改;4-电审专员已审核;5-电审主管已审核;6-电审经理已审核;7总监已审核;)',
  `car_insurance` tinyint(1) DEFAULT '0' COMMENT '车辆保险',
  `apply_license_plate_record` tinyint(1) DEFAULT '0' COMMENT '上牌记录',
  `apply_license_plate_deposit_info` tinyint(1) DEFAULT '0' COMMENT '上牌抵押',
  `install_gps` tinyint(1) DEFAULT '0' COMMENT 'GPS安装',
  `commit_key` tinyint(1) DEFAULT '0' COMMENT '待收钥匙',
  `vehicle_information` tinyint(1) DEFAULT '0' COMMENT '提车资料',
  `material_review` tinyint(1) DEFAULT '0' COMMENT '资料审核',
  `material_print_review` tinyint(1) DEFAULT '0' COMMENT '合同套打',
  `business_review` tinyint(1) DEFAULT '0' COMMENT '业务审批',
  `loan_review` tinyint(1) DEFAULT '0' COMMENT '放款审批',
  `remit_review` tinyint(1) DEFAULT '0' COMMENT '打款确认',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `cancel_task_def_key` varchar(64) DEFAULT NULL COMMENT '弃单任务节点KEY',
  `loan_apply_reject_orgin_task` varchar(64) DEFAULT NULL COMMENT '贷款申请打回来源任务节点KEY（仅当loan_apply节点状态为3时可能[由资料审核打回]有值）',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='贷款进度表(通用：默认值0-未执行到此节点;1-已提交;2-未提交;3-打回修改;)';

DROP TABLE IF EXISTS  `material_audit`;
CREATE TABLE `material_audit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `complete_material_date` date NOT NULL COMMENT '资料齐全日期',
  `rate_type` tinyint(1) NOT NULL COMMENT '手续费收取方式  1:一次性; 2:分期;',
  `is_pledge` tinyint(1) NOT NULL COMMENT '是否抵押 1是 2否',
  `is_guarantee` tinyint(1) NOT NULL COMMENT '是否担保 1 是 2 否',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-有效;1-无效;',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='资料审核';

DROP TABLE IF EXISTS  `menu`;
CREATE TABLE `menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `name` varchar(32) DEFAULT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级菜单ID',
  `uri` varchar(100) DEFAULT NULL COMMENT '菜单路径',
  `level` int(11) DEFAULT NULL COMMENT '菜单等级',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 (0:有效;1:无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='菜单表';

DROP TABLE IF EXISTS  `operation`;
CREATE TABLE `operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `name` varchar(32) DEFAULT NULL COMMENT '操作名称',
  `uri` varchar(100) DEFAULT NULL COMMENT '操作API路径',
  `page_id` bigint(20) DEFAULT NULL COMMENT '操作所属页面ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 (0:有效;1:无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 COMMENT='操作表';

DROP TABLE IF EXISTS  `padding_company`;
CREATE TABLE `padding_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `mnemonic_code` varchar(32) DEFAULT NULL COMMENT '助记码',
  `contact` varchar(32) DEFAULT NULL COMMENT '联系人',
  `tel` varchar(32) DEFAULT NULL COMMENT '手机号',
  `office_phone` varchar(32) NOT NULL COMMENT '办公室电话',
  `fax` varchar(32) NOT NULL COMMENT '传真',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `rate` decimal(10,5) DEFAULT NULL COMMENT '费率',
  `cooperation_policy` text COMMENT '合作政策',
  `bank` varchar(32) NOT NULL COMMENT '开户行',
  `account_name` varchar(32) DEFAULT NULL COMMENT '开户名',
  `bank_account` varchar(32) NOT NULL COMMENT '银行账号',
  `file` varchar(1000) DEFAULT NULL COMMENT '文件上传路径',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展字段',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='垫资公司表';

DROP TABLE IF EXISTS  `page`;
CREATE TABLE `page` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '页面ID',
  `name` varchar(32) DEFAULT NULL COMMENT '页面名称',
  `uri` varchar(500) DEFAULT NULL COMMENT '页面路径',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '页面所属菜单ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 (0:有效;1:无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='页面表';

DROP TABLE IF EXISTS  `partner`;
CREATE TABLE `partner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '合伙人ID',
  `name` varchar(32) NOT NULL COMMENT '团队名称',
  `department_id` bigint(20) NOT NULL COMMENT '对应负责部门ID',
  `leader_name` varchar(32) NOT NULL COMMENT '团队负责人(姓名)',
  `leader_mobile` varchar(32) NOT NULL COMMENT '负责人手机',
  `tel` varchar(32) DEFAULT NULL COMMENT '团队联系电话',
  `fax` varchar(32) DEFAULT NULL COMMENT '团队传真',
  `area_id` bigint(20) DEFAULT NULL COMMENT '区域(城市)',
  `biz_type` tinyint(1) DEFAULT NULL COMMENT '业务来源(1:4S店; 2:综合店; 3:个人)',
  `sign` tinyint(1) DEFAULT NULL COMMENT '是否签约（0:否;1:是）',
  `cooperation_scale` varchar(32) DEFAULT NULL COMMENT '合作规模',
  `exec_rate` decimal(10,5) DEFAULT NULL COMMENT '执行利率',
  `cooperation_insurance_company` varchar(64) DEFAULT NULL COMMENT '合作保险公司',
  `open_bank` varchar(64) NOT NULL COMMENT '开户行',
  `account_name` varchar(32) NOT NULL COMMENT '开户名',
  `bank_account` varchar(32) NOT NULL COMMENT '银行账号',
  `open_bank_two` varchar(64) DEFAULT NULL COMMENT '开户行二',
  `account_name_two` varchar(32) DEFAULT NULL COMMENT '开户名二',
  `bank_account_two` varchar(32) DEFAULT NULL COMMENT '银行账号二',
  `pay_month` tinyint(1) DEFAULT NULL COMMENT '收费是否月结(0:否;1:是)',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '状态（0：有效;1：无效）',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='合伙人表';

DROP TABLE IF EXISTS  `partner_rela_employee`;
CREATE TABLE `partner_rela_employee` (
  `partner_id` bigint(20) NOT NULL COMMENT '合伙人ID',
  `employee_id` bigint(20) NOT NULL COMMENT '(外包)员工ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`partner_id`,`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合伙人-(外包)员工 关联表';

DROP TABLE IF EXISTS  `product_rate`;
CREATE TABLE `product_rate` (
  `prod_id` bigint(20) NOT NULL COMMENT '金融产品ID',
  `bank_rate` decimal(10,6) NOT NULL COMMENT '银行费率',
  `loan_time` int(11) NOT NULL COMMENT '按揭期数',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(2000) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`prod_id`,`bank_rate`,`loan_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `remit_details`;
CREATE TABLE `remit_details` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `beneficiary_bank` varchar(20) NOT NULL COMMENT '收款银行',
  `beneficiary_account` varchar(20) NOT NULL COMMENT '收款账户',
  `beneficiary_account_number` varchar(20) NOT NULL COMMENT '收款账号',
  `remit_amount` decimal(10,2) NOT NULL COMMENT '打款金额',
  `return_rate_amount` decimal(10,2) NOT NULL COMMENT '返利金额',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-有效;1-无效;',
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='打款明细';

DROP TABLE IF EXISTS  `repayment_record`;
CREATE TABLE `repayment_record` (
  `biz_order` bigint(20) DEFAULT NULL COMMENT '业务ID',
  `user_name` varchar(20) DEFAULT NULL COMMENT '客户姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `repay_card_id` varchar(20) NOT NULL COMMENT '还款卡号',
  `optimal_return` decimal(20,4) DEFAULT NULL COMMENT '最优还款额',
  `min_payment` decimal(20,4) DEFAULT NULL COMMENT '最低还款额',
  `past_due` decimal(20,4) DEFAULT NULL COMMENT '逾期金额',
  `current_overdue_times` int(4) DEFAULT NULL COMMENT '当前逾期期数',
  `cumulative_overdue_times` int(4) DEFAULT NULL COMMENT '累计逾期期数',
  `card_balance` decimal(20,4) DEFAULT NULL COMMENT '卡余额',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  `feature` varchar(2000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id_card`,`repay_card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `user_group`;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户组ID',
  `name` varchar(32) DEFAULT NULL COMMENT '用户组名称',
  `department_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `info` text COMMENT '用户组说明',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  `feature` varchar(5000) DEFAULT NULL COMMENT '扩展属性',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态（0：有效;1：无效）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='用户组表（角色表）';

DROP TABLE IF EXISTS  `user_group_rela_area`;
CREATE TABLE `user_group_rela_area` (
  `user_group_id` bigint(20) NOT NULL COMMENT '用户组ID',
  `area_id` bigint(20) NOT NULL COMMENT '区域ID',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`user_group_id`,`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组-区域(城市) 关联表';

DROP TABLE IF EXISTS  `user_group_rela_area_auth`;
CREATE TABLE `user_group_rela_area_auth` (
  `user_group_id` bigint(20) NOT NULL COMMENT '用户组ID',
  `auth_id` bigint(20) NOT NULL COMMENT '权限ID',
  `area_id` bigint(20) NOT NULL COMMENT '区域ID(限制权限可使用的城市) ',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modify` datetime DEFAULT NULL,
  PRIMARY KEY (`user_group_id`,`auth_id`,`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组-权限-区域(城市)关系表';

SET FOREIGN_KEY_CHECKS = 1;

/* FUNCTIONS */;
DROP FUNCTION IF EXISTS `bankFee_1`;
DELIMITER $$
CREATE FUNCTION `bankFee_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100))*100))/100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `bankFee_2`;
DELIMITER $$
CREATE FUNCTION `bankFee_2`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `bankFee_3`;
DELIMITER $$
CREATE FUNCTION `bankFee_3`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `bankFee_4`;
DELIMITER $$
CREATE FUNCTION `bankFee_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `bankFee_6`;
DELIMITER $$
CREATE FUNCTION `bankFee_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `bankFee_7`;
DELIMITER $$
CREATE FUNCTION `bankFee_7`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `bankFee_8`;
DELIMITER $$
CREATE FUNCTION `bankFee_8`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `calculateStagingRatio`;
DELIMITER $$
CREATE FUNCTION `calculateStagingRatio`(formula_id int(11),loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthBankFee_4`;
DELIMITER $$
CREATE FUNCTION `eachMonthBankFee_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (FLOOR((loanAmt)*(bankBaseRate/100)/year))+(FLOOR((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt))*(bankBaseRate/100)/year));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_1`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/(year))+((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100))/year);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_2`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_2`( loanAmt decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = FLOOR((loanAmt/year)+((loanAmt*(bankBaseRate/100))/year));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_3`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_3`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = FLOOR((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/year)+(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*((bankBaseRate/100)/year)));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_4`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (FLOOR(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(1+bankBaseRate/100)/year));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_5`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_5`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (FLOOR(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(1+bankBaseRate/100)/year));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_6`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = FLOOR((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)/year)+FLOOR(CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100/year);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_7`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_7`( loanAmt decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = FLOOR(loanAmt/year)+FLOOR((loanAmt*(bankBaseRate/100))/year);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `eachMonthRepay_8`;
DELIMITER $$
CREATE FUNCTION `eachMonthRepay_8`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = FLOOR((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)/year)+FLOOR(CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100/year);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstMonthBankFee_4`;
DELIMITER $$
CREATE FUNCTION `firstMonthBankFee_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((loanAmt)*(bankBaseRate/100))-((FLOOR((loanAmt)*(bankBaseRate/100)/year))*(year-1)))+(((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt))*(bankBaseRate/100))-((FLOOR((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt))*(bankBaseRate/100)/year))*(year-1)));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_1`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/(year))+((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100))/year))+100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_2`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_2`( loanAmt decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (loanAmt*(1+bankBaseRate/100))- (FLOOR((loanAmt*(1+bankBaseRate/100))/year)*(year-1));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_3`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_3`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(1+bankBaseRate/100))-((FLOOR((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(1+bankBaseRate/100))/year))*(year-1));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_4`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100*(1+bankBaseRate/100)-((FLOOR(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(1+bankBaseRate/100)/year))*(year-1));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_5`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_5`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100*(1+bankBaseRate/100)-((FLOOR(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(1+bankBaseRate/100)/year))*(year-1));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_6`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)-(year-1)*FLOOR(CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100/year))*100)+(((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)-((year-1)*FLOOR((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)/year)))*100))/100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_7`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_7`( loanAmt decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = FLOOR(loanAmt-FLOOR(FLOOR(loanAmt/year)*(year-1)))+FLOOR(loanAmt*bankBaseRate/100)-(FLOOR(FLOOR(loanAmt*bankBaseRate/100)/year)*(year-1));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `firstRepayment_8`;
DELIMITER $$
CREATE FUNCTION `firstRepayment_8`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)-(year-1)*FLOOR(CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100/year))*100)+(((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)-((year-1)*FLOOR((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)/year)))*100))/100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `loanInterest_1`;
DELIMITER $$
CREATE FUNCTION `loanInterest_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `loanInterest_3`;
DELIMITER $$
CREATE FUNCTION `loanInterest_3`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `loanInterest_4`;
DELIMITER $$
CREATE FUNCTION `loanInterest_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `loanInterest_6`;
DELIMITER $$
CREATE FUNCTION `loanInterest_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt);
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `loanToValueRatio_1`;
DELIMITER $$
CREATE FUNCTION `loanToValueRatio_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)+(CEIL(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100))))/(carPrice))*100))/10;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `periodPrincipal_1`;
DELIMITER $$
CREATE FUNCTION `periodPrincipal_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `periodPrincipal_3`;
DELIMITER $$
CREATE FUNCTION `periodPrincipal_3`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `periodPrincipal_4`;
DELIMITER $$
CREATE FUNCTION `periodPrincipal_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `periodPrincipal_6`;
DELIMITER $$
CREATE FUNCTION `periodPrincipal_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `principalEachMonthRepay_4`;
DELIMITER $$
CREATE FUNCTION `principalEachMonthRepay_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (FLOOR(loanAmt/year))+(FLOOR((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt))/year));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `principalFirstMonthRepay_4`;
DELIMITER $$
CREATE FUNCTION `principalFirstMonthRepay_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),year decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (loanAmt-((FLOOR(loanAmt/year))*(year-1)))+((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt))-((FLOOR((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)-(loanAmt))/year))*(year-1)));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_1`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_2`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_2`( loanAmt decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (loanAmt/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_3`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_3`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_4`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_5`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_5`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (loanAmt/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_6`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_7`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_7`( loanAmt decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (loanAmt/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stagingRatio_8`;
DELIMITER $$
CREATE FUNCTION `stagingRatio_8`( loanAmt decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (loanAmt/carPrice)*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `totalRepayment_1`;
DELIMITER $$
CREATE FUNCTION `totalRepayment_1`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)+(CEIL(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100)));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `totalRepayment_4`;
DELIMITER $$
CREATE FUNCTION `totalRepayment_4`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (CEIL((((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)+(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100)))/100))*100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `totalRepayment_6`;
DELIMITER $$
CREATE FUNCTION `totalRepayment_6`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)*100)+((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*100))/100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `totalRepayment_7`;
DELIMITER $$
CREATE FUNCTION `totalRepayment_7`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = ((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)+(CEIL(((CEIL(((loanAmt*(1+exeRate/100))/(1+bankBaseRate/100))/100))*100)*(bankBaseRate/100)));
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `totalRepayment_8`;
DELIMITER $$
CREATE FUNCTION `totalRepayment_8`( loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6)) RETURNS decimal(20,6)
BEGIN
            -- 定义一个变量 result，类型为 decimal，长度是20，
            DECLARE result decimal(20,6);
            SET result = (((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*(bankBaseRate/100)*100)+((CEIL((loanAmt*(1+exeRate/100)/(1+bankBaseRate/100))/100)*100)*100))/100;
			RETURN result;

END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `useCalculateStagingRatio`;
DELIMITER $$
CREATE FUNCTION `useCalculateStagingRatio`(formula_id int(11),loanAmt decimal(20,6),exeRate decimal(20,6),bankBaseRate decimal(20,6),carPrice decimal(20,6)) RETURNS decimal(20,6)
BEGIN
				  DECLARE result decimal(20,6);

							IF(formula_id=1) THEN
								SET result =  stagingRatio_1(loanAmt,exeRate,bankBaseRate,carPrice);
							ELSEIF(formula_id=2) THEN
								SET result =  stagingRatio_2(loanAmt,carPrice);
							ELSEIF(formula_id=3) THEN
								SET result =  stagingRatio_3(loanAmt,exeRate,bankBaseRate,carPrice);
							ELSEIF(formula_id=4) THEN
								SET result =  stagingRatio_4(loanAmt,exeRate,bankBaseRate,carPrice);
							ELSEIF(formula_id=5) THEN
								SET result =  stagingRatio_5(loanAmt,exeRate,bankBaseRate,carPrice);
							ELSEIF(formula_id=6) THEN
								SET result =  stagingRatio_6(loanAmt,exeRate,bankBaseRate,carPrice);
							ELSEIF(formula_id=7) THEN
								SET result =  stagingRatio_7(loanAmt,carPrice);
							ELSEIF(formula_id=8) THEN
								SET result =  stagingRatio_8(loanAmt,carPrice);
							ELSE
								SET result =  0;
							END IF;
					
					RETURN result;
END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `useFileTypeCastName`;
DELIMITER $$
CREATE FUNCTION `useFileTypeCastName`( type TINYINT(2)) RETURNS varchar(20) CHARSET utf8
BEGIN
  /**
	文件类型：1-身份证;2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;6-驾驶证;7- 户口本;8- 银行流水;9-结婚证;10-房产证;11-定位照;12-合影照片;13-家访视频; 14-收入证明; 15-面签照; 16-家访照片; 17-车辆照片; 18-其他资料;
	*/
	DECLARE result VARCHAR(20);
	
	  IF type = 1 then   set result = '身份证';
		ELSEIF type =  2 then   set result = '身份证正面';
		ELSEIF type =  3 then   set result = '身份证反面';
		ELSEIF type =  4 then   set result = '授权书';
		ELSEIF type =  5 then   set result = '授权书签字照';
		ELSEIF type =  6 then   set result = '驾驶证';
		ELSEIF type =  7 then   set result = '户口本';
		ELSEIF type =  8 then   set result = '银行流水';
		ELSEIF type =  9 then   set result = '结婚证';
		ELSEIF type =  10 then  set result = '房产证';
		ELSEIF type =  11 then  set result = '定位照';
		ELSEIF type =  12 then  set result = '合影照片';
		ELSEIF type =  13 then  set result = '家访视频';
		ELSEIF type =  14 then  set result = '收入证明';
		ELSEIF type =  15 then  set result = '面签照';
		ELSEIF type =  16 then  set result = '家访照片';
		ELSEIF type =  17 then  set result = '车辆照片';
		ELSEIF type =  18 then  set result = '其他资料';
	  ELSE set result = '其他'; END IF;
	return result;
END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `useGetCarName`;
DELIMITER $$
CREATE FUNCTION `useGetCarName`( car_detail_id BIGINT(20)) RETURNS varchar(100) CHARSET utf8
BEGIN
		DECLARE brand varchar(20);
		DECLARE model varchar(20);
		DECLARE detail varchar(20);
		
		set brand = (select name from car_brand where id = (select brand_id from car_model where id = (select model_id from car_detail where id = car_detail_id)));
	  set model = (select name from car_model where id = (select model_id from car_detail where id = car_detail_id));
		set detail = (select name from car_detail where id = car_detail_id);
		
		return CONCAT(case when brand = '' or brand is null then '' else brand end,'-',case when model = '' or model is null then '' else model end,'-',case when detail = '' or detail is null then '' else detail end);
END
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `useGetSmallCarName`;
DELIMITER $$
CREATE FUNCTION `useGetSmallCarName`( car_detail_id BIGINT(20)) RETURNS varchar(100) CHARSET utf8
BEGIN
		DECLARE brand varchar(20);
		DECLARE model varchar(20);
		DECLARE detail varchar(20);
		
		set brand = (select name from car_brand where id = (select brand_id from car_model where id = (select model_id from car_detail where id = car_detail_id)));
	  set model = (select name from car_model where id = (select model_id from car_detail where id = car_detail_id));
		
		return CONCAT(case when brand = '' or brand is null then '' else brand end,'-',case when model = '' or model is null then '' else model end);
END
$$
DELIMITER ;

