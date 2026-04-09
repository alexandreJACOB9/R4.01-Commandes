package application;

/**
 * Représente un item de menu et sa quantité dans une demande de commande.
 */
public class OrderLineInput {
    /** Identifiant du menu (doit exister dans le composant Menus). */
    public Long menuId;

    /** Nombre d'exemplaires commandés pour ce menu. */
    public Integer quantity;
}

