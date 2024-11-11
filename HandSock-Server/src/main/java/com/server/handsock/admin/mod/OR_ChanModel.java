package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_channel")
public class OR_ChanModel {
    @TableId
    private Long gid;
    private String name;
    private Integer home;
    private Integer open;
    private Integer active;
    private String notice;
    private String avatar;
}
