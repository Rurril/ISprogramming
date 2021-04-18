package util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

public class ChatUtil {

    private static final String CHAT_ALGORITHM = "AES";

    // Generate AES key - for chatting function
    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(CHAT_ALGORITHM);
        generator.init(128);
        Key key = generator.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private static Key decodeKey(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key.getBytes());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String encryptMessage(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Key secretKey = decodeKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] plaintext = message.getBytes("UTF-8");
        byte[] ciphertext = cipher.doFinal(plaintext);

        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public static String decryptMessage(String message, String key) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Key secretKey = decodeKey(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] ciphertext = Base64.getDecoder().decode(message);
        System.out.print("\n   Cipher Text: ");
        for(byte b: ciphertext) System.out.printf("%02X ", b);

        byte[] plaintext = cipher.doFinal(ciphertext);
        String ret = new String(plaintext, "UTF8");
        System.out.print("\nDecrypted Text: " + ret);

        return ret;
    }
}
