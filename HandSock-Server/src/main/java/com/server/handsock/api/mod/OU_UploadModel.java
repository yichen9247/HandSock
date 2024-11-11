package com.server.handsock.api.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_upload")
public class OU_UploadModel {
    @TableId
    private String fid;
    private Long uid;
    private String time;
    private String name;
    private String path;
}
