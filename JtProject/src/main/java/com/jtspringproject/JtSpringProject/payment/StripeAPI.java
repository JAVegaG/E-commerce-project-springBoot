// Crear en: src/main/java/com/jtspringproject/JtSpringProject/payment/StripeAPI.java
package com.jtspringproject.JtSpringProject.payment;

public class StripeAPI {

    private String apiKey;

    public StripeAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public String createCharge(double amount, String token, String description) {
        // Código real para conectar con API de Stripe
        System.out.println("Stripe: Creando cargo de " + amount);
        return "ST-" + System.currentTimeMillis(); // ID de cargo simulado
    }

    public boolean issueRefund(String chargeId) {
        // Código real para emitir un reembolso
        System.out.println("Stripe: Reembolsando cargo " + chargeId);
        return true;
    }
}
