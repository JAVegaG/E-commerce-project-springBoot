package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.impl.CartDaoImpl;
import com.jtspringproject.JtSpringProject.models.Cart;

@Service
public class CartService extends AbstractService<Cart> {

    @Autowired
    private CartDaoImpl cartDao;

    public List<Cart> getCartsByCustomerId(int customerId) {
        return cartDao.getCartsByCustomerId(customerId);
    }
}
