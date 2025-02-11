package com.server.handsock.admin.mod;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("handsock_user")
public class ServerUserModel {
    @TableId
    private Long uid;
    private String nick;
    private String taboo;
    private String avatar;
    private String regTime;
    private Integer isAdmin;
    private String username;
    private String password;
    private Integer isRobot;
    // 慎用这个数据模型，这个模型是给后端用的，客户端调用ClientUserModel，切记不可暴露password字段
}
