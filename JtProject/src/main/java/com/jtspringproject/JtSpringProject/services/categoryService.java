package com.jtspringproject.JtSpringProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.impl.CategoryDaoImpl;
import com.jtspringproject.JtSpringProject.models.Category;

@Service
public class CategoryService extends AbstractService<Category> {

    @Autowired
    private CategoryDaoImpl categoryDao;

    public Category addCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryDao.save(category);
    }

    public Category updateCategory(int id, String name) {
        return categoryDao.updateCategory(id, name);
    }
}
