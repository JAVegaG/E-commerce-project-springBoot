// Crear en: src/main/java/com/jtspringproject/JtSpringProject/payment/PaymentFactory.java
package com.jtspringproject.JtSpringProject.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jtspringproject.JtSpringProject.services.PaymentService;

@Component
public class PaymentFactory {

    @Value("${paypal.api.key:dummy-paypal-key}")
    private String paypalApiKey;

    @Value("${paypal.secret.key:dummy-paypal-secret}")
    private String paypalSecretKey;

    @Value("${stripe.api.key:dummy-stripe-key}")
    private String stripeApiKey;

    public PaymentService getPaymentService(String paymentMethod) {
        switch (paymentMethod.toLowerCase()) {
            case "paypal":
                return new PayPalAdapter(paypalApiKey, paypalSecretKey);
            case "stripe":
                return new StripeAdapter(stripeApiKey);
            default:
                throw new IllegalArgumentException("Método de pago no soportado: " + paymentMethod);
        }
    }
}
