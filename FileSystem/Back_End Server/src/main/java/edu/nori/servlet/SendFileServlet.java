package edu.nori.servlet;

import edu.nori.derby.MyDerby;
import edu.nori.entity.FileInfo;
import edu.nori.utils.HelperServletUtil;
import edu.nori.utils.InspectorUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;

/***
 * 相应前端服务器
 * 发送下载文件文件
 */
@WebServlet(name = "SendFileServlet")
public class SendFileServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
//        //检查请求是否合法
        if(!InspectorUtil.isLegalRequest(request)){
            System.out.println("非法请求");
            response.setStatus(403);
            return;
        }else{
            System.out.println("合法请求");
        }

        FileInfo fileInfo = new FileInfo();

        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try{

            //取得返回的输入流
            objectInputStream = new ObjectInputStream(request.getInputStream());
            fileInfo = (FileInfo)objectInputStream.readObject();

            try {
                //数据库查找文件元数据
                fileInfo = MyDerby.selectByUUID(fileInfo.getUUID_name());

                //找文件
                HelperServletUtil.findFile(fileInfo);;

                //对文件解密
                HelperServletUtil.decodeFile(fileInfo);

            } catch (SQLException e) {
                System.out.println("文件元数据查找");
            }

            //将文件上传至前端服务器
            objectOutputStream = new ObjectOutputStream(response.getOutputStream());
            objectOutputStream.writeObject(fileInfo);

            System.out.println("后端服务器传送下载文件成功");
        }catch (Exception e){
            System.out.println("！！！后端服务器传送下载文件失败");
        }finally {
            objectInputStream.close();
            objectOutputStream.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
