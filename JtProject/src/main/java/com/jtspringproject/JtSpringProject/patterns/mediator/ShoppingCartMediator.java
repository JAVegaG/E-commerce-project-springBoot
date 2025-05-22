package com.jtspringproject.JtSpringProject.patterns.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.cartService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService; // Para inyección si se hace un @Service

/**
 * ConcreteMediator: implementa ICartMediator. Conoce y coordina a los servicios
 * colegas (userService, productService, cartService).
 */
// @Component // Descomentar si quieres que Spring gestione este mediador como un bean
public class ShoppingCartMediator implements ICartMediator {

    private final userService userService;
    private final productService productService;
    private final cartService cartService;

    /**
     * Constructor para inyectar los servicios colegas. Si ShoppingCartMediator
     * es un bean de Spring (@Component o @Service), Spring inyectará
     * automáticamente estas dependencias si también son beans.
     */
    @Autowired // Opcional si solo hay un constructor y las dependencias son beans
    public ShoppingCartMediator(userService userService,
            productService productService,
            cartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    @Override
    @Transactional // Es crucial que toda la operación sea transaccional
    public boolean addProductToCart(String username, int productId, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            System.err.println("Mediator Error: La cantidad a añadir debe ser mayor que cero.");
            return false;
        }

        // 1. Obtener el usuario
        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.err.println("Mediator Error: Usuario '" + username + "' no encontrado.");
            return false;
        }

        // 2. Obtener el producto
        Product product = productService.getProduct(productId);
        if (product == null) {
            System.err.println("Mediator Error: Producto con ID " + productId + " no encontrado.");
            return false;
        }

        // 3. Verificar stock disponible del producto
        if (product.getQuantity() < quantityToAdd) { // product.getQuantity() es el stock total
            System.err.println("Mediator Error: Stock insuficiente para el producto '" + product.getName()
                    + "'. Stock disponible: " + product.getQuantity() + ", Solicitado: " + quantityToAdd);
            return false;
        }

        // 4. Obtener o crear el carrito para el usuario (lógica en cartService)
        Cart cart = cartService.getOrCreateCartForUser(user);
        if (cart == null) {
            System.err.println("Mediator Error: No se pudo obtener o crear el carrito para el usuario: " + username);
            return false;
        }

        // 5. Añadir/actualizar producto en el carrito (lógica en cartService)
        // cartService se encargará de ver si CartProduct ya existe o si es nuevo.
        boolean cartUpdated = cartService.addProductOrUpdateQuantityInCart(cart, product, quantityToAdd);

        if (!cartUpdated) {
            System.err.println("Mediator Error: cartService no pudo actualizar el carrito.");
            // No se debería llegar aquí si las validaciones anteriores y la lógica de cartService son correctas.
            // Podría ser un problema de persistencia en cartService.
            return false;
        }

        // 6. Reducir el stock del producto.
        // Es importante que productService.updateProductQuantity maneje la persistencia.
        // Esta es la parte donde la corrección de productDao.updateProduct y la existencia
        // de productService.updateProductQuantity (del patrón Command) son cruciales.
        productService.updateProductQuantity(productId, -quantityToAdd); // Resta la cantidad del stock

        System.out.println("Mediator Success: " + quantityToAdd + " unidad(es) del producto '" + product.getName()
                + "' añadidas/actualizadas en el carrito del usuario '" + username + "'.");
        return true;
    }
}
