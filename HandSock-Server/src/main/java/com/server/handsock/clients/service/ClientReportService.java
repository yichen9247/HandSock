package com.server.handsock.clients.service;

import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.clients.dao.ClientChatDao;
import com.server.handsock.clients.dao.ClientReportDao;
import com.server.handsock.clients.man.ClientReportManage;
import com.server.handsock.clients.mod.ClientReportModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service @Setter @Getter
public class ClientReportService {

    private final ClientChatDao clientChatDao;
    private final ClientReportDao clientReportDao;
    private final HandleResults handleResults = new HandleResults();

    public ClientReportService(ClientReportDao clientReportDao, ClientChatDao clientChatDao) {
        this.clientChatDao = clientChatDao;
        this.clientReportDao = clientReportDao;
    }

    public Map<String, Object> addReport(Map<String, Object> service, String sid, Long reporter_id, Long reported_id, String reason) {
        try {
            if (Objects.equals(reporter_id, reported_id)) {
                return handleResults.handleResultByCode(400, null, "不能举报自己");
            } else {
                ClientReportModel clientReportModel = new ClientReportModel();
                new ClientReportManage().insertRepo(clientReportModel, sid, reporter_id, reported_id, reason);
                if (clientReportDao.insert(clientReportModel) > 0) {
                    return handleResults.handleResultByCode(200, null, "举报成功");
                } else return handleResults.handleResultByCode(400, null, "举报失败");
            }
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}
