package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represente une commande passee par un abonne.
 */
public class Order {
    private Long id;
    private Long subscriberId;
    private LocalDateTime orderDate;
    private String deliveryAddress;
    private LocalDate deliveryDate;
    private List<OrderLine> lines;
    private Double totalPrice;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubscriberId() { return subscriberId; }
    public void setSubscriberId(Long subscriberId) { this.subscriberId = subscriberId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public List<OrderLine> getLines() { return lines; }
    public void setLines(List<OrderLine> lines) { this.lines = lines; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    /**
     * Calcule le prix total de la commande en additionnant le prix de chaque ligne.
     * Si la commande ne contient aucune ligne, le prix total est fixé à 0.0.
     */
    public void calculateTotalPrice() {
        if (this.lines == null || this.lines.isEmpty()) {
            this.totalPrice = 0.0;
            return;
        }
        this.totalPrice = this.lines.stream()
                .mapToDouble(OrderLine::getLinePrice)
                .sum();
    }
}
