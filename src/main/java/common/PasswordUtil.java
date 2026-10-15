
package common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {

    private PasswordUtil() {
    }


    public static String hash(String plain) {
        byte[] saltBytes = new byte[8];
        new SecureRandom().nextBytes(saltBytes);
        String salt = toHex(saltBytes);
        return salt + "$" + sha256(salt + plain);
    }

    public static boolean matches(String plain, String stored) {
        if (plain == null || stored == null || !stored.contains("$")) {
            return false;
        }
        String[] parts = stored.split("\\$");
        return parts.length == 2 && sha256(parts[0] + plain).equals(parts[1]);
    }

    private static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
