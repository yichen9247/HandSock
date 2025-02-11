package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_system")
public class ServerSystemModel {
    @TableId
    private Integer yid;
    private String time;
    private String name;
    private String value;
}
