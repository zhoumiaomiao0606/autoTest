package com.yunche.loan.web;

import lombok.Data;

@Data
public class TreeNode {

    /** 节点Id*/
    private String nodeId;
    /** 父节点Id*/
    private String parentId;

    public TreeNode(String nodeId) {
        this.nodeId = nodeId;
    }

    public TreeNode(String nodeId, String parentId) {
        this.nodeId = nodeId;
        this.parentId = parentId;
    }
}
