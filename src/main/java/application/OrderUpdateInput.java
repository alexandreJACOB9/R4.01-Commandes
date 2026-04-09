package application;

/**
 * Modèle de données restreint pour la mise à jour des informations de livraison. */
public class OrderUpdateInput {
    /** Nouvelle adresse de livraison. */
    public String deliveryAddress;

    /** Nouvelle date de livraison prévue */
    public String deliveryDate; // YYYY-MM-DD
}

