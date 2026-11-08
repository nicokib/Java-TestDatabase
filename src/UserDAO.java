import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    // ==========================================
    // 1. CREATE : Insérer un utilisateur (avec mot de passe haché)
    // ==========================================
    public static boolean create(User user) {
        String sql = "INSERT INTO users (email, password, first_name, last_name, birthday) VALUES (?, ?, ?, ?, ?)";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getEmail());
            // Sécurité : On hache le mot de passe avant l'insertion en BDD
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            statement.setString(2, hashedPassword);

            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setDate(5, Date.valueOf(user.getBirthday()));

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                // Récupération de l'ID généré automatiquement par MySQL
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    // ==========================================
    // 2. READ : Récupérer un utilisateur par son ID
    // ==========================================
    public static User findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur : " + e.getMessage());
        }
        return null; // Retourne null si aucun utilisateur n'est trouvé
    }

    public static User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur : " + e.getMessage());
        }
        return null; // Retourne null si aucun utilisateur n'est trouvé
    }

    // READ (Bonus) : Récupérer TOUS les utilisateurs
    public static List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }



    // ==========================================
    // 3. UPDATE : Modifier les informations d'un utilisateur
    // ==========================================
    public static boolean update(User user) {
        String sql = "UPDATE users SET email = ?, first_name = ?, last_name = ?, birthday = ? WHERE id = ?";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            statement.setLong(5, user.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 4. DELETE : Supprimer un utilisateur par son ID
    // ==========================================
    public static boolean delete(long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // MÉTHODE OUTIL : Transforme une ligne MySQL en objet User
    // ==========================================
    private static User mapRowToUser(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getString("email"),
                resultSet.getString("password"), // Contient le hash
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getDate("birthday").toLocalDate()
        );
        user.setId(resultSet.getLong("id"));
        return user;
    }
}