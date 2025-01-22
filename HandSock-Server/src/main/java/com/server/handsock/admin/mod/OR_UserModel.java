package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_user")
public class OR_UserModel {
    @TableId
    private Long uid;
    private String nick;
    private String taboo;
    private String avatar;
    private String regTime;
    private String username;
    private String password;
    private Integer isRobot;
}
