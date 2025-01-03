package com.server.handsock.api.man;

import com.server.handsock.generat.IDGenerator;
import com.server.handsock.api.mod.OU_UploadModel;

public class OU_UploadManage {

    public void insertUploadFile(OU_UploadModel ou_uploadModel, Long uid, String name, String path, String time) {
        ou_uploadModel.setFid(new IDGenerator().generateRandomFileId(uid, name, path, time));
        ou_uploadModel.setUid(uid);
        ou_uploadModel.setName(name);
        ou_uploadModel.setPath(path);
    }
}
