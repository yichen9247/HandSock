package com.server.handsock.client.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.client.mod.ClientUserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientUserDao extends BaseMapper<ClientUserModel> {
}
