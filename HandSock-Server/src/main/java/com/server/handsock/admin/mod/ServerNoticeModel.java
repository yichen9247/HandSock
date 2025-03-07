package com.server.handsock.admin.mod;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_notice")
public class ServerNoticeModel {
    @TableId
    private Integer nid;
    private String time;
    private String title;
    private String content;
}
