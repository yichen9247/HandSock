package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_channel")
public class ServerChannelModel {
    @TableId
    public Long gid;
    public String name;
    public Integer home;
    public Integer open;
    public String notice;
    public String avatar;
    public Integer active;
    public Integer aiRole;
}
