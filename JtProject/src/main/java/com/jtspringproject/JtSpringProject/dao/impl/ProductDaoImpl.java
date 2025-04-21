package com.jtspringproject.JtSpringProject.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.dao.DaoImplementor;
import com.jtspringproject.JtSpringProject.models.Product;

@Repository
public class ProductDaoImpl implements DaoImplementor<Product> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Product> findAll() {
        return this.sessionFactory.getCurrentSession().createQuery("from PRODUCT", Product.class).list();
    }

    @Override
    @Transactional
    public Product save(Product entity) {
        this.sessionFactory.getCurrentSession().save(entity);
        return entity;
    }

    @Override
    @Transactional
    public Product findById(int id) {
        return this.sessionFactory.getCurrentSession().get(Product.class, id);
    }

    @Override
    @Transactional
    public Product update(Product entity) {
        this.sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Object persistanceInstance = session.load(Product.class, id);

        if (persistanceInstance != null) {
            session.delete(persistanceInstance);
            return true;
        }
        return false;
    }
}
