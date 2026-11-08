import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHasher {

    // Configuration de la sécurité recommandée par l'OWASP
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int ITERATIONS = 120_000; // Nombre de tours de hachage [2]
    private static final int KEY_LENGTH = 512;     // Taille de la clé finale en bits pour un hash long [2]
    private static final int SALT_LENGTH = 16;     // Taille du sel aléatoire en octets [2]

    /**
     * Génère un hachage sécurisé à sens unique pour un mot de passe en clair.
     */
    public static String hashPassword(String passwordEnClair) {
        try {
            // 1. Génération d'un sel hautement aléatoire et unique [2]
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);

            // 2. Calcul mathématique du hachage PBKDF2 [2]
            byte[] hash = pbkdf2(passwordEnClair.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // 3. Encodage en Base64 pour stocker le tout sous forme de texte lisible [2]
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            // On assemble la configuration et les données séparées par des ":" [2]
            return ITERATIONS + ":" + saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erreur lors du hachage PBKDF2", e);
        }
    }

    /**
     * Vérifie si un mot de passe en clair correspond au hachage stocké en base de données.
     */
    public static boolean checkPassword(String passwordEnClair, String passwordHacheBDD) {
        try {
            // On découpe la chaîne stockée pour récupérer les itérations, le sel et le hash original [2]
            String[] parts = passwordHacheBDD.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hashAttendu = Base64.getDecoder().decode(parts[2]);

            // On re-hache le mot de passe saisi avec exactement les mêmes paramètres [2]
            byte[] hashSaisi = pbkdf2(passwordEnClair.toCharArray(), salt, iterations, hashAttendu.length * 8);

            // Comparaison sécurisée à temps constant (évite les failles de sécurité de type Timing Attacks) [2]
            return java.security.MessageDigest.isEqual(hashAttendu, hashSaisi);

        } catch (Exception e) {
            // Gère le cas où le hachage stocké en BDD n'est pas au bon format ou est corrompu [2]
            return false;
        }
    }

    // Méthode interne utilisant l'API crypto native de Java
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    public static void main(String[] args) {
        String monPassword = "monMotDePasseSuperSecurise123";

        // 1. Hachage du mot de passe (à faire AVANT le PreparedStatement d'insertion)
        String hash = hashPassword(monPassword);
        System.out.println("Mot de passe en clair : " + monPassword);
        System.out.println("Longueur du hash : " + hash.length() + " caractères");
        System.out.println("Hachage stocké en BDD :\n" + hash);

        // 2. Vérification (à faire lors d'une tentative de Connexion / Login)
        boolean estValide = checkPassword("monMotDePasseSuperSecurise123", hash);
        System.out.println("\nTentative de login réussie ? : " + estValide); // true

        boolean estInvalide = checkPassword("mauvaisPassword", hash);
        System.out.println("Tentative avec mauvais mot de passe réussie ? : " + estInvalide); // false
    }
}