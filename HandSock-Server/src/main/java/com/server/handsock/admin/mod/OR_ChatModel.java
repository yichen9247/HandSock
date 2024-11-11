package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_message")
public class OR_ChatModel {
    private Long uid;
    private Long gid;
    @TableId
    private String sid;
    private String type;
    private String time;
    private Integer deleted;
    private String address;
    private String content;
}
