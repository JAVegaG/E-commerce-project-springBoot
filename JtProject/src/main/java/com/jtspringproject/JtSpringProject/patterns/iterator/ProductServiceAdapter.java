package com.jtspringproject.JtSpringProject.patterns.iterator;

import java.util.List;

import com.jtspringproject.JtSpringProject.models.Product; // Importa tu servicio de productos
import com.jtspringproject.JtSpringProject.services.productService;

/**
 * ConcreteAggregate (o un Adaptador para un Aggregate existente): Implementa
 * IProductCollection y utiliza productService para obtener los productos.
 */
public class ProductServiceAdapter implements IProductCollection {

    private productService productService; // Referencia al servicio existente

    public ProductServiceAdapter(productService productService) {
        this.productService = productService;
    }

    @Override
    public IProductIterator createIterator() {
        List<Product> productList = this.productService.getProducts(); // Obtiene la lista de productos del servicio
        return new ProductListIterator(productList); // Crea y devuelve el iterador concreto
    }

    // Opcionalmente, podrías añadir métodos para crear iteradores con filtros específicos:
    // public IProductIterator createIteratorFilteredByCategory(int categoryId) {
    //     List<Product> allProducts = productService.getProducts();
    //     List<Product> filteredProducts = allProducts.stream()
    //                                           .filter(p -> p.getCategory() != null && p.getCategory().getId() == categoryId)
    //                                           .collect(java.util.stream.Collectors.toList());
    //     return new ProductListIterator(filteredProducts);
    // }
    public productService getProductService() {
        return productService;
    }

    public void setProductService(productService productService) {
        this.productService = productService;
    }
}
