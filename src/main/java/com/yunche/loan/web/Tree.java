package com.yunche.loan.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Tree {

    /** 树节点*/
    private TreeNode node;
    /** 子树集合*/
    private List<Tree> childNodes;

    public Tree(TreeNode node)
    {
        this.node = node;
        this.childNodes = new ArrayList<Tree>();
    }
}
