package com.server.handsock.clients.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.clients.mod.ClientUserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientUserDao extends BaseMapper<ClientUserModel> {}
