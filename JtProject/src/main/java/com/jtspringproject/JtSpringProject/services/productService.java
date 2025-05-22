package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.productDao;
import com.jtspringproject.JtSpringProject.models.Product;

@Service
public class productService {

    @Autowired
    private productDao productDao;

    public List<Product> getProducts() {
        return this.productDao.getProducts();
    }

    public Product addProduct(Product product) {
        return this.productDao.addProduct(product);
    }

    public Product getProduct(int id) {
        return this.productDao.getProduct(id);
    }

    public Product updateProduct(int id, Product product) {
        product.setId(id);
        return this.productDao.updateProduct(product);
    }

    public boolean deleteProduct(int id) {
        return this.productDao.deletProduct(id);
    }

    public void updateProductQuantity(int productId, int quantityToAdd) {
        Product product = this.productDao.getProduct(productId);

        product.setQuantity(product.getQuantity() - quantityToAdd);
        this.updateProduct(productId, product); // o el método de update que tengas
    }

}
