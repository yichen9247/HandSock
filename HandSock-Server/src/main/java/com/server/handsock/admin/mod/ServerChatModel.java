package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_message")
public class ServerChatModel {
    public Long uid;
    public Long gid;
    @TableId
    public String sid;
    public String type;
    public String time;
    public Integer deleted;
    public String address;
    public String content;
}
