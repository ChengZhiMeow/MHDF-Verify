package cn.chengzhiya.mhdfverify.manager;

import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

@Getter
public final class RSAManager {
    private final KeyPair KeyPair;

    public RSAManager() {
        try {
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(2048);
            KeyPair = rsa.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取私钥实例
     *
     * @return 私钥实例
     */
    public PrivateKey getPrivateKey() {
        return getKeyPair().getPrivate();
    }

    /**
     * 获取公钥实例
     *
     * @return 公钥实例
     */
    public PublicKey getPublicKey() {
        return getKeyPair().getPublic();
    }

    /**
     * 加密
     *
     * @param bytes 要加密的数据
     * @return 加密后的数据
     */
    public byte[] encode(byte[] bytes) {
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.ENCRYPT_MODE, getKeyPair().getPublic());
            return decryptCipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param string 要文本的数据
     * @return 加密后的数据
     */
    public byte[] encode(String string) {
        return encode(string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解密
     *
     * @param bytes 要解密的数据
     * @return 加密后的文本
     */
    public String decode(byte[] bytes) throws Exception {
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, getKeyPair().getPrivate());
            return new String(decryptCipher.doFinal(bytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param string 要解密的文本
     * @return 加密后的文本
     */
    public String decode(String string) throws Exception {
        return decode(string.getBytes(StandardCharsets.UTF_8));
    }
}
