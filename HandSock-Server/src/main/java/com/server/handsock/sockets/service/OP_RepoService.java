package com.server.handsock.sockets.service;

import com.server.handsock.console.ConsolePrints;
import com.server.handsock.console.HandleResults;
import com.server.handsock.service.EmailService;
import com.server.handsock.sockets.dao.OP_ChatDao;
import com.server.handsock.sockets.dao.OP_RepoDao;
import com.server.handsock.sockets.dao.OP_UserDao;
import com.server.handsock.sockets.man.OP_RepoManage;
import com.server.handsock.sockets.mod.OP_RepoModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service @Getter @Setter
public class OP_RepoService {

    private final OP_UserDao opUserDao;
    private final OP_ChatDao opChatDao;
    private final OP_RepoDao op_repoDao;
    private final HandleResults handleResults = new HandleResults();

    public OP_RepoService(OP_RepoDao op_repoDao, OP_ChatDao opChatDao, OP_UserDao opUserDao) {
        this.opUserDao = opUserDao;
        this.opChatDao = opChatDao;
        this.op_repoDao = op_repoDao;
    }

    @Value("${handsock.admin}")
    private String adminMail;

    @Value("${handsock.reportMail}")
    private Boolean reportMail;

    public Map<String, Object> addReport(Map<String, Object> service, String sid, Long reporter_id, Long reported_id, String reason) {
        try {
            if (Objects.equals(reporter_id, reported_id)) {
                return handleResults.handleResultByCode(400, null, "不能举报自己");
            } else {
                OP_RepoModel op_repoModel = new OP_RepoModel();
                new OP_RepoManage().insertRepo(op_repoModel, sid, reporter_id, reported_id, reason);
                if (op_repoDao.insert(op_repoModel) > 0) {
                    if (reportMail) {
                        EmailService emailService = (EmailService) service.get("emailService");
                        emailService.sendSimpleMessage(adminMail, "举报通知（举报人：" + reporter_id + "）", "被举报人：" + reported_id + "，举报理由：" + reason + "，举报内容：" + opChatDao.selectById(sid).getContent());
                    }
                    return handleResults.handleResultByCode(200, null, "举报成功");
                } else return handleResults.handleResultByCode(400, null, "举报失败");
            }
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return handleResults.handleResultByCode(500, null, "服务端异常");
        }
    }
}
