package com.server.handsock.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.admin.mod.ServerChatModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServerChatDao extends BaseMapper<ServerChatModel> {
}
