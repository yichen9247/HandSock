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
    public String qqId;
    public String avatar;
    public String regTime;
    public Integer status;
    public String username;
    public String password;
    public Integer permission;
}
