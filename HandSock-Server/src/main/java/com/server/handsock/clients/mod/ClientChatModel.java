package com.server.handsock.clients.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_message")
public class ClientChatModel {
    public Long uid;
    public Long gid;
    @TableId
    public String sid;
    public int deleted;
    public String type;
    public String time;
    public String address;
    public String content;
}
