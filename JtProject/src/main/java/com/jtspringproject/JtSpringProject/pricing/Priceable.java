package com.jtspringproject.JtSpringProject.pricing;

/**
 * Componente: Define la interfaz para objetos que pueden tener precios
 * calculados y a los que se les pueden añadir responsabilidades dinámicamente.
 */
public interface Priceable {

    /**
     * Obtiene la descripción acumulada (producto base + decoradores).
     *
     * @return Descripción del item con sus modificadores.
     */
    String getDescription();

    /**
     * Calcula el precio final, potencialmente modificado por decoradores.
     *
     * @return El precio calculado.
     */
    double calculatePrice();
}
