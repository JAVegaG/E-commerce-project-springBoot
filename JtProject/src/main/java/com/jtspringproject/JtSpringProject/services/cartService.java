package com.jtspringproject.JtSpringProject.services;

import com.jtspringproject.JtSpringProject.dao.cartDao;
import com.jtspringproject.JtSpringProject.dao.cartProductDao; // Necesario
import com.jtspringproject.JtSpringProject.models.Cart;
// import com.jtspringproject.JtSpringProject.models.Category; // No se usa directamente aquí
import com.jtspringproject.JtSpringProject.models.Product;   // Necesario
import com.jtspringproject.JtSpringProject.models.User;      // Necesario
import com.jtspringproject.JtSpringProject.models.CartProduct; // Necesario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections; // Para lista vacía
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para el stream().collect()

@Service
public class cartService {

    @Autowired
    private cartDao cartDao;

    @Autowired
    private cartProductDao cartProductDao; // Inyectar el DAO para CartProduct

    // No necesitamos productService aquí si el Mediator ya se encarga de la lógica de stock.
    // @Autowired
    // private productService productService;

    @Transactional
    public Cart addCart(Cart cart) { // Este método ya lo tenías
        return cartDao.addCart(cart);
    }

    @Transactional(readOnly = true)
    public List<Cart> getCarts() { // Este método ya lo tenías
        return this.cartDao.getCarts();
    }

    @Transactional
    public void updateCart(Cart cart) { // Este método ya lo tenías
        cartDao.updateCart(cart);
    }

    @Transactional
    public void deleteCart(Cart cart) { // Este método ya lo tenías
        // Considerar borrar también los CartProducts asociados si es necesario (CASCADE en BD o manualmente)
        List<CartProduct> itemsInCart = getCartProductsByCartId(cart.getId());
        for (CartProduct item : itemsInCart) {
            cartProductDao.deleteCartProduct(item);
        }
        cartDao.deleteCart(cart);
    }

    /**
     * Obtiene el carrito activo de un usuario o crea uno nuevo si no existe.
     * @param user El usuario para el cual obtener/crear el carrito.
     * @return El Cart del usuario.
     */
    @Transactional
    public Cart getOrCreateCartForUser(User user) {
        // Idealmente, cartDao debería tener un método como: findByUserIdAndStatus(int userId, String status)
        // Para este ejemplo, asumimos que un usuario solo tiene un carrito o tomamos el primero que encontremos.
        // Esta lógica es simplificada.
        List<Cart> userCarts = cartDao.getCarts().stream()
                                      .filter(c -> c.getCustomer() != null && c.getCustomer().getId() == user.getId())
                                      .collect(Collectors.toList());

        if (!userCarts.isEmpty()) {
            return userCarts.get(0); // Devuelve el primer carrito encontrado para el usuario
        } else {
            Cart newCart = new Cart();
            newCart.setCustomer(user);
            return cartDao.addCart(newCart);
        }
    }

    /**
     * Busca un CartProduct específico en un carrito dado para un producto dado.
     * @param cart El carrito donde buscar.
     * @param product El producto a buscar.
     * @return Un Optional<CartProduct> que puede contener el CartProduct si se encuentra.
     */
    @Transactional(readOnly = true)
    public Optional<CartProduct> findCartProductInCart(Cart cart, Product product) {
        // cartProductDao debería tener un método: findByCartIdAndProductId(int cartId, int productId)
        // Simulación con la lista completa (ineficiente para muchos CartProducts):
        return cartProductDao.getCartProducts().stream()
                             .filter(cp -> cp.getCart().getId() == cart.getId() &&
                                           cp.getProduct().getId() == product.getId())
                             .findFirst();
    }

    /**
     * Añade un producto al carrito o actualiza su cantidad si ya existe.
     * @param cart El carrito del usuario.
     * @param product El producto a añadir/actualizar.
     * @param quantityToAdd La cantidad a añadir al carrito (no la cantidad total final).
     * @return true si la operación fue exitosa.
     */
    @Transactional
    public boolean addProductOrUpdateQuantityInCart(Cart cart, Product product, int quantityToAdd) {
        if (cart == null || product == null || quantityToAdd <= 0) {
            return false;
        }

        Optional<CartProduct> existingCartProductOpt = findCartProductInCart(cart, product);

        if (existingCartProductOpt.isPresent()) {
            // El producto ya está en el carrito, actualizar cantidad
            CartProduct cartProduct = existingCartProductOpt.get();
            int newQuantity = cartProduct.getQuantity() + quantityToAdd;
            cartProduct.setQuantity(newQuantity);
            cartProductDao.updateCartProduct(cartProduct); // Asumiendo que este método persiste el cambio
        } else {
            // El producto no está en el carrito, crear nueva entrada CartProduct
            CartProduct newCartProduct = new CartProduct(cart, product, quantityToAdd);
            cartProductDao.addCartProduct(newCartProduct); // Asumiendo que este método persiste la nueva entrada
        }
        return true;
    }

    /**
     * Obtiene todos los CartProduct (ítems) asociados a un ID de carrito.
     * @param cartId El ID del carrito.
     * @return Una lista de CartProduct.
     */
    @Transactional(readOnly = true)
    public List<CartProduct> getCartProductsByCartId(int cartId) {
        // cartProductDao debería tener un método como: findAllByCartId(int cartId)
        // Si no, simulamos filtrando la lista completa (ineficiente).
        // La consulta SQL que tenías en cartProductDao.getProductByCartID devolvía List<Product>,
        // necesitamos List<CartProduct> para obtener también la cantidad de cada ítem en el carrito.
        if (cartProductDao.getCartProducts() == null) return Collections.emptyList();

        return cartProductDao.getCartProducts().stream()
                             .filter(cp -> cp.getCart() != null && cp.getCart().getId() == cartId)
                             .collect(Collectors.toList());
    }

    /**
     * Elimina un producto (CartProduct) del carrito.
     * @param cart El carrito del usuario.
     * @param product El producto a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    @Transactional
    public boolean removeProductFromCart(Cart cart, Product product) {
        Optional<CartProduct> cartProductOpt = findCartProductInCart(cart, product);
        if (cartProductOpt.isPresent()) {
            cartProductDao.deleteCartProduct(cartProductOpt.get());
            return true;
        }
        return false;
    }
}