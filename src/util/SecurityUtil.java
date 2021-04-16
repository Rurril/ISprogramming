package util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class SecurityUtil {

    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";

    public static HashMap<String, String> generateKeyPair() throws NoSuchAlgorithmException{

        HashMap<String, String> stringkeyPair = new HashMap<>();

        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
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
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
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
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(new PKCS8EncodedKeySpec(bytePublicKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String encrypt(String plainText, String encodedPublicKey) throws NoSuchAlgorithmException{
        PublicKey publicKey = SecurityUtil.generatePublicKey(encodedPublicKey);
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String cipherText, String encodedPrivateKey) throws NoSuchAlgorithmException {
        PrivateKey privateKey = SecurityUtil.generatePrivateKey(encodedPrivateKey);
        try {
            byte[] bytes = Base64.getDecoder().decode(cipherText);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(bytes), "UTF-8");
        } catch (NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


//    public static boolean verify(String plainText, String signature, byte[] encodedPublicKey){
//        PublicKey publicKey = this.generatePublicKey(encodedPublicKey);
//
//    }

    private static boolean verifySignarue(String plainText, String signature, PublicKey publicKey) {
        Signature sig;
        try {
            sig = Signature.getInstance(ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(plainText.getBytes());
            if (!sig.verify(Base64.getDecoder().decode(signature)))
                throw new InvalidParameterException("It was awesome! Signature hasn't be invalid");
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


}
