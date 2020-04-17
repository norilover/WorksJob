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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/***
 * ，发送文件列表
 */
@WebServlet(name = "SendFileListServlet")
public class SendFileListServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //检查请求是否合法
        if(!InspectorUtil.isLegalRequest(request)){
            System.out.println("非法请求");
            response.setStatus(403);
            return;
        }else{
            System.out.println("合法请求");
        }

        //封装用于接收的对象输入、输出流
        ObjectOutputStream objectOutputStream = null;

        try{
            objectOutputStream = new ObjectOutputStream(response.getOutputStream());

            //从数据库中获得文件元数据列表
            HelperServletUtil.setFileInfoList(MyDerby.selectAll());

            //将修改后的对象写入输出流
            objectOutputStream.writeObject(HelperServletUtil.getFileInfoList());
            System.out.println("上传数据，更新文件列表成功");
        }catch (IOException | SQLException e){
            System.out.println("！！！后端服务器传送文件列表失败");
        }finally {
            objectOutputStream.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
