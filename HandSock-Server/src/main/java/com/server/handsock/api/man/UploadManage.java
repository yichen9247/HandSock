package com.server.handsock.api.man;

import com.server.handsock.api.mod.UploadModel;
import com.server.handsock.utils.HandUtils;
import com.server.handsock.utils.IDGenerator;

public class UploadManage {
    private final HandUtils handUtils;
    private final IDGenerator idGenerator;

    public UploadManage(HandUtils handUtils, IDGenerator idGenerator) {
        this.handUtils = handUtils;
        this.idGenerator = idGenerator;
    }

    public void insertUploadFile(UploadModel uploadModel, Long uid, String name, String path, String time, String type, Long size) {
        uploadModel.setFid(idGenerator.generateRandomFileId(uid, name, path, time));
        uploadModel.setUid(uid);
        uploadModel.setName(name);
        uploadModel.setPath(path);
        uploadModel.setType(type);
        uploadModel.setSize(size);
        uploadModel.setTime(handUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss"));
    }
}
