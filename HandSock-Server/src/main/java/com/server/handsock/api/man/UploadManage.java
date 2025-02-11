package com.server.handsock.api.man;

import com.server.handsock.console.UtilityService;
import com.server.handsock.console.IDGenerator;
import com.server.handsock.api.mod.UploadModel;

public class UploadManage {

    public void insertUploadFile(UploadModel uploadModel, Long uid, String name, String path, String time, String type, Long size) {
        uploadModel.setFid(new IDGenerator().generateRandomFileId(uid, name, path, time));
        uploadModel.setUid(uid);
        uploadModel.setName(name);
        uploadModel.setPath(path);
        uploadModel.setType(type);
        uploadModel.setSize(size);
        uploadModel.setTime(new UtilityService().formatTime("yyyy-MM-dd HH:mm:ss"));
    }
}
