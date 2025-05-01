package com.jtspringproject.JtSpringProject.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decorador Concreto: Añade la funcionalidad de aplicar un impuesto.
 */
public class TaxDecorator extends PriceDecorator {

    private final double taxRate; // Ej: 19.0 para 19% de IVA

    public TaxDecorator(Priceable wrappedPriceable, double taxRate) {
        super(wrappedPriceable);
        if (taxRate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }
        this.taxRate = taxRate;
    }

    @Override
    public String getDescription() {
        // Añade la información del impuesto a la descripción base
        return super.getDescription() + String.format(" (Impuesto: %.2f%%)", taxRate);
    }

    @Override
    public double calculatePrice() {
        // Obtiene el precio del objeto envuelto (que ya podría tener descuentos)
        double priceBeforeTax = super.calculatePrice();
        // Calcula el impuesto
        double taxAmount = priceBeforeTax * (taxRate / 100.0);
        // Añade el impuesto
        double finalPrice = priceBeforeTax + taxAmount;

        // Usar BigDecimal para precisión monetaria
        BigDecimal priceBD = BigDecimal.valueOf(finalPrice);
        return priceBD.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getTaxRate() {
        return taxRate;
    }
}
