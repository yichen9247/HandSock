package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_system")
public class SystemModel {
    @TableId
    public Integer yid;
    public String time;
    public String name;
    public String value;
}
