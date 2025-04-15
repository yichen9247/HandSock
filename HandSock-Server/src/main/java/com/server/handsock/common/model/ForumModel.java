package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.server.handsock.client.mod.ClientUserModel;
import lombok.Data;

@Data
@TableName("handsock_forum")
public class ForumModel {
    public Long uid;
    @TableId
    public Integer pid;
    public String time;
    public String title;
    public String image;
    public String content;

    @TableField(exist = false)
    public ClientUserModel user;
}
