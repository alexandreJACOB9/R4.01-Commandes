package infrastructure;

import application.OrderRepository;
import domain.Order;
import domain.OrderLine;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Implementation JDBC du depot des commandes.
 */
public class JdbcOrderRepository implements OrderRepository {
    private final String url;
    private final String user;
    private final String password;

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public JdbcOrderRepository() {
        Properties props = loadConfig();
        this.url = props.getProperty("DB_URL");
        this.user = props.getProperty("DB_USER");
        this.password = props.getProperty("DB_PASS");
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.env")) {
            if (input == null) {
                throw new RuntimeException("Impossible de charger le fichier config.env");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier config.env", e);
        }
        return props;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Order save(Order order) {
        String sqlOrder = "INSERT INTO commande (abonne_id, date_commande, adresse_livraison, date_livraison, prix_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

            psOrder.setLong(1, order.getSubscriberId());
            psOrder.setTimestamp(2, java.sql.Timestamp.valueOf(order.getOrderDate()));
            psOrder.setString(3, order.getDeliveryAddress());
            psOrder.setDate(4, java.sql.Date.valueOf(order.getDeliveryDate()));
            psOrder.setDouble(5, order.getTotalPrice());
            int affectedRows = psOrder.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getLong(1));
                    }
                }
            }
            // Sauvegarde les lignes de commande
            if (order.getId() != null && order.getLines() != null && !order.getLines().isEmpty()) {
                String sqlLine = "INSERT INTO ligne_commande (commande_id, menu_id, menu_nom, quantite, prix_unitaire, prix_ligne) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psLine = conn.prepareStatement(sqlLine)) {
                    for (OrderLine line : order.getLines()) {
                        psLine.setLong(1, order.getId());
                        psLine.setLong(2, line.getMenuId());
                        psLine.setString(3, line.getMenuName());
                        psLine.setInt(4, line.getQuantity());
                        psLine.setDouble(5, line.getUnitPrice());
                        psLine.setDouble(6, line.getLinePrice());
                        psLine.addBatch();
                    }
                    psLine.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM commande")) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setSubscriberId(rs.getLong("abonne_id"));
                order.setDeliveryAddress(rs.getString("adresse_livraison"));
                order.setOrderDate(rs.getTimestamp("date_commande").toLocalDateTime());
                order.setDeliveryDate(rs.getDate("date_livraison").toLocalDate());
                order.setTotalPrice(rs.getDouble("prix_total"));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Récupere les lignes de chaque commande
        if (!list.isEmpty()) {
            try (Connection conn = getConnection()) {
                String sqlLine = "SELECT * FROM ligne_commande WHERE commande_id = ?";
                try (PreparedStatement psLine = conn.prepareStatement(sqlLine)) {
                    for (Order order : list) {
                        psLine.setLong(1, order.getId());
                        try (ResultSet rsLine = psLine.executeQuery()) {
                            List<OrderLine> lines = new ArrayList<>();
                            while (rsLine.next()) {
                                OrderLine line = new OrderLine();
                                line.setMenuId(rsLine.getLong("menu_id"));
                                line.setMenuName(rsLine.getString("menu_nom"));
                                line.setQuantity(rsLine.getInt("quantite"));
                                line.setUnitPrice(rsLine.getDouble("prix_unitaire"));
                                line.setLinePrice(rsLine.getDouble("prix_ligne"));
                                lines.add(line);
                            }
                            order.setLines(lines);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return findAll().stream().filter(order -> order.getId().equals(id)).findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = getConnection()) {
            // Suppression des lignes avant la commande pour évietr les problèmes de contrainte de clef etrangere
            try (PreparedStatement psLines = conn.prepareStatement("DELETE FROM ligne_commande WHERE commande_id = ?")) {
                psLines.setLong(1, id);
                psLines.executeUpdate();
            }
            try (PreparedStatement psOrder = conn.prepareStatement("DELETE FROM commande WHERE id = ?")) {
                psOrder.setLong(1, id);
                return psOrder.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
