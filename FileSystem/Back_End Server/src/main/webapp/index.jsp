<%--
  Created by IntelliJ IDEA.
  User: NORI
  Date: 4/12/2020
  Time: 4:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<form action="/http://localhost:8080/t1/upload" method="post" enctype="multipart/form-data">
    Select File to Upload:<input type="file" name="fileName">
    <br>
    <input type="submit" value="Upload">
</form>
</body>
</html>
