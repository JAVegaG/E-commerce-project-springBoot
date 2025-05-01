package com.jtspringproject.JtSpringProject.pricing;

/**
 * Decorador Base (Abstracto): Mantiene la referencia al objeto envuelto y
 * delega las operaciones a él por defecto.
 */
public abstract class PriceDecorator implements Priceable {

    protected Priceable wrappedPriceable; // protected para acceso de subclases

    public PriceDecorator(Priceable wrappedPriceable) {
        this.wrappedPriceable = wrappedPriceable;
    }

    /**
     * Delega la llamada al objeto envuelto por defecto. Los decoradores
     * concretos pueden sobrescribirlo.
     */
    @Override
    public String getDescription() {
        return wrappedPriceable.getDescription();
    }

    /**
     * Delega la llamada al objeto envuelto por defecto. Los decoradores
     * concretos DEBEN sobrescribirlo para añadir su lógica.
     */
    @Override
    public double calculatePrice() {
        return wrappedPriceable.calculatePrice();
    }
}
