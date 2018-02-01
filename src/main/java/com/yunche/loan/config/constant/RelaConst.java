package com.yunche.loan.config.constant;

/**
 * 关联查询
 *
 * @author liuzhe
 * @date 2018/2/1
 */
public class RelaConst {
    /**
     * 仅查询当前用户已授权operation列表
     */
    public static final Byte GET_ALL_HAS_BIND = 1;
    /**
     * 仅查询当前用户未授权operation列表
     */
    public static final Byte GET_ALL_NOT_BIND = 2;
    /**
     * 查询所有(带是否已选中状态)
     */
    public static final Byte GET_ALL_AND_HAS_SELECTED_STATE = 3;
}
