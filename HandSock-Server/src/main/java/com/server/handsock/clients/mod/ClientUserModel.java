package com.server.handsock.clients.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_user")
public class ClientUserModel {
    @TableId
    public Long uid;
    public String nick;
    public int isAdmin;
    public int isRobot;
    public String taboo;
    public String email;
    public String avatar;
    public Integer aiAuth;
    public String regTime;
    public String username;
}
