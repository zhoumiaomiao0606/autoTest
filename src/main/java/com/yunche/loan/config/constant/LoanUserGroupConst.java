package com.yunche.loan.config.constant;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/3/22
 */
public class LoanUserGroupConst {
    /**
     * [0, 10W]
     */
    public static final String USER_GROUP_TELEPHONE_VERIFY_COMMISSIONER = "电审专员";

    public static final String USER_GROUP_TELEPHONE_VERIFY_COMMISSIONER_Harbin = "哈尔滨电审专员";
    /**
     * [10W+1, 30W]
     */
    public static final String USER_GROUP_TELEPHONE_VERIFY_LEADER = "电审主管";

    public static final String USER_GROUP_TELEPHONE_VERIFY_LEADER_Harbin = "哈尔滨电审主管";
    /**
     * [30W+1, 50W]
     */
    public static final String USER_GROUP_TELEPHONE_VERIFY_MANAGER = "电审经理";

    public static final String USER_GROUP_TELEPHONE_VERIFY_MANAGER_Harbin = "哈尔滨电审经理";
    /**
     * [50W+1, +∞)
     */
    public static final String USER_GROUP_DIRECTOR = "总监";

    public static final String USER_GROUP_DIRECTOR_Harbin = "哈尔滨总监";

    /**
     * 电审专员等级
     */
    public static final Byte LEVEL_TELEPHONE_VERIFY_COMMISSIONER = 4;
    /**
     * 电审主管等级
     */
    public static final Byte LEVEL_TELEPHONE_VERIFY_LEADER = 5;
    /**
     * 电审经理等级
     */
    public static final Byte LEVEL_TELEPHONE_VERIFY_MANAGER = 6;
    /**
     * 总监
     */
    public static final Byte LEVEL_DIRECTOR = 7;

    /**
     * 所有可电审的角色
     */
    public static List<String> USER_GROUP_TELEPHONE_VERIFY_LIST = Lists.newArrayList(USER_GROUP_TELEPHONE_VERIFY_COMMISSIONER,
            USER_GROUP_TELEPHONE_VERIFY_LEADER, USER_GROUP_TELEPHONE_VERIFY_MANAGER, USER_GROUP_DIRECTOR);


    /**
     * 电审 角色-等级 映射关系
     */
    public static Map<String, Byte> TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP = Maps.newHashMap();

    static {
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_TELEPHONE_VERIFY_COMMISSIONER, LEVEL_TELEPHONE_VERIFY_COMMISSIONER);
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_TELEPHONE_VERIFY_LEADER, LEVEL_TELEPHONE_VERIFY_LEADER);
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_TELEPHONE_VERIFY_MANAGER, LEVEL_TELEPHONE_VERIFY_MANAGER);
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_DIRECTOR, LEVEL_DIRECTOR);

        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_TELEPHONE_VERIFY_COMMISSIONER_Harbin, LEVEL_TELEPHONE_VERIFY_COMMISSIONER);
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_TELEPHONE_VERIFY_LEADER_Harbin, LEVEL_TELEPHONE_VERIFY_LEADER);
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_TELEPHONE_VERIFY_MANAGER_Harbin, LEVEL_TELEPHONE_VERIFY_MANAGER);
        TELEPHONE_VERIFY_USER_GROUP_LEVEL_MAP.put(USER_GROUP_DIRECTOR_Harbin, LEVEL_DIRECTOR);
    }
}
