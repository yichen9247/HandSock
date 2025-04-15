package com.server.handsock.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.handsock.common.model.UploadModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadDao extends BaseMapper<UploadModel> {}
