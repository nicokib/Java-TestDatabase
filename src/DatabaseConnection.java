// Importation des classes de l'API JDBC nécessaires à la gestion des connexions SQL
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Déclaration d'une variable de classe (statique) pour stocker l'unique connexion.
    // Elle est privée pour empêcher toute modification externe directe.
    private static Connection con = null;

    // BLOC D'INITIALISATION STATIQUE : S'exécute une seule fois au chargement de la classe en mémoire.
    // C'est l'endroit idéal pour établir une connexion permanente (Singleton rudimentaire).
    static {
        // Définition des paramètres de connexion à la base de données MySQL
        String url = "jdbc:mysql://localhost:3306/db_test"; // URL JDBC (hôte: localhost, port: 3306, BDD: org)
        String user = "root";                            // Identifiant de l'administrateur MySQL
        String pass = "";                                // Mot de passe (vide par défaut sur certaines configurations)

        try {
            // Chargement dynamique du pilote (Driver) JDBC de MySQL en mémoire.
            // Note : Cette ligne permet à DriverManager de savoir quel pilote utiliser pour l'URL "jdbc:mysql://".
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établissement de la connexion physique avec la base de données
            con = DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException | SQLException e) {
            // Capture des erreurs (si le pilote est introuvable ou si la connexion échoue).
            // Le bloc statique ne pouvant pas propager (throws) d'exceptions vérifiées,
            // l'erreur est encapsulée et relancée sous forme de RuntimeException.
            throw new RuntimeException(e);
        }
    }

    // Méthode publique statique (Getter) permettant à n'importe quelle autre classe
    // de récupérer l'objet de connexion 'con' déjà initialisé.
    public static Connection getConnection()
    {
        return con;
    }
}