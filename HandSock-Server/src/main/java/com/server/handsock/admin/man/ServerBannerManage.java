package com.server.handsock.admin.man;

import com.server.handsock.admin.mod.ServerBannerModel;

public class ServerBannerManage {
    public void setBanner(ServerBannerModel serverBannerModel, String name, String href, String image) {
        serverBannerModel.setName(name);
        serverBannerModel.setHref(href);
        serverBannerModel.setImage(image);
    }

    public void updateBanner(ServerBannerModel serverBannerModel, Integer bid, String name, String href, String image) {
        serverBannerModel.setBid(bid);
        setBanner(serverBannerModel, name, href, image);
    }
}
