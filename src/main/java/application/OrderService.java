package application;

import domain.Order;
import domain.OrderLine;
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
 * Crée une nouvelle commande en récupérant les informations des menus via l'API distante.
 * Calcule le prix total au moment de la commande.
 *
 * param input L'objet contenant les informations de la commande (abonné, adresse, lignes...).
 * return La commande créée et sauvegardée.
 * throws IllegalArgumentException Si la date de livraison est dans le passé.
 * throws Exception En cas d'erreur de communication avec l'API Menus ou la base de données.
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
    /**
     * Récupère la liste des commandes.
     * Si un identifiant d'abonné est fourni, la liste est filtrée pour ne retourner
     * que les commandes appartenant à cet abonné spécifique.
     *
     * @param subscriberId L'identifiant de l'abonné pour le filtrage (peut être null pour tout lister).
     * @return La liste des commandes correspondantes.
     */
    public List<Order> listOrders(Long subscriberId) {
        List<Order> all = repository.findAll();
        if (subscriberId != null) {
            return all.stream().filter(order -> order.getSubscriberId().equals(subscriberId)).toList();
        }
        return all;
    }

    /**
     * Recherche les informations complètes d'une commande spécifique.
     *
     * @param id L'identifiant unique de la commande.
     * @return Un Optional contenant la commande si elle est trouvée.
     */
    public Optional<Order> findOrder(Long id) {
        return repository.findById(id);
    }

    /**
     * Supprime une commande de manière définitive.
     *
     * @param id L'identifiant de la commande à supprimer.
     * @return true si la suppression a été effectuée, false si la commande n'existait pas.
     */
    public boolean deleteOrder(Long id) {
        return repository.deleteById(id);
    }

    /**
     * Met à jour les informations de livraison d'une commande existante.
     * Seules l'adresse de livraison et la date de livraison peuvent être modifiées via cette opération.
     *
     * @param id L'identifiant de la commande à modifier.
     * @param input L'objet contenant la nouvelle adresse et/ou nouvelle date de livraison.
     * @return Un Optional contenant la commande mise à jour, ou vide si l'identifiant n'existe pas.
     */
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

