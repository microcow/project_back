package com.restdemo.mapper;
import com.restdemo.domain.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
	
	public List<Category> ReadlgCategoryList();
	
}

