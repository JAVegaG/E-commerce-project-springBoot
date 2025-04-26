// Crear en: src/main/java/com/jtspringproject/JtSpringProject/payment/PayPalAPI.java
package com.jtspringproject.JtSpringProject.payment;

public class PayPalAPI {

    private String apiKey;
    private String secretKey;

    public PayPalAPI(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String makePayment(double amount, String currency, String description) {
        // Código real para conectar con API de PayPal
        System.out.println("PayPal: Procesando pago de " + amount + " " + currency);
        return "PP-" + System.currentTimeMillis(); // ID de transacción simulado
    }

    public boolean cancelPayment(String paymentId) {
        // Código real para cancelar un pago
        System.out.println("PayPal: Cancelando pago " + paymentId);
        return true;
    }
}
