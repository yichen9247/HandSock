package com.server.handsock.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("handsock_banner")
public class BannerModel {
    @TableId
    public Integer bid;
    public String name;
    public String href;
    public String image;
}
