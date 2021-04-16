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

    /*
    public static void main(String[] args) throws Exception {

        String text = "This is a plaintext for symmetric encryption test";
        System.out.println("Plaintext: "+text);

        System.out.println("\n\nAES Key Generation ");
        KeyGenerator keyGen2 = KeyGenerator.getInstance("AES");
        keyGen2.init(128);
        Key key2 = keyGen2.generateKey();
        byte[] printKey2 = key2.getEncoded();

        for(byte b: printKey2) System.out.printf("%02X ", b);

        System.out.println("\n\nAES Encryption ");
        Cipher cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher2.init(Cipher.ENCRYPT_MODE, key2);

        String text2 = "test Text";
        byte[] plaintext = text2.getBytes();

        byte[] ciphertext2 = cipher2.doFinal(plaintext);
        System.out.print("\nCiphertext :");
        for(byte b: ciphertext2) System.out.printf("%02X ", b);

        cipher2.init(Cipher.DECRYPT_MODE, key2);
        byte[] decrypttext2 = cipher2.doFinal(ciphertext2);
        String output2 = new String(decrypttext2, "UTF8");
        System.out.print("\nDecrypted Text:" + output2);
    }

     */
}
