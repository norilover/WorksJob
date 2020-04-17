package edu.nori.utils;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

/***
 * RSA工具类
 */
public class RSA_Util implements Serializable {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";
    //私钥还原数据
    private static final String PRIVATE_KEY_DATA = "MIICcgIBADANBgkqhkiG9w0BAQEFAASCAlwwggJYAgEAAoGADy3RrM9dc3DYP40ywHgGBdXxdcgx6THqCm6IAdRM5OGANd0IVY+OLvQwAksoOnIML1S2GbbQaVCpAyVfY8pYZhcrH+m4ZUp6uuLxFxs6WVz0Kz4pYMh2F7RvDGOuuFpSvboGqv86x1HUfHOcPpi4e1am296L2oRlv+ZeZ8FWbaMCAwEAAQKBgACFpOZ6o04rHWTiKFKyThK5TvqsDftlOhVtw8O8V4CvuMcheP3oWA2JXHVXvGR2M7EVCsZLoVNd8NIo4OT/upaT/6+OgoBQYa/vAXcEtMFc4KA6BX9dNpYF6YKH22P0NHYAjW+FXAqISlSWUTrYBKHPNNweT8LN17Ao6PoQYJ2xAkA+viB3PofcDh9urQbCKDHVURunomEGL2j1JG4LrfVRGS0N+eI038eP2pvYpFxDCZ0fSs12rMIk9S2PBkP3EfWPAkA97sCBAdXVxzR5ySwKgIrWO5TaP/hoyLM70tv5SidBgulfkEv+bEZdP/ezhX6zVEELq6LUdrSyv57EP38trsStAkA0RSS68ucwVrus8oz814uckTOe1lJKWtjv0We0ZzpGU9kLGbBwKDYTDCZlt5f9aVbyqNi/E3GyZGeODcQ1Y0rVAkAwqAJVWDvHWFnB1GrlVVe+N9EaJmfi6srllSN9FuUAcvkmOxd5K1ecq0TPpXGFMgxoPtAOW7RbOnBhfgFMwqGBAkAhfyVUJLkjuyDt6/cSFGyz3lGZmSLPfYfkM5T1hMd1PhVklYZ+WiUkKUWUc2UDeH+SDvrfVqAHFuYRg8vzz6I8";
    private static PrivateKey privateKey;

    static{
        generatePrivateKey();
    }

    /***
     * 初始化私钥
     * @return
     */
    private static void generatePrivateKey() {
        byte[] privateBytes = AES_Util.stringKey2ByteKey(PRIVATE_KEY_DATA);
        privateKey = restorePrivateKey(privateBytes);
    }

    /***
     * 还原私钥
     * @param keyBytes
     * @return
     */
    private static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /***
     * 私钥加密
     * @param data
     * @return
     */
     public static byte[] RSAEncode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
