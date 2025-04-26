// Crear en: src/main/java/com/jtspringproject/JtSpringProject/payment/StripeAdapter.java
package com.jtspringproject.JtSpringProject.payment;

import com.jtspringproject.JtSpringProject.services.PaymentService;

public class StripeAdapter implements PaymentService {

    private final StripeAPI stripeAPI;

    public StripeAdapter(String apiKey) {
        this.stripeAPI = new StripeAPI(apiKey);
    }

    @Override
    public String processPayment(double amount, String paymentInfo, String orderId) {
        try {
            // Convertir los parámetros al formato que espera Stripe
            String description = "Orden #" + orderId;

            // Llamar al método de StripeAPI
            return stripeAPI.createCharge(amount, paymentInfo, description);
        } catch (Exception e) {
            System.err.println("Error al procesar pago con Stripe: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean refundPayment(String transactionId) {
        try {
            return stripeAPI.issueRefund(transactionId);
        } catch (Exception e) {
            System.err.println("Error al reembolsar con Stripe: " + e.getMessage());
            return false;
        }
    }
}
