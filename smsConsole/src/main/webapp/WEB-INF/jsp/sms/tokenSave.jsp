<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>短信管理平台</title>
	 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.min.css">
  	<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
  	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  	<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
  	
  	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.structure.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.theme.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/css/ui.jqgrid.css" />
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>
  	
  	<script type="text/javascript">
	  	 //表单校验
	    function validate_form(){
	  		 
			var appCode = $("#appCode").val();
			if(appCode == ""){
				alert("请录入应用标识！");
				return false;
			}
	    	
			var flag = false;
	    	$.ajax({
	            url : "${pageContext.request.contextPath}/sms/tokenValidate.action?appCode="+encodeURIComponent(encodeURIComponent(appCode)),
	            async : false,
	            type : "get",
	            success : function (result){
	            	if(result == "isExisted"){
	            		alert(appCode+"已经配置了认证标识");
			  			}else if(result == "notExisted"){
			  				flag = true;
			  			}else if(result == "error"){
			  				alert("查询是否已存在" + appCode + "应用标识发生异常！");
			  			}
	            }
	        });
	    	
			return flag; 
	    }
	  	 
		$(function() {
		   	$("#submit").click(function(){
		   		var appCode = $("input[name='appCode']").val();
		   		appCode = encodeURIComponent(appCode);
  				$("input[name='appCode']").val(appCode);
		   		$("#inputForm").submit();
		   	});
		   	
	    });
	  	 
  	</script>
  	
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-error" align="center">
			<font size="3">${message}</font>
		</div>
		<!-- 自动隐藏提示信息 -->
		<script type="text/javascript">
		setTimeout(function() {
			$('#message').hide('slow');
		}, 7000);
		</script>
	</c:if>
	<c:if test="${not empty error}">
		<div id="error" class="alert alert-error"  align="center">
			<font size="3">${error}</font>
		</div>
		<!-- 自动隐藏提示信息 -->
		<script type="text/javascript">
		setTimeout(function() {
			$('#error').hide('slow');
		}, 7000);
		</script>
	</c:if>
	<form id="inputForm" class="form-horizontal" action="${pageContext.request.contextPath}/sms/tokenSave.action" method="get" onsubmit="return validate_form()">
		<table width="200px" height="100%" align="center">
			<tr>
				<th>应用认证管理</th>
			</tr>
			<tr>
				<td>
					<br/>
					<div align="center">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th>应用标识</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" id="appCode" name="appCode"/></td>
							</tr>
						</tbody>
					</table>
				</div>
				</td>
			</tr>
		</table>
		<div align="center"><a id="submit" class="btn btn-primary">提交</a></div>
	</form>
</body>
</html>
