package domain;

/**
 * Decrit une ligne de commande avec menu, quantite et montants.
 */
public class OrderLine {
    private Long menuId;
    private String menuName;
    private Integer quantity;
    private Double unitPrice;
    private Double linePrice;

    public OrderLine() {}

    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getLinePrice() { return linePrice; }
    public void setLinePrice(Double linePrice) { this.linePrice = linePrice; }
}

