<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>短信管理平台</title>
	 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrapV3.min.css">
  	<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
  	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  	<script src="<%=request.getContextPath()%>/js/bootstrapV3.min.js"></script>
  	
  	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.structure.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.theme.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/css/ui.jqgrid.css" />
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>
  	
</head>

<body>
	<div class="container">
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
			<div id="error" class="alert alert-error" align="center">
				<font size="3">${error}</font>
			</div>
			<!-- 自动隐藏提示信息 -->
			<script type="text/javascript">
				setTimeout(function() {
					$('#error').hide('slow');
				}, 7000);
			</script>
		</c:if>
		
		<div align="center">
			<h4>短信送达详情</h4>
		</div>
		</br>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-bordered table-striped" style="word-break:break-all; word-wrap:break-all;">
					<thead>
						<tr>
							<th width="50%">手机号</th>
							<th width="50%">类型</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>${smsEntity.phone}</td>
							<c:if test="${smsEntity.type=='NOTICE'}">
								<td>通知</td>
							</c:if>
							<c:if test="${smsEntity.type=='SALE'}">
								<td>营销</td>
							</c:if>
						</tr>
					</tbody>
					<thead>
						<tr>
							<th>发送时间</th>
							<th>状态上报时间</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><fmt:formatDate value="${smsEntity.createTime}"
									pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td><fmt:formatDate value="${smsEntity.receiveTime}"
									pattern="yyyy-MM-dd HH:mm:ss" /></td>
						</tr>
					</tbody>
					<thead>
						<tr>
							<th>发送状态</th>
							<th>状态描述</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>${smsEntity.statusCode}</td>
							<td>${smsEntity.description}</td>
						</tr>
					</tbody>
					<thead>
						<tr>
							<th>发送通道</th>
							<th>消息ID</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>${smsEntity.channelNo}</td>
							<td>${smsEntity.msgid}</td>
						</tr>
					</tbody>
					<thead>
						<tr>
							<th colspan="2">内容</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="2">${smsEntity.content}</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
