package com.server.handsock.clients.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_channel")
public class ClientChannelModel {
    @TableId
    public Long gid;
    public String name;
    public Integer open;
    public Integer home;
    public String notice;
    public String avatar;
    public Integer active;
    public Integer aiRole;
}
