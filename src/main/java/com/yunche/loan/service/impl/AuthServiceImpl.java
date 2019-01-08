package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.domain.query.AuthQuery;
import com.yunche.loan.domain.entity.MenuDO;
import com.yunche.loan.domain.entity.OperationDO;
import com.yunche.loan.domain.entity.PageDO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.PageVO;
import com.yunche.loan.service.AuthService;
import com.yunche.loan.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AuthConst.OPERATION;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;

/**
 * @author liuzhe
 * @date 2018/1/29
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthDOMapper authDOMapper;
    @Autowired
    private MenuDOMapper menuDOMapper;
    @Autowired
    private PageDOMapper pageDOMapper;
    @Autowired
    private OperationDOMapper operationDOMapper;
    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ActivitiCache activitiCache;

    @Autowired
    private ReportPowerDOMapper reportPowerDOMapper;


    @Override
    public ResultBean<List<CascadeVO>> listMenu() {
        // getAll
        List<MenuDO> menuDOS = menuDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<MenuDO>> parentIdDOMap = getParentIdDOSMapping(menuDOS);

        // 分级递归解析
        List<CascadeVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        return ResultBean.ofSuccess(topLevelList);
    }

    @Override
    public ResultBean<List<PageVO>> listOperation(AuthQuery query) {
        Preconditions.checkNotNull(query.getStatus(), "状态不能为空");

        // 根据menuId填充所有子menuId(含自身)
        getAndSetMenuIdList(query);

        // 预加载：
        // 操作ID-操作实体 映射关系： operation_id —— OperationDO 映射
        Map<Long, OperationDO> idOperationDOMap = getIdOperationDOMapping();
        // 页面ID-页面实体 映射关系： page_id —— PageDO 映射
        Map<Long, PageDO> idPageDOMap = getIdPageDOMapping();

        // queryAll   operation ID列表
        List<Long> allOperationIdListByCondition = operationDOMapper.queryAllOperationIdList(query);
        // 已绑定    operations  ID列表
        List<Long> hasBindOperationIdList = userGroupRelaAreaAuthDOMapper.getHasBindAuthEntityIdListByUserGroupIdAndType(query.getUserGroupId(), OPERATION);

        // 获取映射： page - operationDO列表
        Map<Long, List<OperationDO>> pageOperationListMap = getPageOperationListMapping(allOperationIdListByCondition, idOperationDOMap);

        // 分页,并返回结果
        return execPagingListOperation(pageOperationListMap, idPageDOMap, hasBindOperationIdList, query);
    }

    @Override
    public ResultBean<List<PageVO>> listBindOperation(AuthQuery query) {
        Preconditions.checkNotNull(query.getUserGroupId(), "用户组ID不能为空");
        Preconditions.checkNotNull(query.getStatus(), "状态不能为空");

        // 预加载：
        // 操作ID-操作实体 映射关系： operation_id —— OperationDO 映射
        Map<Long, OperationDO> idOperationDOMap = getIdOperationDOMapping();
        // 页面ID-页面实体 映射关系： page_id —— PageDO 映射
        Map<Long, PageDO> idPageDOMap = getIdPageDOMapping();


        // queryAll  operations  ID列表
        List<Long> allOperationIdListByCondition = operationDOMapper.queryAllOperationIdList(query);

        // 已绑定    operations  ID列表
        List<Long> hasBindOperationIdList = userGroupRelaAreaAuthDOMapper.getHasBindAuthEntityIdListByUserGroupIdAndType(query.getUserGroupId(), OPERATION);
        List<Long> hasBindOperationIdListByCondition = allOperationIdListByCondition.stream()
                .distinct()
                .filter(Objects::nonNull)
                .map(e -> {
                    if (hasBindOperationIdList.contains(e)) {
                        return e;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        // 获取已绑定的： page - operationDO列表 映射关系
        Map<Long, List<OperationDO>> pageOperationListMap = getPageOperationListMapping(hasBindOperationIdListByCondition, idOperationDOMap);

        // 分页,并返回结果
        return execPagingListOperation(pageOperationListMap, idPageDOMap, hasBindOperationIdList, query);
    }

    @Override
    public ResultBean<List<PageVO>> listUnbindOperation(AuthQuery query) {
        Preconditions.checkNotNull(query.getUserGroupId(), "用户组ID不能为空");
        Preconditions.checkNotNull(query.getStatus(), "状态不能为空");

        // 预加载：
        // 操作ID-操作实体 映射关系： operation_id —— OperationDO 映射
        Map<Long, OperationDO> idOperationDOMap = getIdOperationDOMapping();
        // 页面ID-页面实体 映射关系： page_id —— PageDO 映射
        Map<Long, PageDO> idPageDOMap = getIdPageDOMapping();

        // getAll   operation ID列表
        List<Long> allOperationIdListByCondition = operationDOMapper.queryAllOperationIdList(query);
        // 已绑定    operations  ID列表
        List<Long> hasBindOperationIdList = userGroupRelaAreaAuthDOMapper.getHasBindAuthEntityIdListByUserGroupIdAndType(query.getUserGroupId(), OPERATION);
        // 未绑定    operations  ID列表
        allOperationIdListByCondition.removeAll(hasBindOperationIdList);
        List<Long> unBindOperationIdListByCondition = allOperationIdListByCondition.stream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        // 获取未绑定的： page - operationDO列表 映射关系
        Map<Long, List<OperationDO>> pageOperationListMap = getPageOperationListMapping(unBindOperationIdListByCondition, idOperationDOMap);

        // 分页,并返回结果
        return execPagingListOperation(pageOperationListMap, idPageDOMap, hasBindOperationIdList, query);
    }

    @Override
    public ResultBean<Map<String, Boolean>> listMenu_() {

        // 节点-是否有权  Map
        Map<String, Boolean> taskAuthMap = Maps.newHashMap();

        // LoginUser 拥有的所有角色权限
        Set<String> loginUserHasUserGroups = permissionService.getLoginUserHasUserGroups();

        // 管理员
        if (!CollectionUtils.isEmpty(loginUserHasUserGroups) && loginUserHasUserGroups.contains("管理员")) {
            // 授予所有节点权限
            return authAllRole(taskAuthMap);
        }

        // 节点-角色列表  Map
        Map<String, List<String>> taskRolesMap = activitiCache.getNodeRolesMap();

        if (!CollectionUtils.isEmpty(taskRolesMap)) {

            // 遍历 节点-角色列表
            taskRolesMap.forEach((k, v) -> {

                List<String> candidateGroups = v;

                // 需要权限
                if (!CollectionUtils.isEmpty(candidateGroups)) {

                    // 无任何角色
                    if (CollectionUtils.isEmpty(loginUserHasUserGroups)) {
                        taskAuthMap.put(k, false);
                    }

                    // 有角色
                    candidateGroups.stream()
                            .filter(e -> StringUtils.isNotBlank(e))
                            .forEach(e -> {

                                // 包含 -> 有权操作
                                if (loginUserHasUserGroups.contains(e)) {

                                    // [资料流转]节点
                                    if (k.startsWith("usertask_data_flow_")) {
                                        taskAuthMap.put(DATA_FLOW.getCode(), true);
                                    } else {
                                        // 普通节点
                                        taskAuthMap.put(k, true);
                                    }
                                }
                            });


                    if (null == taskAuthMap.get(k)) {
                        taskAuthMap.put(k, false);
                    }

                }

                // candidateGroups为空  -> 不需要权限
                else if (!BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(k)
                        && !LOAN_APPLY_VISIT_VERIFY_FILTER.getCode().equals(k)
                        && !REMIT_REVIEW_FILTER.getCode().equals(k)
                        && !DATA_FLOW_MORTGAGE_P2C_NEW_FILTER.getCode().equals(k)) {

                    taskAuthMap.put(k, true);
                }

            });

            taskAuthMap.put("configure_center", false);
        }

        return ResultBean.ofSuccess(taskAuthMap);
    }

    @Override
    public ResultBean<Map<String, Boolean>> listReport() {
        // LoginUser 拥有的所有角色权限
        Map<String, Boolean> taskAuthMap = Maps.newHashMap();
        Set<String> loginUserHasUserGroups = permissionService.getLoginUserHasUserGroups();
        List<String> total = new ArrayList<>();
        total.add("bank_dep");
        total.add("tel_exam_dep");
        total.add("channel_dep");
        total.add("finance_dep");
        total.add("mortgage_dep");
        total.add("after_loan");
        if(loginUserHasUserGroups == null){
            for(String s:total){
                taskAuthMap.put(s,false);
            }
        }else{
            List<String> list = reportPowerDOMapper.selectPointByGroupName(loginUserHasUserGroups);
            if(list !=null){
                HashSet h = new HashSet(list);
                list.clear();
                list.addAll(h);
                for(String s:list){
                    taskAuthMap.put(s,true);
                }
                for(String s:total){
                    if(!taskAuthMap.containsKey(s)){
                        taskAuthMap.put(s,false);
                    }
                }
            }else{
                for(String s:total){
                    taskAuthMap.put(s,false);
                }
            }
        }

        return ResultBean.ofSuccess(taskAuthMap);
    }

    /**
     * 授予所有节点权限
     *
     * @param taskAuthMap
     * @return
     */
    private ResultBean<Map<String, Boolean>> authAllRole(Map<String, Boolean> taskAuthMap) {
        // configure_center
        taskAuthMap.put("configure_center", true);
        // task
        for (LoanProcessEnum k : LoanProcessEnum.values()) {
            taskAuthMap.put(k.getCode(), true);
        }

        return ResultBean.ofSuccess(taskAuthMap);
    }

    /**
     * 执行分页，并返回ResultBean
     *
     * @param pageOperationListMap
     * @param idPageDOMap
     * @param hasBindOperationIdList
     * @param query
     * @return
     */
    private ResultBean<List<PageVO>> execPagingListOperation(Map<Long, List<OperationDO>> pageOperationListMap, Map<Long, PageDO> idPageDOMap, List<Long> hasBindOperationIdList, AuthQuery query) {
        if (!CollectionUtils.isEmpty(pageOperationListMap)) {

            // 分页截取
            Map<Long, List<OperationDO>> pagingPageOperationListMap = getPagingPageOperationListMapping(query.getStartRow(),
                    query.getPageSize(), pageOperationListMap);

            if (!CollectionUtils.isEmpty(pagingPageOperationListMap)) {

                List<PageVO> pageVOList = Lists.newArrayList();

                pagingPageOperationListMap.forEach((pageId, operationList) -> {

                    // fillPage
                    PageVO pageVO = new PageVO();
                    PageDO pageDO = idPageDOMap.get(pageId);
                    BeanUtils.copyProperties(pageDO, pageVO);

                    // fillOperationList
                    fillOperations(operationList, pageVO, hasBindOperationIdList);

                    // fillMenuList
                    fillMenu(pageDO.getMenuId(), pageVO);

                    pageVOList.add(pageVO);
                });

                return ResultBean.ofSuccess(pageVOList, pageOperationListMap.size(), query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, pageOperationListMap.size(), query.getPageIndex(), query.getPageSize());
    }

    /**
     * fillMenu
     *
     * @param menuId
     * @param pageVO
     */
    private void fillMenu(Long menuId, PageVO pageVO) {
        MenuDO menuDO = menuDOMapper.selectByPrimaryKey(menuId, VALID_STATUS);
        if (null != menuDO) {
            PageVO.Menu menu = new PageVO.Menu();
            BeanUtils.copyProperties(menuDO, menu);

            // 递归填充父级菜单
            fillParentMenu(menu, pageVO, 10);
        }

    }

    /**
     * fillOperations
     *
     * @param operations
     * @param pageVO
     * @param allHasAuthOperationIdList
     */
    private void fillOperations(List<OperationDO> operations, PageVO pageVO, List<Long> allHasAuthOperationIdList) {

        if (!CollectionUtils.isEmpty(operations)) {

            List<PageVO.Operation> operationList = operations.parallelStream()
                    .filter(Objects::nonNull)
                    .map(o -> {

                        PageVO.Operation operation = new PageVO.Operation();
                        BeanUtils.copyProperties(o, operation);

                        // 检验并设置是否已授权
                        checkAndSetHasAuth(operation, allHasAuthOperationIdList);

                        return operation;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(PageVO.Operation::getId))
                    .collect(Collectors.toList());

            pageVO.setOperations(operationList);
        }
    }

    /**
     * fillOperations
     *
     * @param operations
     * @param pageVO
     */
    private void fillOperations(List<OperationDO> operations, PageVO pageVO) {

        if (!CollectionUtils.isEmpty(operations)) {

            List<PageVO.Operation> operationList = operations.parallelStream()
                    .filter(Objects::nonNull)
                    .map(o -> {

                        PageVO.Operation operation = new PageVO.Operation();
                        BeanUtils.copyProperties(o, operation);

                        return operation;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(PageVO.Operation::getId))
                    .collect(Collectors.toList());

            pageVO.setOperations(operationList);
        }
    }


    /**
     * 根据operation ID列表 反推page   并获取绑定/未绑定的： page - operationDO列表 映射关系
     *
     * @param operationIdList
     * @param idOperationDOMap
     * @return
     */
    private Map<Long, List<OperationDO>> getPageOperationListMapping(List<Long> operationIdList, Map<Long, OperationDO> idOperationDOMap) {

        Map<Long, List<OperationDO>> pageIdOperationListMap = Maps.newConcurrentMap();

        operationIdList.stream()
                .forEach(id -> {

                    OperationDO operationDO = idOperationDOMap.get(id);
                    Long pageId = operationDO.getPageId();

                    if (!pageIdOperationListMap.containsKey(pageId)) {
                        pageIdOperationListMap.put(pageId, Lists.newArrayList(operationDO));
                    } else {
                        pageIdOperationListMap.get(pageId).add(operationDO);
                    }

                });

        return pageIdOperationListMap;
    }

    /**
     * 分页截取
     *
     * @param startRow
     * @param pageSize
     * @param pageOperationListMapping
     * @return
     */
    private Map<Long, List<OperationDO>> getPagingPageOperationListMapping(Integer startRow, Integer pageSize,
                                                                           Map<Long, List<OperationDO>> pageOperationListMapping) {

        int fromIndex = 0;
        int toIndex = 0;
        int totalNum = pageOperationListMapping.size();

        if (startRow > totalNum) {
            return null;
        } else {
            fromIndex = startRow;
        }

        if (pageSize + startRow > totalNum) {
            toIndex = totalNum;
        } else {
            toIndex = pageSize + startRow;
        }

        // ID大小排序
        List<Long> pageIdList = pageOperationListMapping.keySet().parallelStream()
                .sorted()
                .collect(Collectors.toList());

        // pageID截取
        List<Long> pagingPageIdList = pageIdList.subList(fromIndex, toIndex);

        // 映射截取
        Map<Long, List<OperationDO>> pagingPageOperationListMapping = Maps.newHashMap();
        pagingPageIdList.parallelStream()
                .forEach(pageId -> {

                    List<OperationDO> operationDOList = pageOperationListMapping.get(pageId);
                    pagingPageOperationListMapping.put(pageId, operationDOList);
                });

        return pagingPageOperationListMapping;
    }

    /**
     * 根据menuId填充所有子menuId(含自身)
     *
     * @param query
     */
    private void getAndSetMenuIdList(AuthQuery query) {
        // getAllChildMenuId
        List<Long> allChildMenuId = getAllChildMenuId(query.getMenuId());
        allChildMenuId.removeAll(Collections.singleton(null));
        // set
        query.setMenuIdList(allChildMenuId);
    }

    /**
     * 获取所有子菜单(含自身)ID
     *
     * @param parentMenuId
     * @return
     */
    private List<Long> getAllChildMenuId(Long parentMenuId) {
        // getAll
        List<MenuDO> menuDOS = menuDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<MenuDO>> parentIdDOMap = getParentIdDOSMapping(menuDOS);

        // 递归填充子菜单ID
        List<Long> childMenuIdList = Lists.newArrayList(parentMenuId);
        fillAllChildMenuId(parentMenuId, childMenuIdList, parentIdDOMap);

        return childMenuIdList;
    }

    /**
     * 递归填充子菜单ID
     *
     * @param parentMenuId
     * @param childMenuIdList
     * @param parentIdDOMap
     */
    private void fillAllChildMenuId(Long parentMenuId, List<Long> childMenuIdList, Map<Long, List<MenuDO>> parentIdDOMap) {
        if (null == parentMenuId) {
            return;
        }

        List<MenuDO> childMenuDOS = parentIdDOMap.get(parentMenuId);
        if (!CollectionUtils.isEmpty(childMenuDOS)) {

            childMenuDOS.parallelStream()
                    .filter(child -> null != child && null != child.getId())
                    .forEach(child -> {
                        // add
                        childMenuIdList.add(child.getId());
                        // 递归填充子ID列表
                        fillAllChildMenuId(child.getId(), childMenuIdList, parentIdDOMap);
                    });
        }
    }

    /**
     * 检验并设置是否已授权
     *
     * @param operation
     * @param allHasAuthOperationIdList
     */
    private void checkAndSetHasAuth(PageVO.Operation operation, List<Long> allHasAuthOperationIdList) {
        if (!CollectionUtils.isEmpty(allHasAuthOperationIdList)) {
            if (allHasAuthOperationIdList.contains(operation.getId())) {
                operation.setSelected(true);
            } else {
                operation.setSelected(false);
            }
        } else {
            operation.setSelected(false);
        }
    }

    /**
     * 填充所属菜单
     *
     * @param menuDO
     * @param pageVO
     */
    private void fillMenu(MenuDO menuDO, PageVO pageVO) {
        if (null != menuDO) {
            PageVO.Menu menu = new PageVO.Menu();
            BeanUtils.copyProperties(menuDO, menu);

            // 递归填充父级菜单
            fillParentMenu(menu, pageVO, 10);
        }
    }

    /**
     * 递归填充父级菜单
     *
     * @param childMenu
     * @param pageVO
     */
    private void fillParentMenu(PageVO.Menu childMenu, PageVO pageVO, Integer limit) {
        limit--;
        if (limit < 0) {
            return;
        }
        if (null != childMenu.getParentId()) {
            MenuDO parentMenuDO = menuDOMapper.selectByPrimaryKey(childMenu.getParentId(), VALID_STATUS);
            if (null != parentMenuDO) {
                PageVO.Menu parentMenu = new PageVO.Menu();
                BeanUtils.copyProperties(parentMenuDO, parentMenu);
                parentMenu.setChildMenu(childMenu);
                fillParentMenu(parentMenu, pageVO, limit);
            }
        } else {
            // null时为最顶级
            pageVO.setMenu(childMenu);
        }
    }

    /**
     * parentId - DOS 映射
     *
     * @param menuDOS
     * @return
     */
    private Map<Long, List<MenuDO>> getParentIdDOSMapping(List<MenuDO> menuDOS) {
        if (CollectionUtils.isEmpty(menuDOS)) {
            return null;
        }
        Map<Long, List<MenuDO>> parentIdDOMap = Maps.newHashMap();
        menuDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Long parentId = e.getParentId();
                    // 为null,用-1标记
                    parentId = null == parentId ? -1L : parentId;
                    if (!parentIdDOMap.containsKey(parentId)) {
                        parentIdDOMap.put(parentId, Lists.newArrayList(e));
                    } else {
                        parentIdDOMap.get(parentId).add(e);
                    }

                });

        return parentIdDOMap;
    }

    /**
     * 分级递归解析
     *
     * @param parentIdDOMap
     * @return
     */
    private List<CascadeVO> parseLevelByLevel(Map<Long, List<MenuDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<MenuDO> menuDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(menuDOS)) {
                List<CascadeVO> topLevelList = menuDOS.stream()
                        .map(p -> {
                            CascadeVO parent = new CascadeVO();
                            parent.setValue(p.getId());
                            parent.setLabel(p.getName());
                            parent.setLevel(p.getLevel());

                            // 递归填充子列表
                            fillChilds(parent, parentIdDOMap);
                            return parent;
                        })
                        .collect(Collectors.toList());

                return topLevelList;
            }
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 递归填充子列表
     *
     * @param parent
     * @param parentIdDOMap
     */
    private void fillChilds(CascadeVO parent, Map<Long, List<MenuDO>> parentIdDOMap) {
        List<MenuDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        childs.stream()
                .forEach(c -> {
                    CascadeVO child = new CascadeVO();
                    child.setValue(c.getId());
                    child.setLabel(c.getName());
                    child.setLevel(c.getLevel());

                    List<CascadeVO> childList = parent.getChildren();
                    if (CollectionUtils.isEmpty(childList)) {
                        parent.setChildren(Lists.newArrayList(child));
                    } else {
                        parent.getChildren().add(child);
                    }

                    // 递归填充子列表
                    fillChilds(child, parentIdDOMap);
                });
    }

    /**
     * 操作ID-操作实体 映射关系： operation_id —— OperationDO 映射
     *
     * @return
     */
    private Map<Long, OperationDO> getIdOperationDOMapping() {
        Map<Long, OperationDO> idOperationDOMap = Maps.newConcurrentMap();
        List<OperationDO> allOperationDO = operationDOMapper.getAll(VALID_STATUS);
        allOperationDO.parallelStream()
                .filter(e -> null != e && null != e.getId())
                .forEach(e -> {
                    idOperationDOMap.put(e.getId(), e);
                });
        return idOperationDOMap;
    }

    /**
     * 页面ID-页面实体 映射关系： page_id —— PageDO 映射
     *
     * @return
     */
    private Map<Long, PageDO> getIdPageDOMapping() {
        Map<Long, PageDO> idPageDOMap = Maps.newConcurrentMap();
        List<PageDO> allPageDODO = pageDOMapper.getAll(VALID_STATUS);
        allPageDODO.parallelStream()
                .filter(e -> null != e && null != e.getId())
                .forEach(e -> {
                    idPageDOMap.put(e.getId(), e);
                });
        return idPageDOMap;
    }
}
