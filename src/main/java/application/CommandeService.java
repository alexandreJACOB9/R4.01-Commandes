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
import java.util.concurrent.atomic.AtomicLong;

public class CommandeService {
    private static List<Commande> database = new ArrayList<>();
    private static AtomicLong idGenerator = new AtomicLong(1);

    public List<Commande> listerToutesLesCommandes(Long abonneId) {
        if (abonneId != null) {
            return database.stream().filter(c -> c.getAbonneId().equals(abonneId)).toList();
        }
        return database;
    }
    public Optional<Commande> recupererParId(Long id) {
        return database.stream().filter(c -> c.getId().equals(id)).findFirst();
    }
    public boolean supprimerCommande(Long id) {
        return database.removeIf(c -> c.getId().equals(id));
    }
    public Optional<Commande> modifierCommande(Long id, CommandeUpdateInput input) {
        Optional<Commande> optCmd = recupererParId(id);
        if (optCmd.isPresent()) {
            Commande cmd = optCmd.get();
            cmd.setAdresseLivraison(input.adresseLivraison);
            cmd.setDateLivraison(LocalDate.parse(input.dateLivraison));
            return Optional.of(cmd);
        }
        return Optional.empty();
    }


    public Commande creerNouvelleCommande(CommandeInput input) throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Commande cmd = new Commande();
            cmd.setId(idGenerator.getAndIncrement());
            cmd.setAbonneId(input.abonneId);
            cmd.setAdresseLivraison(input.adresseLivraison);
            cmd.setDateLivraison(LocalDate.parse(input.dateLivraison));
            cmd.setDateCommande(LocalDateTime.now());

            List<LigneCommande> lignes = new ArrayList<>();
            double total = 0;

            for (LigneCommandeInput lin : input.lignes) {
                JsonObject menuData = client.target("http://localhost:3004/menus")
                        .path(lin.menuId.toString())
                        .request(MediaType.APPLICATION_JSON)
                        .get(JsonObject.class);

                LigneCommande lc = new LigneCommande();
                lc.setMenuId(lin.menuId);
                lc.setMenuNom(menuData.getString("nom"));
                lc.setPrixUnitaire(menuData.getJsonNumber("prix").doubleValue());
                lc.setQuantite(lin.quantite);
                lc.setPrixLigne(lc.getPrixUnitaire() * lc.getQuantite());

                lignes.add(lc);
                total += lc.getPrixLigne();
            }

            cmd.setLignes(lignes);
            cmd.setPrixTotal(total);
            database.add(cmd);
            return cmd;
        } finally {
            client.close();
        }
    }
}