package com.server.handsock.admin.service;

import org.springframework.stereotype.Service;
import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.admin.dao.ServerUploadDao;
import com.server.handsock.admin.mod.ServerUploadModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;

@Service
public class ServerUploadService {

    private final ServerUploadDao serverUploadDao;
    private final ConsolePrints consolePrints = new ConsolePrints();
    private final HandleResults handleResults = new HandleResults();

    @Autowired
    public ServerUploadService(ServerUploadDao serverUploadDao) {
        this.serverUploadDao = serverUploadDao;
    }

    public Map<String, Object> getUploadList(int page, int limit) {
        try {
            List<ServerUploadModel> server_uploadModelList = serverUploadDao.selectList(null);
            int startWith = (page - 1) * limit;
            int total = server_uploadModelList.size();
            Collections.reverse(server_uploadModelList);
            int endWith = Math.min(startWith + limit, server_uploadModelList.size());
            List<ServerUploadModel> subList = server_uploadModelList.subList(startWith, endWith);
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
            if (serverUploadDao.deleteById(fid) > 0) {
                return handleResults.handleResultByCode(200, null, "删除成功");
            } else return handleResults.handleResultByCode(400, null, "删除失败");
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }

    private void deleteUploadFile(String fid) {
        try {

            ServerUploadModel server_uploadModel = serverUploadDao.selectById(fid);
            File uploadFile = new File("upload/" + server_uploadModel.getType() + "/" + server_uploadModel.getPath());
            if (uploadFile.exists() && uploadFile.delete()) {
                new ConsolePrints().printInfoLogV2("File deleted " + fid);
                new ConsolePrints().printInfoLogV2(server_uploadModel.getPath() + " deleted");
            } else new ConsolePrints().printErrorLog("File delete failed " + fid);
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
        }
    }
}
