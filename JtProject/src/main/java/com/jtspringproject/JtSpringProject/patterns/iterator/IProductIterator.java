package com.jtspringproject.JtSpringProject.patterns.iterator;

import com.jtspringproject.JtSpringProject.models.Product;

/**
 * Interfaz Iterator: define las operaciones para recorrer una colección de productos.
 */
public interface IProductIterator {
    /**
     * Verifica si hay más elementos en la iteración.
     * @return true si hay más elementos, false en caso contrario.
     */
    boolean hasNext();

    /**
     * Devuelve el siguiente elemento en la iteración.
     * @return el siguiente Product.
     * @throws java.util.NoSuchElementException si no hay más elementos.
     */
    Product next();

    /**
     * (Opcional) Reinicia el iterador a la primera posición.
     */
    void reset();
}