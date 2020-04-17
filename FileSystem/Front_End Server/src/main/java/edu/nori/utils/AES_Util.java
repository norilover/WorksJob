package edu.nori.utils;

import edu.nori.controller.FileSystemController;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;

/***
 * AES工具类
 */
public class AES_Util {
    /**
     * 返回随机密钥
     * @return
     */

    public static byte[] initKey(String name) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 加密
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /***
     * 加密文件
     * @param fileInputStream
     * @param key
     * @return
     */

    public static File encryptFile(InputStream fileInputStream, String UUID_Name, byte[] key) throws IOException {
        //加密密流
        CipherInputStream cipherInputStream = null;
        FileOutputStream fileOutputStream = null;

        File newFile = new File(HelperUtil.BASIC_PATH_TEMP + UUID_Name);
        newFile.createNewFile();

        try{

            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            cipherInputStream = new CipherInputStream(fileInputStream, cipher);
            fileOutputStream = new FileOutputStream(newFile);

            byte[] buffer = new byte[1024];

            int i = cipherInputStream.read(buffer);
            while( i != -1){
                fileOutputStream.write(buffer, 0, i);
                i = cipherInputStream.read(buffer);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("加密文件出错！");
        }finally {
            fileInputStream.close();
            fileOutputStream.close();
        }

        return newFile;
    }
    /***
     * 解密
     * @param data
     * @param key
     * @return
     */

    public static byte[] decode(byte[] data, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * 将byte[]转换为String
     * @param byteKey
     * @return
     */
    public static String byteKey2StringKey(byte[] byteKey){
        return Base64.encodeBase64String(byteKey);
    }

    /***
     * 将String转换为byte[]
     * @param stringKey
     * @return
     */
    public static byte[] stringKey2ByteKey(String stringKey){
        return Base64.decodeBase64(stringKey);
    }
}
