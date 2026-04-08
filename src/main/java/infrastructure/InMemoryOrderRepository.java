package infrastructure;

import application.OrderRepository;
import domain.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation en memoire du depot des commandes.
 */
public class InMemoryOrderRepository implements OrderRepository {
    private static final List<Order> storage = new ArrayList<>();
    private static final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGen.getAndIncrement());
        }
        storage.add(order);
        return order;
    }

    @Override
    public List<Order> findAll() { return new ArrayList<>(storage); }

    @Override
    public Optional<Order> findById(Long id) {
        return storage.stream().filter(order -> order.getId().equals(id)).findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.removeIf(order -> order.getId().equals(id));
    }
}
