package com.jtspringproject.JtSpringProject.patterns.command;

import com.jtspringproject.JtSpringProject.services.productService;
// El modelo Product no es estrictamente necesario aquí si el servicio lo maneja internamente,
// pero es bueno tenerlo en mente si el comando necesitara más datos del producto.

/**
 * Comando Concreto: encapsula la acción de actualizar el stock de un producto.
 * Contiene una referencia al receptor (productService) y los parámetros
 * necesarios.
 */
public class UpdateStockCommand implements Command {

    private productService productService; // El receptor de la acción
    private int productId;
    private int quantityChange; // Positivo para añadir stock, negativo para reducir

    public UpdateStockCommand(productService productService, int productId, int quantityChange) {
        this.productService = productService;
        this.productId = productId;
        this.quantityChange = quantityChange;
    }

    @Override
    public void execute() {
        // Delega la ejecución de la acción al método correspondiente en el servicio receptor.
        productService.updateProductQuantity(productId, quantityChange);
    }

    public productService getProductService() {
        return productService;
    }

    public void setProductService(productService productService) {
        this.productService = productService;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }
}
