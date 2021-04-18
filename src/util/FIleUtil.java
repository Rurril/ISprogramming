package util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class FIleUtil {

    private static final int KEY_SIZE = 2048;
    private static final String FILE_ALGORITHM = "RSA";

    public static HashMap<String, String> generateKeyPair() throws NoSuchAlgorithmException{

        HashMap<String, String> stringkeyPair = new HashMap<>();

        KeyPairGenerator generator = KeyPairGenerator.getInstance(FILE_ALGORITHM);
        generator.initialize(KEY_SIZE, new SecureRandom());
        KeyPair keyPair = generator.genKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String strPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String strPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        stringkeyPair.put("publicKey", strPublicKey);
        stringkeyPair.put("privateKey", strPrivateKey);
        return stringkeyPair;
    }

    private static PrivateKey generatePrivateKey(String strPrivatekey){
        byte[] bytePrivateKey = Base64.getDecoder().decode(strPrivatekey.getBytes());
        try{
            KeyFactory keyFactory = KeyFactory.getInstance(FILE_ALGORITHM);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytePrivateKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static PublicKey generatePublicKey(String strPublicKey){
        byte[] bytePublicKey = Base64.getDecoder().decode(strPublicKey.getBytes());
        try{
            KeyFactory keyFactory = KeyFactory.getInstance(FILE_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(bytePublicKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String encrypt(String plainText, String encodedPublicKey) throws NoSuchAlgorithmException{
        PublicKey publicKey = FIleUtil.generatePublicKey(encodedPublicKey);
        try{
            Cipher cipher = Cipher.getInstance(FILE_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String cipherText, String encodedPrivateKey) throws NoSuchAlgorithmException {
        PrivateKey privateKey = FIleUtil.generatePrivateKey(encodedPrivateKey);
        try {
            byte[] bytes = Base64.getDecoder().decode(cipherText);
            Cipher cipher = Cipher.getInstance(FILE_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(bytes), "UTF-8");
        } catch (NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sign(String plainText, String priKey){
        try{
            PrivateKey privateKey = generatePrivateKey(priKey);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(plainText.getBytes("UTF-8"));
            byte[] bSignature = signature.sign();
            return Base64.getEncoder().encodeToString(bSignature);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifySignarue(String plainText, String signature, String pubKey) {
        Signature sig;
        try {
            PublicKey publicKey = generatePublicKey(pubKey);
            sig = Signature.getInstance("SHA256WithRSA");
            sig.initVerify(publicKey);
            sig.update(plainText.getBytes());
            return sig.verify(Base64.getDecoder().decode(signature));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            return false;
        }
    }


}
