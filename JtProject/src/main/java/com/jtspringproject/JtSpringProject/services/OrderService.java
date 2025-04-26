// Crear en: src/main/java/com/jtspringproject/JtSpringProject/services/OrderService.java
package com.jtspringproject.JtSpringProject.services;

import com.jtspringproject.JtSpringProject.payment.PaymentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    @Autowired
    private PaymentFactory paymentFactory;
    
    // Aquí iría la inyección del repositorio para órdenes si existiera
    // @Autowired
    // private OrderRepository orderRepository;
    
    public String createOrder(double amount, String userId, String paymentMethod, String paymentInfo) {
        // 1. Generar ID de orden
        String orderId = "ORD-" + System.currentTimeMillis();
        
        // 2. Obtener el servicio de pago adecuado
        PaymentService paymentService = paymentFactory.getPaymentService(paymentMethod);
        
        // 3. Procesar el pago
        String transactionId = paymentService.processPayment(amount, paymentInfo, orderId);
        
        if (transactionId != null) {
            // 4. Guardar la orden en la base de datos (código real iría aquí)
            System.out.println("Orden creada: " + orderId + " con transacción: " + transactionId);
            
            // 5. Devolver el ID de la orden
            return orderId;
        } else {
            System.err.println("Error al procesar el pago para la orden: " + orderId);
            return null;
        }
    }
    
    public boolean cancelOrder(String orderId, String transactionId) {
        // Aquí normalmente obtendríamos la orden de la base de datos
        // Order order = orderRepository.findById(orderId);
        
        // Para este ejemplo, asumimos que el método de pago es PayPal
        String paymentMethod = "paypal"; // En un caso real, se obtendría de la orden
        
        // Obtener servicio de pago
        PaymentService paymentService = paymentFactory.getPaymentService(paymentMethod);
        
        // Solicitar reembolso
        boolean refunded = paymentService.refundPayment(transactionId);
        
        if (refunded) {
            // Actualizar estado de la orden en la base de datos
            System.out.println("Orden " + orderId + " cancelada y reembolsada");
            return true;
        } else {
            System.err.println("No se pudo reembolsar la orden: " + orderId);
            return false;
        }
    }
}