package com.server.handsock.clients.man;

import com.server.handsock.console.UtilityService;
import com.server.handsock.console.IDGenerator;
import com.server.handsock.clients.mod.ClientReportModel;

public class ClientReportManage {
    public void insertRepo(ClientReportModel clientReportModel, String sid, Long reporter_id, Long reported_id, String reason) {
        clientReportModel.setSid(sid);
        clientReportModel.setReason(reason);
        clientReportModel.setTime(new UtilityService().formatTime("yyyy-MM-dd HH:mm:ss"));
        clientReportModel.setReporterId(reporter_id);
        clientReportModel.setReportedId(reported_id);
        clientReportModel.setRid(new IDGenerator().generateRandomReportedId(sid, reporter_id, reported_id));
    }
}
