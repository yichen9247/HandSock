package com.server.handsock.admin.man;

import com.server.handsock.admin.mod.ServerSystemModel;

import java.util.HashMap;
import java.util.Map;

public class ServerSystemManage {

    public Map<String, Object> setSystemKeyStatus(ServerSystemModel OUSystemModel, String value) {
        OUSystemModel.setValue(value);
        return new HashMap<>(){{
           put("status", OUSystemModel.getValue());
        }};
    }
}
