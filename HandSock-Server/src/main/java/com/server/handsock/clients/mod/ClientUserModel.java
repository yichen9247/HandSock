package com.server.handsock.clients.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_user")
public class ClientUserModel {
    @TableId
    private Long uid;
    private String nick;
    private int isAdmin;
    private int isRobot;
    private String taboo;
    private String email;
    private String avatar;
    private Integer aiAuth;
    private String regTime;
    private String username;
}
