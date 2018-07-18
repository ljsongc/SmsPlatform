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

  		$(function() {
			
  			$("#reset").click(function(){
		   		$("#appCode").val("");
		   		$("#token").val("");
		   	});
  			
  			$("#add").click(function(){
  				window.location.href = "${pageContext.request.contextPath}/sms/toSaveToken.action";
		   	});
  			
  			/* $(".update").click(function(){
  				var id = $(this).attr("id");
  				var appCode = $(this).attr("appCode");
  				window.location.href = "${pageContext.request.contextPath}/sms/toUpdateToken.action?id="+id+"&appCode="+appCode;
		   	}); */
  			
  			$(".delete").click(function(){
  				var flag = confirm("确定删除吗");
  				if(flag){
  					var id = $(this).attr("id");
  					var appCode = $(this).attr("appCode");
  	  				window.location.href = "${pageContext.request.contextPath}/sms/deleteToken.action?id="+id+"&appCode="+appCode;
  				}
		   	});
		   	
		   	$("#search").click(function(){
		   		$(this).attr("disabled","disabled");
		   		var appCode = $("input[name='appCode']").val();
		   		appCode = encodeURIComponent(encodeURIComponent(appCode));
  				var token = $("input[name='token']").val();
  				token = encodeURIComponent(encodeURIComponent(token));
  				window.location.href = "${pageContext.request.contextPath}/sms/tokenSearch.action?"
  					+"appCode="+appCode+"&token="+token;
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
	<br/>
		<form action="${pageContext.request.contextPath}/sms/tokenSearch.action" method="get">
			<div align="center">
				<table width="1100px" height="100%">
					<tr>
						<td style="width: 200px">应用标识：<input id="appCode" type="text" name="appCode" style="width: 200px" value="${appCode}"/></td>
						<td style="width: 200px">认证标识：<input id="token" type="text" name="token" style="width: 200px"  value="${token}"/></td>
					</tr>
				</table>
				<input id="search" type="button" class="btn btn-primary"  value="搜索"/>&nbsp;
				<input id="reset" type="button" class="btn btn-primary"  value="重置"/>
			</div>
		</form>
	<table width="1100px" height="100%" align="center">
			<tr>
				<th>应用认证管理</th>
			</tr>
			<tr>
				<td>
					<div align="center">
					<table class="table table-bordered  table-striped">
						<thead>
							<tr>
								<th>应用标识</th>
								<th>认证标识</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty page.result}">
								<tr><td colspan="3" align="center">暂无数据</td></tr>
							</c:if>
							<c:forEach items="${page.result}" var="map">
								<tr>
									<td>${map.appCode}</td>
									<td>${map.token}</td>
									<td>
										<a class="delete" id = "${map.id}" appCode="${map.appCode}" href="#">删除</a>&nbsp;&nbsp;&nbsp;
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				</td>
			</tr>
		</table>
		<tags:pagination page="${page}" paginationSize="${page.pageSize}"/>
		<div align="center"><a id="add" class="btn btn-primary">新增</a></div>
</body>
</html>
