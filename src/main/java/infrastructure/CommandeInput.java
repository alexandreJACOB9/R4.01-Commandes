package infrastructure;

import java.util.List;

public class CommandeInput {
    public Long abonneId;
    public String adresseLivraison;
    public String dateLivraison; // Format "YYYY-MM-DD"
    public List<LigneCommandeInput> lignes;
}