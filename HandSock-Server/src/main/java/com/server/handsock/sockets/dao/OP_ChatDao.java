package com.server.handsock.sockets.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.sockets.mod.OP_ChatModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OP_ChatDao extends BaseMapper<OP_ChatModel> {}
