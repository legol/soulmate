<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>

<!--enable JSTL begin-->
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!--enable JSTL end-->

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Upload Result</title>
</head>
<body>
  <h3>Uploaded Files:</h3>

   <c:forEach items="${uploadedFiles}" var="file">
          - ${file} <br>
   </c:forEach>

</body>
</html>