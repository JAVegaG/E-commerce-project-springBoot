package com.jtspringproject.JtSpringProject.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decorador Concreto: Añade la funcionalidad de aplicar un descuento.
 */
public class DiscountDecorator extends PriceDecorator {

    private final double discountPercentage; // Ej: 10.0 para 10%

    public DiscountDecorator(Priceable wrappedPriceable, double discountPercentage) {
        super(wrappedPriceable);
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String getDescription() {
        // Añade la información del descuento a la descripción base
        return super.getDescription() + String.format(" (Descuento: %.2f%%)", discountPercentage);
    }

    @Override
    public double calculatePrice() {
        // Obtiene el precio del objeto envuelto
        double originalPrice = super.calculatePrice();
        // Calcula el descuento
        double discountAmount = originalPrice * (discountPercentage / 100.0);
        // Aplica el descuento
        double discountedPrice = originalPrice - discountAmount;

        // Usar BigDecimal para precisión monetaria es recomendable
        BigDecimal priceBD = BigDecimal.valueOf(discountedPrice);
        // Redondear a 2 decimales (típico para moneda)
        return priceBD.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
