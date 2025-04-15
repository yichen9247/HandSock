package com.server.handsock.admin.man;

import com.server.handsock.common.model.BannerModel;

public class ServerBannerManage {
    public void setBanner(BannerModel bannerModel, String name, String href, String image) {
        bannerModel.setName(name);
        bannerModel.setHref(href);
        bannerModel.setImage(image);
    }

    public void updateBanner(BannerModel bannerModel, Integer bid, String name, String href, String image) {
        bannerModel.setBid(bid);
        setBanner(bannerModel, name, href, image);
    }
}
