package com.server.handsock.clients.mod;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("handsock_channel")
public class ClientChannelModel {
    @TableId
    private Long gid;
    private String name;
    private Integer open;
    private Integer home;
    private String notice;
    private String avatar;
    private Integer active;
    private Integer aiRole;
}
