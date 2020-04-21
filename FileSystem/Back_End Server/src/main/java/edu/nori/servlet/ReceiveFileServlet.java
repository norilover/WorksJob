package edu.nori.servlet;

import edu.nori.entity.FileInfo;
import edu.nori.utils.HelperServletUtil;
import edu.nori.utils.InspectorUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/***
 * 上传文件后，将元数据存储至数据库，更新文件列表
 */
@WebServlet(name = "ReceiveFileServlet")
public class ReceiveFileServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //检查请求是否合法
        if(!InspectorUtil.isLegalRequest(request)){
            System.out.println("非法请求");
            response.setStatus(403);
            return;
        }else{
            System.out.println("合法请求");
        }

        //按指定类
        FileInfo fileInfo = new FileInfo();
        //封装用于接收的对象输入、输出流
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(response.getOutputStream());
            objectInputStream = new ObjectInputStream(request.getInputStream());

            //获得输入流中的对象
            fileInfo = (FileInfo)objectInputStream.readObject();
        }
        catch (Exception e )
        {
            System.out.println("！！！前端服务器传送文件信息失败");
        }finally {
            objectInputStream.close();
        }

        try {
            //将文件信息
            HelperServletUtil.saveFile(fileInfo);
        } catch (Exception e) {
            System.out.println("！！！ 文件数据保存失败");
        }

        try{
            //将修改后的对象写入输出流
            objectOutputStream.writeObject(fileInfo);
            //改变文件列表
            HelperServletUtil.addFileInfo(fileInfo);

            System.out.println("更新文件列表成功");
        }catch (IOException e){
            System.out.println("！！！后端服务器传送文件信息失败");
        }finally {
            objectOutputStream.close();
        }


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
