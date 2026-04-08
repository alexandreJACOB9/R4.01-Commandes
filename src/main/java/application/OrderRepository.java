package application;

import domain.Order;
import java.util.List;
import java.util.Optional;

/**
 * Definit les operations d'acces aux commandes.
 */
public interface OrderRepository {
    Order save(Order order);
    List<Order> findAll();
    Optional<Order> findById(Long id);
    boolean deleteById(Long id);
}

