package com.server.handsock.api.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.api.mod.UploadModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadDao extends BaseMapper<UploadModel> {}
