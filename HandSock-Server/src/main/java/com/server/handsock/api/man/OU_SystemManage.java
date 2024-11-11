package com.server.handsock.api.man;

import java.util.Map;
import java.util.HashMap;
import com.server.handsock.api.mod.OU_SystemModel;

public class OU_SystemManage {

    public Map<String, Object> setSystemKeyStatus(OU_SystemModel OUSystemModel, int status) {
        if (status == 1) OUSystemModel.setValue("open");
        if (status == 0) OUSystemModel.setValue("close");

        return new HashMap<>(){{
           put("status", OUSystemModel.getValue());
        }};
    }
}
