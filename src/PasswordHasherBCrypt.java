import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasherBCrypt {

    /**
     * Génère un hachage sécurisé à sens unique pour un mot de passe en clair.
     */
    public static String hashPassword(String passwordEnClair) {
        // BCrypt.gensalt() génère un sel aléatoire unique pour chaque mot de passe
        return BCrypt.hashpw(passwordEnClair, BCrypt.gensalt());
    }

    /**
     * Vérifie si un mot de passe en clair correspond au hachage stocké en base de données.
     */
    public static boolean checkPassword(String passwordEnClair, String passwordHacheBDD) {
        try {
            // Compare le mot de passe saisi avec l'empreinte stockée
            return BCrypt.checkpw(passwordEnClair, passwordHacheBDD);
        } catch (IllegalArgumentException e) {
            // Gère le cas où le hachage stocké en BDD n'est pas au format valide BCrypt
            return false;
        }
    }

    public static void main(String[] args) {
        String monPassword = "monMotDePasseSuperSecurise123";

        // 1. Hachage du mot de passe (à faire AVANT le PreparedStatement d'insertion)
        String hash = hashPassword(monPassword);
        System.out.println("Mot de passe en clair : " + monPassword);
        System.out.println("Hachage stocké en BDD : " + hash);
        // Le résultat ressemblera à : $2a$10$r9j2... (toujours 60 caractères)

        // 2. Vérification (à faire lors d'une tentative de Connexion / Login)
        boolean estValide = checkPassword("monMotDePasseSuperSecurise123", hash);
        System.out.println("Tentative de login réussie ? : " + estValide); // true

        boolean estInvalide = checkPassword("mauvaisPassword", hash);
        System.out.println("Tentative avec mauvais mot de passe réussie ? : " + estInvalide); // false
    }
}