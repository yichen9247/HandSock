package com.server.handsock.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.handsock.common.model.CommentModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentDao extends BaseMapper<CommentModel> {
    IPage<CommentModel> selectHierarchicalComments(Page<CommentModel> page, @Param("pid") Integer pid);
}