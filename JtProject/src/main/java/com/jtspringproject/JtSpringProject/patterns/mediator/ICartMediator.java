package com.jtspringproject.JtSpringProject.patterns.mediator;

/**
 * Interfaz Mediator: define la operación para añadir un producto al carrito,
 * coordinando las interacciones entre los servicios (colegas).
 */
public interface ICartMediator {
    /**
     * Añade una cantidad específica de un producto al carrito del usuario.
     *
     * @param username El nombre del usuario.
     * @param productId El ID del producto a añadir.
     * @param quantityToAdd La cantidad del producto a añadir.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    boolean addProductToCart(String username, int productId, int quantityToAdd);

    // Aquí se podrían añadir otras operaciones coordinadas por el mediador,
    // como removeProductFromCart, updateQuantityInCart, clearCart, etc.
}