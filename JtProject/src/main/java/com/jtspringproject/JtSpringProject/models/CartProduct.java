package com.jtspringproject.JtSpringProject.models;

import javax.persistence.Column; // Importar Column
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn; // Importar JoinColumn
import javax.persistence.ManyToOne;
// import javax.persistence.JoinTable; // JoinTable aquí es inusual para ManyToOne, es mejor usar JoinColumn directamente.

// Ya no son necesarios si no se usan directamente aquí.
// import java.util.ArrayList;
// import java.util.List;


@Entity(name="CART_PRODUCT")
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name="cart_id") // Clave foránea hacia la tabla CART
    private Cart cart;

    @ManyToOne
    @JoinColumn(name="product_id") // Clave foránea hacia la tabla PRODUCT. La anotación @JoinTable es más para @ManyToMany.
    private Product product;

    @Column(name = "quantity") // Nueva columna para la cantidad del producto en esta entrada del carrito
    private int quantity;


    public CartProduct() {
        // El constructor por defecto es requerido por JPA
    }

    // Constructor actualizado para incluir la cantidad
    public CartProduct(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}