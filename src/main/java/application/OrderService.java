package application;

import domain.Order;
import domain.OrderLine;
import infrastructure.OrderInput;
import infrastructure.OrderLineInput;
import infrastructure.OrderUpdateInput;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Porte les regles metier principales de gestion des commandes.
 */
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(OrderInput input) throws Exception {
        if (LocalDate.parse(input.deliveryDate).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The delivery date cannot be in the past.");
        }
        Client client = ClientBuilder.newClient();
        try {
            Order order = new Order();
            order.setSubscriberId(input.subscriberId);
            order.setDeliveryAddress(input.deliveryAddress);
            order.setDeliveryDate(LocalDate.parse(input.deliveryDate));
            order.setOrderDate(LocalDateTime.now());
            List<OrderLine> lines = new ArrayList<>();
            for (OrderLineInput lineInput : input.lines) {
                JsonObject menu = client.target("http://localhost:3004/menus")
                        .path(lineInput.menuId.toString())
                        .request(MediaType.APPLICATION_JSON)
                        .get(JsonObject.class);

                OrderLine line = new OrderLine();
                line.setMenuId(lineInput.menuId);
                line.setMenuName(menu.getString("nom"));
                line.setUnitPrice(menu.getJsonNumber("prix").doubleValue());
                line.setQuantity(lineInput.quantity);
                line.setLinePrice(line.getUnitPrice() * line.getQuantity());

                lines.add(line);
            }
            order.setLines(lines);
            order.calculateTotalPrice();
            return repository.save(order);
        } finally {
            client.close();
        }
    }

    public List<Order> listOrders(Long subscriberId) {
        List<Order> all = repository.findAll();
        if (subscriberId != null) {
            return all.stream().filter(order -> order.getSubscriberId().equals(subscriberId)).toList();
        }
        return all;
    }

    public Optional<Order> findOrder(Long id) {
        return repository.findById(id);
    }

    public boolean deleteOrder(Long id) {
        return repository.deleteById(id);
    }

    public Optional<Order> updateOrder(Long id, OrderUpdateInput input) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isPresent()) {
            Order order = existing.get();
            order.setDeliveryAddress(input.deliveryAddress);
            order.setDeliveryDate(LocalDate.parse(input.deliveryDate));
            return Optional.of(repository.save(order));
        }
        return Optional.empty();
    }

}

