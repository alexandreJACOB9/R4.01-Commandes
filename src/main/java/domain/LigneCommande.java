package domain;

public class LigneCommande {
    private Long menuId;
    private String menuNom; // Snapshot du nom au moment de la commande
    private Integer quantite;
    private Double prixUnitaire; // Snapshot du prix au moment de la commande
    private Double prixLigne; // prixUnitaire * quantite

    public LigneCommande() {}

    // Getters et Setters
    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public String getMenuNom() { return menuNom; }
    public void setMenuNom(String menuNom) { this.menuNom = menuNom; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public Double getPrixLigne() { return prixLigne; }
    public void setPrixLigne(Double prixLigne) { this.prixLigne = prixLigne; }
}