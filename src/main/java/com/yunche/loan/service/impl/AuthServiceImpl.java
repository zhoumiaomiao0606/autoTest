package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.MenuDO;
import com.yunche.loan.domain.dataObj.OperationDO;
import com.yunche.loan.domain.dataObj.PageDO;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.PageVO;
import com.yunche.loan.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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


    @Override
    public ResultBean<AuthVO> listAuth() {


//        AuthVO authVO = new AuthVO();
//
//        // 获取并填充所有菜单
//        getAndFillAllMenu(authVO);
//
//        // 获取并填充所有子菜单
//        xxx(authVO);
//
//        // 获取并填充子菜单下的所有子页面
//        xxx(authVO);
//
//        // 获取并填充子页面下的操作权限
//        xxx(authVO);
//
//
//        List<PageDO> pageDOS = pageDOMapper.getAll(VALID_STATUS);
//        List<OperationDO> operationDOS = operationDOMapper.getAll(VALID_STATUS);

        return null;
    }

    private void getAndFillAllMenu(AuthVO authVO) {
        // getAll
        List<MenuDO> menuDOS = menuDOMapper.getAll(VALID_STATUS);

        // parent - DOS
//        menuDOS.stream()
//                .filter(Objects::nonNull)


        // getAllChild

        // fill


        // getAllChild

        // fill

    }

    @Override
    public ResultBean<List<LevelVO>> listMenu() {
        // getAll
        List<MenuDO> menuDOS = menuDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<MenuDO>> parentIdDOMap = getParentIdDOSMapping(menuDOS);

        // 分级递归解析
        List<LevelVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        return ResultBean.ofSuccess(topLevelList);
    }

    @Override
    public ResultBean<Object> listPage(RelaQuery query) {
        Preconditions.checkNotNull(query.getMenuId(), "菜单ID不能为空");
        Preconditions.checkNotNull(query.getUserGroupId(), "用户组ID不能为空");
        int totalNum = pageDOMapper.countMenuPageAndOperation(query);
        if (totalNum > 0) {

            List<PageDO> pageDOS = pageDOMapper.getMenuPageAndOperation(query);
            if (!CollectionUtils.isEmpty(pageDOS)) {
                List<PageVO> pageVOList = pageDOS.stream()
                        .filter(Objects::nonNull)
                        .map(pageDO -> {

                            PageVO pageVO = new PageVO();
                            BeanUtils.copyProperties(pageDO, pageVO);

                            fillMenu(pageDO.getMenu(), pageVO);
                            fillOperations(pageDO.getOperations(), pageVO);

                            return pageVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(pageVOList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    private void fillOperations(List<OperationDO> operations, PageVO pageVO) {
        if (!CollectionUtils.isEmpty(operations)) {
            List<PageVO.Operation> operationList = operations.stream()
                    .filter(Objects::nonNull)
                    .map(o -> {

                        PageVO.Operation operation = new PageVO.Operation();
                        BeanUtils.copyProperties(o, operation);
                        return operation;
                    })
                    .collect(Collectors.toList());

            pageVO.setOperations(operationList);
        }
    }

    private void fillMenu(MenuDO menuDO, PageVO pageVO) {
        if (null != menuDO) {
            PageVO.Menu menu = new PageVO.Menu();
            BeanUtils.copyProperties(menuDO, menu);

            // 递归填充父级菜单
            fillParentMenu(menu, pageVO);
        }
    }

    /**
     * 递归填充并返回父级菜单
     *
     * @param childMenu
     * @param pageVO
     */
    private void fillParentMenu(PageVO.Menu childMenu, PageVO pageVO) {
        if (null != childMenu.getParentId()) {
            MenuDO parentMenuDO = menuDOMapper.selectByPrimaryKey(childMenu.getParentId(), VALID_STATUS);
            if (null != parentMenuDO) {
                PageVO.Menu parentMenu = new PageVO.Menu();
                BeanUtils.copyProperties(parentMenuDO, parentMenu);
                parentMenu.setChildMenu(childMenu);
                fillParentMenu(parentMenu, pageVO);
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
    private List<LevelVO> parseLevelByLevel(Map<Long, List<MenuDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<MenuDO> menuDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(menuDOS)) {
                List<LevelVO> topLevelList = menuDOS.stream()
                        .map(p -> {
                            LevelVO parent = new LevelVO();
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
    private void fillChilds(LevelVO parent, Map<Long, List<MenuDO>> parentIdDOMap) {
        List<MenuDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        childs.stream()
                .forEach(c -> {
                    LevelVO child = new LevelVO();
                    child.setValue(c.getId());
                    child.setLabel(c.getName());
                    child.setLevel(c.getLevel());

                    List<LevelVO> childList = parent.getChildren();
                    if (CollectionUtils.isEmpty(childList)) {
                        parent.setChildren(Lists.newArrayList(child));
                    } else {
                        parent.getChildren().add(child);
                    }

                    // 递归填充子列表
                    fillChilds(child, parentIdDOMap);
                });
    }


    private void xxx(AuthVO authVO) {
        // getAll
        List<MenuDO> menuDOS = menuDOMapper.getAll(VALID_STATUS);

        if (!CollectionUtils.isEmpty(menuDOS)) {
            List<AuthVO.Menu> menuList = menuDOS.stream()
                    .filter(e -> null != e && null != e.getId())
                    .map((MenuDO e) -> {

                        AuthVO.Menu menu = new AuthVO.Menu();
                        BeanUtils.copyProperties(e, menu);

                        return menu;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(AuthVO.Menu::getId))
                    .collect(Collectors.toList());

            authVO.setMenus(menuList);
        }
    }
}
