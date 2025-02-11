package com.server.handsock.clients.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_message")
public class ClientChatModel {
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
