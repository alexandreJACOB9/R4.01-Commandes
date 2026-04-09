package application;

import java.util.List;

/**
 * Modèle d'entrée pour la création d'une commande via l'API REST.
 */
public class OrderInput {
    /** Identifiant de l'abonné passant la commande. */
    public Long subscriberId;

    /** Adresse postale complète pour la livraison. */
    public String deliveryAddress;

    /** Date souhaitée pour la livraison au format YYYY-MM-DD. */
    public String deliveryDate; // YYYY-MM-DD

    /** Liste des plats (menus) commandés avec leurs quantités. */
    public List<OrderLineInput> lines;
}

