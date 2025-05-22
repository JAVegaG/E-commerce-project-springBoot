package com.jtspringproject.JtSpringProject.patterns.iterator;

/**
 * Interfaz Aggregate (Colección): define un método para crear un iterador.
 */
public interface IProductCollection {
    /**
     * Crea un iterador para la colección de productos.
     * @return una instancia de IProductIterator.
     */
    IProductIterator createIterator();
}