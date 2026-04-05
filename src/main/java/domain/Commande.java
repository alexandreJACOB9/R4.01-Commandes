package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Commande {
    private Long id;
    private Long abonneId;
    private LocalDateTime dateCommande;
    private String adresseLivraison;
    private LocalDate dateLivraison;
    private List<LigneCommande> lignes;
    private Double prixTotal;

    public Commande() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAbonneId() { return abonneId; }
    public void setAbonneId(Long abonneId) { this.abonneId = abonneId; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    public LocalDate getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDate dateLivraison) { this.dateLivraison = dateLivraison; }
    public List<LigneCommande> getLignes() { return lignes; }
    public void setLignes(List<LigneCommande> lignes) { this.lignes = lignes; }
    public Double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }
}