package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_notice")
public class NoticeModel {
    @TableId
    private Integer nid;
    private String time;
    private String title;
    private String content;
}
