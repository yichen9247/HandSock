package com.server.handsock.clients.dao;

import org.apache.ibatis.annotations.Mapper;
import com.server.handsock.clients.mod.ClientReportModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface ClientReportDao extends BaseMapper<ClientReportModel> {}
