package com.server.handsock.api.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_upload")
public class UploadModel {
    @TableId
    private Long uid;
    private Long size;
    private String fid;
    private String time;
    private String name;
    private String path;
    private String type;
}
