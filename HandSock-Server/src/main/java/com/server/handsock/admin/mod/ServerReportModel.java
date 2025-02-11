package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_report")
public class ServerReportModel {
    @TableId
    private String rid;
    private String sid;
    private String time;
    private String reason;
    private Long reporterId;
    private Long reportedId;
}
