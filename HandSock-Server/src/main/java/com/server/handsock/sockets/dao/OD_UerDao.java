package com.server.handsock.sockets.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.sockets.mod.OP_UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OD_UerDao extends BaseMapper<OP_UserModel> {}