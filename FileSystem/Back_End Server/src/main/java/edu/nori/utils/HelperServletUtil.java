package edu.nori.utils;

import edu.nori.derby.MyDerby;
import edu.nori.entity.FileInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/***
 * SErvlet工具类
 */
public class HelperServletUtil {
    private static final String DATE_FORMAT = "yyyyMMdd";
    //存储队列元素
    private static List<FileInfo> fileInfoList = new ArrayList<>();
    //保存目录前缀
    public static final String BASIC_PATH =  System.getProperty("user.dir") + "/target/classes/META-INF/FileRepository/";

    //私有构造
    private HelperServletUtil(){}

    /***
     * 对外提供访问方法
     * @return
     */
    public static List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }
    public static void setFileInfoList(List<FileInfo> fileInfoList) {
        HelperServletUtil.fileInfoList = fileInfoList;
    }
    public static void addFileInfo(FileInfo fileInfo) {
        HelperServletUtil.fileInfoList.add(fileInfo);
    }

    /***
     * 保存文件信息
     * @param fileInfo
     */
    public static FileInfo saveFileInfo2Database(FileInfo fileInfo) throws SQLException {

        fileInfo.setSaved_dir(createDirectoryName());
        //保存至数据库
        MyDerby.insertFileInfo(fileInfo);

        //返回查询到的元数据
        return MyDerby.selectByUUID(fileInfo.getUUID_name());
    }

    /***
     * 将文件下载至本地
     *保存文件信息到数据库
     * @param fileInfo
     */
    public static void saveFile(FileInfo fileInfo){

        File file = fileInfo.getFile();
        String path = getPath(file.getName());

        File savedFile = null;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try{
            savedFile = new File(path);;
            //检测是否存在目录
            checkDirectory(savedFile);
            //创建文件
            savedFile.createNewFile();

            //创建输入流、输出流
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(savedFile);

            //创建缓冲数组，依次将输入流读入
            byte[] buffer = new byte[1024];
            int i = fileInputStream.read(buffer);
            while(i != -1){
                fileOutputStream.write(buffer, 0, i);
                i = fileInputStream.read(buffer);
            }

            //将信息保存至数据库
            fileInfo = saveFileInfo2Database(fileInfo);
        }catch (Exception e){
            System.out.println("!!!后端服务器保存文件");
        }

        return;
    }

    /***
     * 根据系统时间生成文件目录名
     * @return
     */
    private static String createDirectoryName(){
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

        return df.format(new java.util.Date());
    }

    /***
     * 获取路径
     * @param fileName
     * @return
     */
    public static String getPath(String fileName){
        StringBuffer path = new StringBuffer(HelperServletUtil.BASIC_PATH);
        //获取年月日目录
        path.append(createDirectoryName());
        path.append("/");
        path.append(fileName);

        return path.toString();
    }

    /***
     * 将文件解密
     * @param fileInfo
     */
    public static void decodeFile(FileInfo fileInfo) throws IOException {
        //先将AES的密钥密文
        String keyStr = fileInfo.getEncry_data();

        //使用后端服务器端的公钥解密
        byte[] AES_Key = AES_Util.stringKey2ByteKey(keyStr);
        AES_Key = RSA_Util.RSADecode(AES_Key);

        //对文件进行解密
        AES_Util.decode(fileInfo, AES_Key);
    }

    /***
     * 找到下载文件
     * @param fileInfo
     */
    public static void findFile(FileInfo fileInfo) {
        StringBuffer path = new StringBuffer(HelperServletUtil.BASIC_PATH);
        path.append(fileInfo.getSaved_dir());
        path.append("/");
        path.append(fileInfo.getUUID_name());

        try{
            //设置元数据
            fileInfo.setFile(new File(path.toString()));
        }catch (Exception e){
            System.out.println("无法找到下载文件");
        }

        return ;
    }

    /***
     * 检查目录
     * @param file
     */
    public static void checkDirectory(File file){
        if (!file.getParentFile().exists()) {
            // 新建文件夹
            file.getParentFile().mkdirs();
        }
    }

}
