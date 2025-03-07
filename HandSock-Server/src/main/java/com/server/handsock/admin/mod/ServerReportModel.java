package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_report")
public class ServerReportModel {
    @TableId
    public String rid;
    public String sid;
    public String time;
    public String reason;
    public Long reporterId;
    public Long reportedId;
}
