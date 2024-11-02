package com.restdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.restdemo.domain.Category;
import com.restdemo.mapper.CategoryMapper;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
    CategoryMapper categoryMapper;
	
	@Override
	public List<Category> ReadlgCategoryList(){
		return categoryMapper.ReadlgCategoryList();
	}
}
