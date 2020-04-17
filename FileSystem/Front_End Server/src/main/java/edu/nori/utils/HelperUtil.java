package edu.nori.utils;

import edu.nori.entity.FileInfo;
import org.apache.jasper.runtime.JspSourceDependent;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Random;

/***
 * 辅助类
 */
public class HelperUtil {
    //签名属性
    public static final String X_SID = "X-SID";
    public static final String X_SIGNATURE= "X-Signature";
    public static final String JSON_FROMAT = ".json";
    public static String BASIC_PATH_TEMP;

    //初始化基本路径
    static {
        try {
            BASIC_PATH_TEMP = ResourceUtils.getURL("classpath:").getPath() + "static/temp/";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取文件类型
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName){
        String type = fileName.substring(fileName.lastIndexOf('.') + 1);
        switch(type){
            case "exe" :{
                return "可执行文件";
            }
            case "txt" :{
                return "文本";
            }
            case "dll" :{
                return "程序集";
            }
            default :{
                return "其它";
            }
        }
    }

    /***
     *返回随机字符串
     * @return
     */
    public static String getRandomString() {
        int length = 16;
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /***
     * 向Header中添加X-SID 和 X-Signature 两个属性
     * @param urlConnection
     */
    public static void addHeaderInfo(HttpURLConnection urlConnection) {

        //设置该连接属性
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);

        //添加Header
        String ranStr =  getRandomString();
        urlConnection.setRequestProperty(X_SID, ranStr);
        System.out.println("随机字符串：" + ranStr);

        //对随机字符串加密
        byte[] encryOut = RSA_Util.RSAEncode(ranStr.getBytes());
        String encryStr = AES_Util.byteKey2StringKey(encryOut);
        System.out.println("加密：" + encryStr);

        urlConnection.setRequestProperty(X_SIGNATURE, encryStr);
    }

    /***
     * 检查目录
     * @param file
     */
    public static void checkDirectory(File file){
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();// 新建文件夹
        }
    }

    /***
     * 生成文件
     * @param originalFilename
     * @return
     */
    public static File createFile(String originalFilename) {
        return new File(HelperUtil.BASIC_PATH_TEMP, originalFilename);
    }

    /***
     * 文件加密、AES密钥加密
     * @param file
     * @param fileInfo
     */
    public static void encryFileAndKey(MultipartFile file, FileInfo fileInfo) {
        //AES文件加密
        byte[] key = AES_Util.initKey(fileInfo.getRaw_name());

        File saveFile = null;
        try{
            saveFile = AES_Util.encryptFile(file.getInputStream(), fileInfo.getUUID_name(), key);
        }catch (Exception e){
            System.out.println("!!!上传文件加密");
        }

        String encryKry = null;
        try{
            //对AES密钥进行RSA加密
            byte[] encryKeyArr = RSA_Util.RSAEncode(key);
            encryKry = AES_Util.byteKey2StringKey(encryKeyArr);
        }catch (Exception e){
            System.out.println("!!!AES密钥加密");
        }

        //保存数据
        fileInfo.setEncry_data(encryKry);
        fileInfo.setFile(saveFile);
    }
}
