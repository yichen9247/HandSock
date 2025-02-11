package com.server.handsock.admin.dao;

import org.apache.ibatis.annotations.Mapper;
import com.server.handsock.admin.mod.ServerReportModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface ServerReportDao extends BaseMapper<ServerReportModel> {}
