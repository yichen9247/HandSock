package com.server.handsock.clients.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_report")
public class ClientReportModel {
    @TableId
    public String rid;
    public String sid;
    public String time;
    public String reason;
    public Long reporterId;
    public Long reportedId;
}
