package edu.nori.service;

import com.alibaba.fastjson.JSON;
import edu.nori.entity.FileInfo;
import edu.nori.utils.HelperUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/***
 * 处理在具体操作
 */
@Service
public class FileSystemServiceImp implements FileSystemService {

    //存放文件列表
    private static List<FileInfo> fileInfoList = new ArrayList<>();
    private static Map<String, FileInfo> infoByUUID_Map = new HashMap<>();

    @Override
    public List<FileInfo> addFile2List(MultipartFile file) throws IOException {
        FileInfo fileInfo = new FileInfo();

        try{
            String fileName = file.getOriginalFilename();
            //生成文件
            File saveFile = HelperUtil.createFile(file.getOriginalFilename());
            // 检测是否存在目录
            HelperUtil.checkDirectory(saveFile);

            //设置fileInfo的各个属性
            fileInfo.setRaw_name(file.getOriginalFilename());
            fileInfo.setFile_size(String.valueOf(file.getSize()));

            UUID uuidName  = UUID.nameUUIDFromBytes(fileInfo.getRaw_name().getBytes());
            fileInfo.setUUID_name(uuidName.toString());

            fileInfo.setFile_type(HelperUtil.getFileType(fileInfo.getRaw_name()));

            HelperUtil.encryFileAndKey(file, fileInfo);
        }catch (Exception e){
            System.out.println("!!!添加文件属性");
        }

        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            //将文件信息发送到服务端
            URL url = new URL("http://localhost:8088/file/receiveFileServlet");

            //根据url建立连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //添加校验Header
            HelperUtil.addHeaderInfo(urlConnection);

            //取出输出流
            outputStream = urlConnection.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);

            //将要传输的对象写入该对象输出流中
            objectOutputStream.writeObject(fileInfo);

            //取得返回的输入流
            inputStream = urlConnection.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            //读取输入流
            fileInfo = (FileInfo)objectInputStream.readObject();
        }catch (Exception e){
            System.out.println("！！！访问后端服务器");
        }finally {
            outputStream.close();
            objectInputStream.close();
            inputStream.close();
            objectInputStream.close();
        }

        fileInfoList.add(fileInfo);
        infoByUUID_Map.put(fileInfo.getUUID_name(), fileInfo);
        return fileInfoList;
    }

    /***
     * 查询所有文件
     * @return
     */
    @Override
    public List<FileInfo> getFileList() throws IOException {
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;

        try{
            //将文件信息发送到服务端
            URL url = new URL("http://localhost:8088/file/sendFileListServlet");
            //根据url建立连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //添加校验Header
            HelperUtil.addHeaderInfo(urlConnection);

            //取得返回的输入流
            inputStream = urlConnection.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            //读取输入流
            fileInfoList = (ArrayList<FileInfo>)objectInputStream.readObject();
            //添加到Map
            for(FileInfo fileInfo : fileInfoList){
                infoByUUID_Map.put(fileInfo.getUUID_name(), fileInfo);
            }
        }catch (Exception e){
            System.out.println("!!!查询所有文件");
        }finally {
            inputStream.close();
            objectInputStream.close();
        }

        return fileInfoList;
    }

    /***
     * 从后端服务器中获得下载文件
     * @param fileInfo
     * @return
     */
    @Override
    public File getDownloadFile(FileInfo fileInfo) throws IOException {

        File savedFile = null;
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try{
            //将文件信息发送到服务端
            URL url = new URL("http://localhost:8088/file/sendFileServlet");


            //根据url建立连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //添加校验Header
            HelperUtil.addHeaderInfo(urlConnection);

            //取出输出流
            outputStream = urlConnection.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);

            //将要传输的对象写入该对象输出流中
            objectOutputStream.writeObject(fileInfo);

            //取得返回的输入流
            inputStream = urlConnection.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            //读取输入流
            fileInfo = (FileInfo)objectInputStream.readObject();

            savedFile = new File(HelperUtil.BASIC_PATH_TEMP + fileInfo.getRaw_name());

            HelperUtil.checkDirectory(savedFile);
            savedFile.createNewFile();
            FileInputStream fileInputStream = new FileInputStream(fileInfo.getFile());
            FileOutputStream fileOutputStream = new FileOutputStream(savedFile);

            //创建缓冲数组，依次将输入流读入
            byte[] buffer = new byte[1024];
            int i = fileInputStream.read(buffer);
            while(i != -1){
                fileOutputStream.write(buffer, 0, i);
                i = fileInputStream.read(buffer);
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("!!!从后端服务器处获得下载文件");
        }finally {
            inputStream.close();
            objectInputStream.close();
        }

        return savedFile;
    }

    /***
     * 相应下载文件
     * @param downloadFile
     * @param response
     */
    @Override
    public void responseDownloadFile(File downloadFile, HttpServletResponse response) throws IOException {

        //设置响应属性
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(downloadFile.getName(), "utf-8"));
        System.out.println("  ==== " + downloadFile.getName() + " ===88=======");

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            byte[] buffer = new byte[1024];

            fileInputStream = new FileInputStream(downloadFile);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            OutputStream outputStream = response.getOutputStream();

            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
        } catch (Exception e) {
            System.out.println("前端服务器响应下载文件");
        } finally {
            fileInputStream.close();
            bufferedInputStream.close();
        }
    }

    /***
     * 下载文件元数据
     * @param uuid_name
     * @param response
     */
    @Override
    public void responseDownloadFileMeta(String uuid_name, HttpServletResponse response) throws IOException {

        FileInfo fileInfo = infoByUUID_Map.get(uuid_name);
        int ind1 = fileInfo.getRaw_name().lastIndexOf(".");
        String jsonName = fileInfo.getRaw_name().substring(0,ind1);
        jsonName += HelperUtil.JSON_FROMAT;

        //设置响应属性
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(jsonName, "utf-8"));

        //将元数据类转换为JSON格式字符串
        String jsonData = JSON.toJSONString(fileInfo);

        ByteArrayInputStream byteArrayInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            byte[] buffer = new byte[1024];

            //加入输入流
            byteArrayInputStream = new ByteArrayInputStream(jsonData.getBytes());
            bufferedInputStream = new BufferedInputStream(byteArrayInputStream);

            OutputStream outputStream = response.getOutputStream();

            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
        } catch (Exception e) {
            System.out.println("前端服务器响应下载文件");
        } finally {
            byteArrayInputStream.close();
            bufferedInputStream.close();
        }

        return;
    }

    /***
     * 下载最近10个上传文件
     */
    @Override
    public List<FileInfo> responseDownloadFileMetaTen() {

        List<FileInfo> fileInfoListTen = new ArrayList<>(10);

        //获取文件元数据
        int len = fileInfoList.size();
        for (int i = len - 1; i > 0; i--){
            fileInfoListTen.add(fileInfoList.get(i));
        }
        return fileInfoListTen;
    }
}

