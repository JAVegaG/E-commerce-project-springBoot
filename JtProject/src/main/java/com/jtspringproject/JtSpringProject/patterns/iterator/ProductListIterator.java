package com.jtspringproject.JtSpringProject.patterns.iterator;

import java.util.List;
import java.util.NoSuchElementException;

import com.jtspringproject.JtSpringProject.models.Product; // Para lanzar excepción si se llama a next() incorrectamente

/**
 * ConcreteIterator: implementa IProductIterator para una lista de productos.
 */
public class ProductListIterator implements IProductIterator {

    private List<Product> products;
    private int position = 0;

    public ProductListIterator(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean hasNext() {
        return this.products != null && this.position < this.products.size();
    }

    @Override
    public Product next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay más productos en la iteración.");
        }
        Product product = this.products.get(this.position);
        this.position++;
        return product;
    }

    @Override
    public void reset() {
        this.position = 0;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
