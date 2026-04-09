package infrastructure;

import application.OrderRepository;
import domain.Order;

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
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Order save(Order order) {
        String sql = "INSERT INTO commande (abonne_id, date_commande, adresse_livraison, date_livraison, prix_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getSubscriberId());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(order.getOrderDate()));
            ps.setString(3, order.getDeliveryAddress());
            ps.setDate(4, java.sql.Date.valueOf(order.getDeliveryDate()));
            ps.setDouble(5, order.getTotalPrice());
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getLong(1));
                    }
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
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return findAll().stream().filter(order -> order.getId().equals(id)).findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM commande WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
