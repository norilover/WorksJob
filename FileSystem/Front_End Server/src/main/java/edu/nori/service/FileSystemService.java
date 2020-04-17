package edu.nori.service;

import edu.nori.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/***
 * 文件服务器前端服务处理
 */
public interface FileSystemService {

    List<FileInfo> addFile2List(MultipartFile file) throws IOException;

    List<FileInfo> getFileList() throws IOException;

    File getDownloadFile(FileInfo fileInfo) throws IOException;

    void responseDownloadFile(File downloadFile, HttpServletResponse response) throws IOException;

    void responseDownloadFileMeta(String uuid_name, HttpServletResponse response) throws IOException;

    List<FileInfo> responseDownloadFileMetaTen();
}
