package com.server.handsock.sockets.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_message")
public class OP_ChatModel {
    private Long uid;
    private Long gid;
    @TableId
    private String sid;
    private int deleted;
    private String type;
    private String time;
    private String address;
    private String content;
}
