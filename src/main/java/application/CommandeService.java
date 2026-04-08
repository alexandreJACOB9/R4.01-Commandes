package application;

import domain.Commande;
import domain.LigneCommande;
import infrastructure.CommandeInput;
import infrastructure.CommandeUpdateInput;
import infrastructure.LigneCommandeInput;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandeService {
    private final CommandeRepository repository;

    public CommandeService(CommandeRepository repository) {
        this.repository = repository;
    }

    public Commande creerCommande(CommandeInput input) throws Exception {
        if (LocalDate.parse(input.dateLivraison).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de livraison ne peut pas être passée.");
        }

        Client client = ClientBuilder.newClient();
        try {
            Commande cmd = new Commande();
            cmd.setAbonneId(input.abonneId);
            cmd.setAdresseLivraison(input.adresseLivraison);
            cmd.setDateLivraison(LocalDate.parse(input.dateLivraison));
            cmd.setDateCommande(LocalDateTime.now());

            List<LigneCommande> lignes = new ArrayList<>();
            double total = 0;

            for (LigneCommandeInput lin : input.lignes) {
                JsonObject menu = client.target("http://localhost:3004/menus")
                        .path(lin.menuId.toString())
                        .request(MediaType.APPLICATION_JSON)
                        .get(JsonObject.class);

                LigneCommande lc = new LigneCommande();
                lc.setMenuId(lin.menuId);
                lc.setMenuNom(menu.getString("nom"));
                lc.setPrixUnitaire(menu.getJsonNumber("prix").doubleValue());
                lc.setQuantite(lin.quantite);
                lc.setPrixLigne(lc.getPrixUnitaire() * lc.getQuantite());

                lignes.add(lc);
                total += lc.getPrixLigne();
            }
            cmd.setLignes(lignes);
            cmd.setPrixTotal(total);

            return repository.save(cmd);
        } finally { client.close(); }
    }

    public List<Commande> lister(Long abonneId) {
        List<Commande> all = repository.findAll();
        if (abonneId != null) {
            return all.stream().filter(c -> c.getAbonneId().equals(abonneId)).toList();
        }
        return all;
    }

    public Optional<Commande> trouver(Long id) {
        return repository.findById(id);
    }

    public boolean supprimer(Long id) {
        return repository.deleteById(id);
    }

    public Optional<Commande> modifier(Long id, CommandeUpdateInput input) {
        Optional<Commande> opt = repository.findById(id);
        if (opt.isPresent()) {
            Commande cmd = opt.get();
            cmd.setAdresseLivraison(input.adresseLivraison);
            cmd.setDateLivraison(LocalDate.parse(input.dateLivraison));
            return Optional.of(repository.save(cmd));
        }
        return Optional.empty();
    }
}