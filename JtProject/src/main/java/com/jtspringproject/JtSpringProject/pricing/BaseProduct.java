package com.jtspringproject.JtSpringProject.pricing;

import com.jtspringproject.JtSpringProject.models.Product;

/**
 * Componente Concreto: Representa el objeto base al que se añadirán
 * decoradores. En este caso, envuelve la entidad Product existente.
 */
public class BaseProduct implements Priceable {

    private final Product productEntity; // Referencia a tu entidad original

    public BaseProduct(Product productEntity) {
        if (productEntity == null) {
            throw new IllegalArgumentException("Product entity cannot be null");
        }
        this.productEntity = productEntity;
    }

    @Override
    public String getDescription() {
        // Devuelve el nombre o descripción básica del producto
        return productEntity.getName();
    }

    @Override
    public double calculatePrice() {
        // Devuelve el precio base del producto de la entidad
        // Asumiendo que tu Product tiene un método getPrice() o un campo price
        // Ajusta esto según tu modelo Product exacto.
        // Ejemplo: return productEntity.getPrice();
        return productEntity.getPrice(); // ¡Asegúrate que este método/campo exista!
    }

    // Opcional: Método para acceder a la entidad original si es necesario
    public Product getProductEntity() {
        return productEntity;
    }
}
