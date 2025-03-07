package com.server.handsock.clients.man;

import com.server.handsock.clients.mod.ClientReportModel;
import com.server.handsock.utils.HandUtils;
import com.server.handsock.utils.IDGenerator;

public class ClientReportManage {
    private final HandUtils handUtils;
    private final IDGenerator idGenerator;

    public ClientReportManage(HandUtils handUtils, IDGenerator idGenerator) {
        this.handUtils = handUtils;
        this.idGenerator = idGenerator;
    }

    public void insertRepo(ClientReportModel clientReportModel, String sid, Long reporter_id, Long reported_id, String reason) {
        clientReportModel.setSid(sid);
        clientReportModel.setReason(reason);
        clientReportModel.setTime(handUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss"));
        clientReportModel.setReporterId(reporter_id);
        clientReportModel.setReportedId(reported_id);
        clientReportModel.setRid(idGenerator.generateRandomReportedId(sid, reporter_id, reported_id));
    }
}
