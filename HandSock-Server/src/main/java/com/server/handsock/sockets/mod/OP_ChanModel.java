package com.server.handsock.sockets.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_channel")
public class OP_ChanModel {
    @TableId
    private Long gid;
    private String name;
    private Integer open;
    private Integer home;
    private String notice;
    private String avatar;
    private Integer active;
}
