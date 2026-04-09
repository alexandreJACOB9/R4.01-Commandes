package application;

import domain.Order;
import java.util.List;
import java.util.Optional;

/**
 * Définit les opérations d'accès aux données des commandes (Contrat de persistance).
 */
public interface OrderRepository {

    /**
     * Sauvegarde une commande dans le système de persistance.
     * Si l'identifiant de la commande est nul, il s'agit d'une création, sinon d'une mise à jour.
     *
     * @param order La commande à sauvegarder.
     * @return La commande sauvegardée (incluant son identifiant généré en cas de création).
     */
    Order save(Order order);

    /**
     * Récupère l'ensemble des commandes existantes dans le système.
     *
     * @return Une liste contenant toutes les commandes, ou une liste vide s'il n'y en a aucune.
     */
    List<Order> findAll();

    /**
     * Recherche une commande spécifique à partir de son identifiant unique.
     *
     * @param id L'identifiant de la commande recherchée.
     * @return Un Optional contenant la commande si elle existe, ou Optional.empty() sinon.
     */
    Optional<Order> findById(Long id);

    /**
     * Supprime une commande du système ainsi que ses lignes associées.
     *
     * @param id L'identifiant de la commande à supprimer.
     * @return true si la commande a été supprimée avec succès, false si elle n'a pas été trouvée.
     */
    boolean deleteById(Long id);
}