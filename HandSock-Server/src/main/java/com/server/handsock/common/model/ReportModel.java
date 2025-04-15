package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_report")
public class ReportModel {
    public String sid;
    @TableId
    public Integer rid;
    public String time;
    public String reason;
    public Long reporterId;
    public Long reportedId;
}
