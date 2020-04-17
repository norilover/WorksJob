package edu.nori.utils;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/***
 * RSA工具类
 */
public class RSA_Util implements Serializable {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";

    //公钥还原数据
    private static final String PUBLIC_KEY_DATA = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgA8t0azPXXNw2D+NMsB4BgXV8XXIMekx6gpuiAHUTOThgDXdCFWPji70MAJLKDpyDC9Uthm20GlQqQMlX2PKWGYXKx/puGVKerri8RcbOllc9Cs+KWDIdhe0bwxjrrhaUr26Bqr/OsdR1HxznD6YuHtWptvei9qEZb/mXmfBVm2jAgMBAAE=";
    private static PublicKey publicKey;

    //初始化公钥
    static{
        generatePrivateKey();
    }

    /***
     * 初始化公钥
     * @return
     */
    private static void generatePrivateKey() {
        byte[] publicBytes = AES_Util.stringKey2ByteKey(PUBLIC_KEY_DATA);
        publicKey = restorePublicKey(publicBytes);
    }

    /***
     * 还原公钥
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 公钥解密
     * @param data
     * @return
     */
    public static byte[] RSADecode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            //设置模式
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
