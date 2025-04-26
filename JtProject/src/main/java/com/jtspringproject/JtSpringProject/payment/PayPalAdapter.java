// Crear en: src/main/java/com/jtspringproject/JtSpringProject/payment/PayPalAdapter.java
package com.jtspringproject.JtSpringProject.payment;

import com.jtspringproject.JtSpringProject.services.PaymentService;

public class PayPalAdapter implements PaymentService {

    private final PayPalAPI payPalAPI;

    public PayPalAdapter(String apiKey, String secretKey) {
        this.payPalAPI = new PayPalAPI(apiKey, secretKey);
    }

    @Override
    public String processPayment(double amount, String paymentInfo, String orderId) {
        try {
            // Convertir los parámetros al formato que espera PayPal
            String currency = "USD";
            String description = "Orden #" + orderId;

            // Llamar al método de PayPalAPI
            return payPalAPI.makePayment(amount, currency, description);
        } catch (Exception e) {
            System.err.println("Error al procesar pago con PayPal: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean refundPayment(String transactionId) {
        try {
            return payPalAPI.cancelPayment(transactionId);
        } catch (Exception e) {
            System.err.println("Error al reembolsar con PayPal: " + e.getMessage());
            return false;
        }
    }
}
