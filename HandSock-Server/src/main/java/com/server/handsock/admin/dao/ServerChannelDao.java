package com.server.handsock.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.admin.mod.ServerChannelModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServerChannelDao extends BaseMapper<ServerChannelModel> {
}
