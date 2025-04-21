package com.jtspringproject.JtSpringProject.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.dao.DaoImplementor;
import com.jtspringproject.JtSpringProject.models.Category;

@Repository
public class CategoryDaoImpl implements DaoImplementor<Category> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Category> findAll() {
        return this.sessionFactory.getCurrentSession().createQuery("from CATEGORY", Category.class).list();
    }

    @Override
    @Transactional
    public Category save(Category entity) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    @Transactional
    public Category findById(int id) {
        return this.sessionFactory.getCurrentSession().get(Category.class, id);
    }

    @Override
    @Transactional
    public Category update(Category entity) {
        this.sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Object persistanceInstance = session.load(Category.class, id);

        if (persistanceInstance != null) {
            session.delete(persistanceInstance);
            return true;
        }
        return false;
    }

    @Transactional
    public Category updateCategory(int id, String name) {
        Category category = this.sessionFactory.getCurrentSession().get(Category.class, id);
        category.setName(name);
        this.sessionFactory.getCurrentSession().update(category);
        return category;
    }
}
