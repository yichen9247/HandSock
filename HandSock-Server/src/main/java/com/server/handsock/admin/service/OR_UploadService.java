package com.server.handsock.admin.service;

import org.springframework.stereotype.Service;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.dao.OR_UploadDao;
import com.server.handsock.admin.mod.OR_UploadModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;

@Service
public class OR_UploadService {

    private final OR_UploadDao or_uploadDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public OR_UploadService(OR_UploadDao or_uploadDao) {
        this.or_uploadDao = or_uploadDao;
    }

    public Map<String, Object> getUploadList(int page, int limit) {
        try {
            List<OR_UploadModel> or_uploadModelList = or_uploadDao.selectList(null);
            int startWith = (page - 1) * limit;
            int total = or_uploadModelList.size();
            Collections.reverse(or_uploadModelList);
            int endWith = Math.min(startWith + limit, or_uploadModelList.size());
            List<OR_UploadModel> subList = or_uploadModelList.subList(startWith, endWith);
            return handleResults.handleResultByCode(200, new HashMap<>(){{
                put("items", subList);
                put("total", total);
            }}, "获取成功");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    public Map<String, Object> deleteUpload(String fid) {
        try {
            deleteUploadFile(fid);
            if (or_uploadDao.deleteById(fid) > 0) {
                return handleResults.handleResultByCode(200, null, "删除成功");
            } else return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    private void deleteUploadFile(String fid) {
        try {
            OR_UploadModel or_uploadModel = or_uploadDao.selectById(fid);
            File uploadFile = new File("upload/files/" + or_uploadModel.getPath());
            if (uploadFile.exists() && uploadFile.delete()) {
                new ConsolePrints().printInfoLogV2("File deleted " + fid);
            } else new ConsolePrints().printInfoLogV2("File delete failed " + fid);
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
    }
}
