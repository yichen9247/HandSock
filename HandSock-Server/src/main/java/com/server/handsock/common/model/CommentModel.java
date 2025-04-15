package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.server.handsock.client.mod.ClientUserModel;
import lombok.Data;

import java.util.List;

@Data
@TableName("handsock_comment")
public class CommentModel {
    public Long uid;
    @TableId
    public Integer cid;
    public Integer pid;
    public String time;
    public String content;
    public Integer parent;

    @TableField(exist = false)
    public ClientUserModel user;

    @TableField(exist = false)
    private List<CommentModel> children;
}
