package application;

import java.util.List;

/**
 * Modele d'entree pour la creation d'une commande.
 */
public class OrderInput {
    public Long subscriberId;
    public String deliveryAddress;
    public String deliveryDate; // YYYY-MM-DD
    public List<OrderLineInput> lines;
}

