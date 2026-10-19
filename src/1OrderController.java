package com.labcorp.orders.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Order management controller.
 * Synthetic DLP test file - contains no real data.
 */
public class OrderController {

    private final ConcurrentHashMap<String, Order> orders = new ConcurrentHashMap<>();

    public static class Order {
        private final String id;
        private final String customerId;
        private final BigDecimal amount;
        private final Instant createdAt;
        private String status;

        public Order(String id, String customerId, BigDecimal amount) {
            this.id = id;
            this.customerId = customerId;
            this.amount = amount;
            this.createdAt = Instant.now();
            this.status = "NEW";
        }

        public String getId() { return id; }
        public String getCustomerId() { return customerId; }
        public BigDecimal getAmount() { return amount; }
        public Instant getCreatedAt() { return createdAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public Order createOrder(String customerId, BigDecimal amount) throws IllegalArgumentException {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String id = "ORD-" + System.nanoTime();
        Order order = new Order(id, customerId, amount);
        orders.put(id, order);
        return order;
    }

    public Optional<Order> findOrder(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> listByStatus(String status) {
        return orders.values().stream()
                .filter(o -> o.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public boolean cancelOrder(String id) {
        Order order = orders.get(id);
        if (order == null || "SHIPPED".equals(order.getStatus())) {
            return false;
        }
        order.setStatus("CANCELLED");
        return true;
    }

    public static void main(String[] args) {
        OrderController controller = new OrderController();
        Order o = controller.createOrder("CUST-0042", new BigDecimal("199.90"));
        System.out.println("Created " + o.getId() + " for " + o.getCustomerId());
        controller.cancelOrder(o.getId());
        System.out.println("Cancelled orders: " + controller.listByStatus("CANCELLED").size());
    }
}
