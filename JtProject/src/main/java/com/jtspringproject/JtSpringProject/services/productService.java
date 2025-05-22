package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

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

    /**
     * Nuevo método para actualizar la cantidad de stock de un producto. Este
     * será el método invocado por el UpdateStockCommand. Es importante que sea
     * transaccional ya que modifica la base de datos.
     *
     * @param productId El ID del producto a actualizar.
     * @param quantityChange La cantidad a añadir (si es positiva) o restar (si
     * es negativa).
     * @return El producto actualizado, o null si el producto no se encontró.
     */
    @Transactional // Asegura la atomicidad de la operación de lectura y escritura.
    public Product updateProductQuantity(int productId, int quantityChange) {
        Product product = this.productDao.getProduct(productId); // Obtener el producto desde la BD
        if (product != null) {
            int currentQuantity = product.getQuantity();
            int newQuantity = currentQuantity + quantityChange;

            if (newQuantity < 0) {
                // Manejar el error: no se puede tener stock negativo.
                System.err.println("Error: Intento de establecer cantidad de stock negativa (" + newQuantity + ") para el producto ID " + productId);

                return product;
            }
            product.setQuantity(newQuantity);
            Product updatedProduct = this.productDao.updateProduct(product);
            System.out.println("Stock actualizado para producto ID " + productId + ". Nueva cantidad: " + newQuantity);
            return updatedProduct;
        } else {
            System.err.println("Error: Producto no encontrado con ID " + productId + " para actualizar cantidad de stock.");
            return null;
        }
    }
}
