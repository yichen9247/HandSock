package com.server.handsock.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_banner")
public class ServerBannerModel {
    @TableId
    private Integer bid;
    private String name;
    private String href;
    private String image;
}
