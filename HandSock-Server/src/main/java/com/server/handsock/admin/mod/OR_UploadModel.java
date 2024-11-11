package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_upload")
public class OR_UploadModel {
    @TableId
    private String fid;
    private Long uid;
    private String time;
    private String name;
    private String path;
}
