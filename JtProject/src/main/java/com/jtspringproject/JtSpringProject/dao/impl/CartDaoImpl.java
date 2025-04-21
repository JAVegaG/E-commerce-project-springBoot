package com.jtspringproject.JtSpringProject.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.dao.DaoImplementor;
import com.jtspringproject.JtSpringProject.models.Cart;

@Repository
public class CartDaoImpl implements DaoImplementor<Cart> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Cart> findAll() {
        return this.sessionFactory.getCurrentSession().createQuery("from CART", Cart.class).list();
    }

    @Override
    @Transactional
    public Cart save(Cart entity) {
        this.sessionFactory.getCurrentSession().save(entity);
        return entity;
    }

    @Override
    @Transactional
    public Cart findById(int id) {
        return this.sessionFactory.getCurrentSession().get(Cart.class, id);
    }

    @Override
    @Transactional
    public Cart update(Cart entity) {
        this.sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Object persistanceInstance = session.load(Cart.class, id);

        if (persistanceInstance != null) {
            session.delete(persistanceInstance);
            return true;
        }
        return false;
    }

    @Transactional
    public List<Cart> getCartsByCustomerId(int customerId) {
        String hql = "from CART where customer_id = :customerId";
        return this.sessionFactory.getCurrentSession()
                .createQuery(hql, Cart.class)
                .setParameter("customerId", customerId)
                .list();
    }
}
