package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_user")
public class ServerUserModel {
    @TableId
    public Long uid;
    public String nick;
    public String taboo;
    public String avatar;
    public String regTime;
    public Integer isAdmin;
    public String username;
    public String password;
    public Integer isRobot;
    // 慎用这个数据模型，这个模型是给后端用的，客户端调用ClientUserModel，切记不可暴露password字段
}
