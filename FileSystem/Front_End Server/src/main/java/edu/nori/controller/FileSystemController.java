package edu.nori.controller;

    import edu.nori.entity.FileInfo;
    import edu.nori.service.FileSystemService;
    import edu.nori.service.FileSystemServiceImp;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import java.io.*;
    import java.util.*;

/***
 * 控制请求
 */
@Controller("/")
public class FileSystemController {
    @Autowired
    private FileSystemService fileSystemService = new FileSystemServiceImp();

    /***
     * 主页面，显示文件列表
     * @param model
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @GetMapping("/")
    public String testInfo(Model model) {

        try{
            List<FileInfo> fileInfoList = fileSystemService.getFileList();
            //Jsp中读取
            model.addAttribute("fileLists", fileInfoList);

        }catch (Exception e){
            System.out.println("！！！查询文件列表");
        }

        return "index";
    }

    /***
     * 上传文件更新列表
     * @param file
     * @param model
     * @return
     */
    @PostMapping("/uploadFile")
    public String uploadFile(MultipartFile file, Model model){

        try {
            List<FileInfo> fileInfoList = fileSystemService.addFile2List(file);
            //Jsp中读取
            model.addAttribute("result", "上传成功！");
        }
        catch (Exception e)
        {
            model.addAttribute("result", "上传失败！");
        }

        return "returnIndex";

    }

    /***
     * 下载文件
     * @param request
     * @param response
     */
    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {

        try{

            FileInfo fileInfo = new FileInfo();
            String uuid_name = request.getParameter("UUID_name");
            fileInfo.setUUID_name(uuid_name);
            //获取下载文件
            File downloadFile = fileSystemService.getDownloadFile(fileInfo);

            //向页面相应下载文件
            fileSystemService.responseDownloadFile(downloadFile, response);

        }catch (Exception e){
            System.out.println("下载失败");
        }

        return ;
    }

    /***
     * 下载文件元数据
     * @param request
     * @param response
     */
    @GetMapping("/downloadFileMetadata")
    public void downloadFileMetadata(HttpServletRequest request, HttpServletResponse response) {

        try{

            String uuid_name = request.getParameter("UUID_name");

            //获取下载文件
            //向页面相应下载文件
            fileSystemService.responseDownloadFileMeta(uuid_name, response);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("下载元数据失败");
        }
    }

    /***
     * 下载最近上传的10个文件元数据
     * @param model
     */
    @GetMapping("/downloadFileMetadataTen")
    public String downloadFileMetadataTen(Model model) {

        try{
            //获取下载文件
            //向页面相应下载文件
            List<FileInfo> fileInfoListTen = fileSystemService.responseDownloadFileMetaTen();
            model.addAttribute("fileLists", fileInfoListTen);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("查找失败");
        }
        return "index";
    }


}
