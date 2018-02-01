package com.yunche.loan.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.MenuDO;
import com.yunche.loan.domain.dataObj.OperationDO;
import com.yunche.loan.domain.dataObj.PageDO;
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
    public ResultBean<List<PageVO>> listOperation(RelaQuery query) {
        int totalNum = pageDOMapper.countMenuPageAndOperation(query);
        if (totalNum > 0) {

            List<PageDO> pageDOS = pageDOMapper.queryMenuPageAndOperation(query);
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
            fillParentMenu(menu, pageVO);
        }
    }

    /**
     * 递归填充父级菜单
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
}
