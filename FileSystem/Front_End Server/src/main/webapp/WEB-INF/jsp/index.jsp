
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
    <head>
        <title>文件系统</title>
    </head>
<body>
<div>
    <div>
        <h3>文件上传</h3>
        <form method="post" action="/uploadFile" enctype="multipart/form-data">
            <input type="file" name="file"><br>
            <input type="submit" value="提交">
        </form>
    </div>
    <div>
        <tr><a methods="get" href="/">显示所有数据</a> </tr>
        <tr><a methods="get" href="/downloadFileMetadataTen">显示最新的10个数据</a> </tr>
    </div>
    <div>
        <h3>文件列表</h3>
        <table border="1">
            <tr align="width = 10">
                <td >序号</td>
                <td >名称</td>
                <td >UUID</td>
                <td >保存目录</td>
                <td >上传时间</td>
                <td  >数字信封</td>
                <td >大小</td>
                <td >类型</td>
                <td >下载文件</td>
                <td >下载元数据</td>
            </tr>
            <c:forEach items="${fileLists}" var="fileSystem">
                <tr align="width = 10">
                    <form method="post" action="/downloadFile" >
                        <td >${fileSystem.id}</td>
                        <td >${fileSystem.raw_name}</td>
                        <td >${fileSystem.UUID_name}</td>
                        <td >${fileSystem.saved_dir}</td>
                        <td >${fileSystem.saved_time}</td>
                        <td >${fileSystem.encry_data}</td>
                        <td >${fileSystem.file_size}</td>
                        <td >${fileSystem.file_type}</td>
                        <td ><a methods="get" id="${fileSystem.UUID_name}" href="/downloadFile?UUID_name=${fileSystem.UUID_name}">文件</a></td>
                        <td ><a methods="get" id="${fileSystem.UUID_name}" href="/downloadFileMetadata?UUID_name=${fileSystem.UUID_name}">元数据</a></td>
                    </form>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>


</body>
</html>
