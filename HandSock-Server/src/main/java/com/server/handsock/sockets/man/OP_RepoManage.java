package com.server.handsock.sockets.man;

import com.server.handsock.console.TimeFormatUtils;
import com.server.handsock.generat.IDGenerator;
import com.server.handsock.sockets.mod.OP_RepoModel;

public class OP_RepoManage {
    public void insertRepo(OP_RepoModel op_repoModel, String sid, Long reporter_id, Long reported_id, String reason) {
        op_repoModel.setSid(sid);
        op_repoModel.setReason(reason);
        op_repoModel.setTime(new TimeFormatUtils().formatTime("yyyy-MM-dd HH:mm:ss"));
        op_repoModel.setReporterId(reporter_id);
        op_repoModel.setReportedId(reported_id);
        op_repoModel.setRid(new IDGenerator().generateRandomReportedId(sid, reporter_id, reported_id));
    }
}
