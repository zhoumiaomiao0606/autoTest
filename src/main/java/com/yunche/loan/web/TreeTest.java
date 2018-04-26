package com.yunche.loan.web;

import java.util.ArrayList;
import java.util.List;

public class TreeTest {

    public static void main(String[] args)
    {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        treeNodes.add(new TreeNode("系统权限管理", "root"));
        treeNodes.add(new TreeNode("用户管理", "系统权限管理"));
        treeNodes.add(new TreeNode("角色管理", "系统权限管理"));
        treeNodes.add(new TreeNode("组管理", "系统权限管理"));
        treeNodes.add(new TreeNode("用户菜单管理", "系统权限管理"));
        treeNodes.add(new TreeNode("角色菜单管理", "系统权限管理"));
        treeNodes.add(new TreeNode("用户权限管理", "系统权限管理"));
        treeNodes.add(new TreeNode("站内信", "root"));
        treeNodes.add(new TreeNode("写信", "站内信"));
        treeNodes.add(new TreeNode("收信", "站内信"));
        treeNodes.add(new TreeNode("草稿", "站内信"));

        TreeFactory tree = new TreeFactory();

        System.out.println(tree.iteratorTree(tree.createTree(treeNodes).getRoot()));
    }
}

