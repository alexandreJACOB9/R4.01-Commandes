package infrastructure;

import application.CommandeRepository;
import domain.Commande;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommandeRepository implements CommandeRepository {
    private static final List<Commande> storage = new ArrayList<>();
    private static final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Commande save(Commande c) {
        if (c.getId() == null) c.setId(idGen.getAndIncrement());
        storage.add(c);
        return c;
    }

    @Override
    public List<Commande> findAll() { return new ArrayList<>(storage); }

    @Override
    public Optional<Commande> findById(Long id) {
        return storage.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.removeIf(c -> c.getId().equals(id));
    }
}