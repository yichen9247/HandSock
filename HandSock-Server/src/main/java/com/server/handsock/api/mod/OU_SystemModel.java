package com.server.handsock.api.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_system")
public class OU_SystemModel {
    @TableId
    private Integer yid;
    private String time;
    private String name;
    private String value;
}
