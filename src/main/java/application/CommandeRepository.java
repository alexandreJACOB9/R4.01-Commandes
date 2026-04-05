package application;

import domain.Commande;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository {
    Commande save(Commande commande);
    List<Commande> findAll();
    Optional<Commande> findById(Long id);
    boolean deleteById(Long id);
}