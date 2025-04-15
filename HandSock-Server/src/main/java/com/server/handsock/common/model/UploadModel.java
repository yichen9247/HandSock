package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_upload")
public class UploadModel {
    @TableId
    public String fid;
    public Long uid;
    public Long size;
    public String time;
    public String name;
    public String path;
    public String type;
}
