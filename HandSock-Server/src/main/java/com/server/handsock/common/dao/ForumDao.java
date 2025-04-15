package com.server.handsock.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.handsock.common.model.ForumModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ForumDao extends BaseMapper<ForumModel> {
    IPage<ForumModel> selectHierarchicalPosts(Page<ForumModel> page);
}