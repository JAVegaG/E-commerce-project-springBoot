package com.jtspringproject.JtSpringProject.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.dao.DaoImplementor;
import com.jtspringproject.JtSpringProject.models.CartProduct;
import com.jtspringproject.JtSpringProject.models.Product;

@Repository
public class CartProductDaoImpl implements DaoImplementor<CartProduct> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<CartProduct> findAll() {
        return this.sessionFactory.getCurrentSession().createQuery("from CART_PRODUCT", CartProduct.class).list();
    }

    @Override
    @Transactional
    public CartProduct save(CartProduct entity) {
        this.sessionFactory.getCurrentSession().save(entity);
        return entity;
    }

    @Override
    @Transactional
    public CartProduct findById(int id) {
        return this.sessionFactory.getCurrentSession().get(CartProduct.class, id);
    }

    @Override
    @Transactional
    public CartProduct update(CartProduct entity) {
        this.sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Object persistanceInstance = session.load(CartProduct.class, id);

        if (persistanceInstance != null) {
            session.delete(persistanceInstance);
            return true;
        }
        return false;
    }

    @Transactional
    public List<Product> getProductsByCartId(Integer cartId) {
        String sql = "SELECT product_id FROM cart_product WHERE cart_id = :cart_id";
        List<Integer> productIds = this.sessionFactory.getCurrentSession()
                .createNativeQuery(sql, Integer.class)
                .setParameter("cart_id", cartId)
                .list();

        sql = "SELECT * FROM product WHERE id IN (:product_ids)";
        return this.sessionFactory.getCurrentSession()
                .createNativeQuery(sql, Product.class)
                .setParameterList("product_ids", productIds)
                .list();
    }
}
