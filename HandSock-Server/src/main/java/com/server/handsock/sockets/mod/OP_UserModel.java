package com.server.handsock.sockets.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_user")
public class OP_UserModel {
    @TableId
    private Long uid;
    private String nick;
    private int isAdmin;
    private int isRobot;
    private String email;
    private String avatar;
    private String username;
}
