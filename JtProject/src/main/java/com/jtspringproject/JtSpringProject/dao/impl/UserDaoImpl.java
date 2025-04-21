package com.jtspringproject.JtSpringProject.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.dao.DaoImplementor;
import com.jtspringproject.JtSpringProject.models.User;

@Repository
public class UserDaoImpl implements DaoImplementor<User> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<User> findAll() {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createQuery("from CUSTOMER", User.class).list();
    }

    @Override
    @Transactional
    public User save(User entity) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    @Transactional
    public User findById(int id) {
        return this.sessionFactory.getCurrentSession().get(User.class, id);
    }

    @Override
    @Transactional
    public User update(User entity) {
        this.sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Object persistenceInstance = session.load(User.class, id);

        if (persistenceInstance != null) {
            session.delete(persistenceInstance);
            return true;
        }
        return false;
    }

    @Transactional
    public User getUser(String username, String password) {
        Query<User> query = sessionFactory.getCurrentSession().createQuery("from CUSTOMER where username = :username", User.class);
        query.setParameter("username", username);

        try {
            User user = (User) query.getSingleResult();
            if (password.equals(user.getPassword())) {
                return user;
            } else {
                return new User();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new User();
        }
    }

    @Transactional
    public boolean userExists(String username) {
        Query<User> query = sessionFactory.getCurrentSession().createQuery("from CUSTOMER where username = :username", User.class);
        query.setParameter("username", username);
        return !query.getResultList().isEmpty();
    }

    @Transactional
    public User getUserByUsername(String username) {
        Query<User> query = sessionFactory.getCurrentSession().createQuery("from User where username = :username", User.class);
        query.setParameter("username", username);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
