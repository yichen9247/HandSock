package com.server.handsock.api.man;

import com.server.handsock.api.mod.OU_SystemModel;

import java.util.HashMap;
import java.util.Map;

public class OU_SystemManage {

    public Map<String, Object> setSystemKeyStatus(OU_SystemModel OUSystemModel, String value) {
        OUSystemModel.setValue(value);
        return new HashMap<>(){{
           put("status", OUSystemModel.getValue());
        }};
    }
}
