// Crear en: src/main/java/com/jtspringproject/JtSpringProject/services/PaymentService.java
package com.jtspringproject.JtSpringProject.services;

public interface PaymentService {
    /**
     * Procesa un pago
     * @param amount cantidad a pagar
     * @param paymentInfo información del pago (puede ser un token, número de tarjeta, etc.)
     * @param orderId identificador de la orden
     * @return ID de la transacción si el pago fue exitoso, null en caso contrario
     */
    String processPayment(double amount, String paymentInfo, String orderId);
    
    /**
     * Realiza un reembolso
     * @param transactionId ID de la transacción a reembolsar
     * @return true si el reembolso fue exitoso, false en caso contrario
     */
    boolean refundPayment(String transactionId);
}