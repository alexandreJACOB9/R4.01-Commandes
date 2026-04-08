package infrastructure;

import application.CommandeRepository;
import domain.Commande;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommandeRepository implements CommandeRepository {
    private final String url = System.getenv("DB_HOST");
    private final String user = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASS");

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Force le chargement du driver
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Commande save(Commande c) {
        String sql = "INSERT INTO commandes (abonne_id, date_commande, adresse_livraison, date_livraison, prix_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, c.getAbonneId());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(c.getDateCommande()));
            ps.setString(3, c.getAdresseLivraison());
            ps.setDate(4, java.sql.Date.valueOf(c.getDateLivraison()));
            ps.setDouble(5, c.getPrixTotal());
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        c.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public List<Commande> findAll() {
        List<Commande> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM commandes")) {
            while (rs.next()) {
                Commande c = new Commande();
                c.setId(rs.getLong("id"));
                c.setAbonneId(rs.getLong("abonne_id"));
                c.setAdresseLivraison(rs.getString("adresse_livraison"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Optional<Commande> findById(Long id) {
        return findAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM commandes WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}