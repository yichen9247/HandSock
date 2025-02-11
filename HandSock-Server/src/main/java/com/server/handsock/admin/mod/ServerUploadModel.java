package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_upload")
public class ServerUploadModel {
    @TableId
    private String fid;
    private Long uid;
    private Long size;
    private String time;
    private String name;
    private String path;
    private String type;
}
