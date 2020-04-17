package edu.nori.utils;


import edu.nori.entity.FileInfo;
import edu.nori.jetty.MyJetty;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/***
 * AES工具类
 */
public class AES_Util {

    /***
     * 解密
     * @param fileInfo
     * @param key
     * @return
     */
    public static void decode(FileInfo fileInfo, byte[] key) throws IOException {

        File newFile = null;

        try{
            newFile = new File(HelperServletUtil.BASIC_PATH + "temp/" + fileInfo.getRaw_name());
            //检测是否存在目录
            if (!newFile.getParentFile().exists())
            {
                newFile.getParentFile().mkdirs();
            }

            //创建临时文件
            newFile.createNewFile();
        }catch (Exception e){
            System.out.println("！！！解密时创建临时文件出错");
        }

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        CipherOutputStream cipherOutputStream = null;

        try{
            fileInputStream = new FileInputStream(fileInfo.getFile());
            fileOutputStream = new FileOutputStream(newFile);

            //准备解密材料
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);

            //写入解密密流
            byte[] buffer = new byte[1024];
            int i = fileInputStream.read(buffer);
            while (i != -1) {
                cipherOutputStream.write(buffer, 0, i);
                i = fileInputStream.read(buffer);
            }
            //记录文件
            fileInfo.setFile(newFile);
        }catch (Exception e){
            System.out.println("解密文件出错！");
        }finally {
            fileInputStream.close();
            fileOutputStream.close();
            cipherOutputStream.close();
        }
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
