package edu.nori.jetty;

import edu.nori.derby.MyDerby;
import edu.nori.servlet.ReceiveFileServlet;
import edu.nori.servlet.SendFileListServlet;
import edu.nori.servlet.SendFileServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/***
 * 启动Jetty服务器和Derby数据库
 */
public class MyJetty {

    public static void main(String[] args) throws Exception {
        try{
            //开启数据库
            MyDerby.connectSchema();
            //MyDerby.clearDataFromTable();
        }catch (Exception e){
            System.out.println("！！！数据库连接出错");
        }

        try{
            MyDerby.deleteTable();
        }catch (Exception e){
            System.out.println("！！！删除表出错");
        }

        try{
            MyDerby.createTable();
        }catch (Exception e){
            System.out.println("！！！创建表出错");
        }

        //设置后端服务器端口号
        Server server = new Server(8088);

        ServletContextHandler servletContextHandler = new ServletContextHandler();
        //设置访问根路径
        servletContextHandler.setContextPath("/file");
        //添加上传文件Servlet
        servletContextHandler.addServlet(new ServletHolder(new ReceiveFileServlet()), "/receiveFileServlet/*");
        //添加下载文件Servlet
        servletContextHandler.addServlet(new ServletHolder(new SendFileServlet()), "/sendFileServlet/*");
        //添加S查询文件列表ervlet
        servletContextHandler.addServlet(new ServletHolder(new SendFileListServlet()), "/sendFileListServlet/*");

        //将handler加入服务器
        server.setHandler(servletContextHandler);

        //启动服务器
        server.start();
        server.join();
    }
}
