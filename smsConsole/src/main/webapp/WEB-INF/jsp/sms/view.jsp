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
	<table width="90%" height="100%" align="center">
			<tr>
				<th>短信详情</th>
			</tr>
			<tr>
				<td>
					<br/>
					<div align="center">
					<table class="table table-bordered table-striped" style="word-break:break-all; word-wrap:break-all;">
						<thead>
							<tr>
								<th width="50%">短信号码</th>
								<th width="50%">创建时间</th>
							</tr>
						</thead>
						<tbody>
								<tr>
									<td>${smsEntity.to}</td>
									<td>
										<fmt:formatDate value="${smsEntity.time}" pattern="yyyy-MM-dd HH:mm:ss"/>
									</td>
								</tr>
						</tbody>
						<thead>
							<tr>
								<th>短信类型</th>
								<th>来源IP</th>
							</tr>
						</thead>
						<tbody>
								<tr>
									<td>
										<c:if test="${smsEntity.type == 'SALE'}">
	                                                   	营销
	                                   </c:if>
	                                    <c:if test="${smsEntity.type == 'NOTICE'}">
	                                                   	通知
	                                   </c:if>
									</td>
									<td>${smsEntity.ip}</td>
								</tr>
						</tbody>
						<thead>
							<tr>
								<th>应用标识</th>
								<th>认证标识</th>
							</tr>
						</thead>
						<tbody>
								<tr>
									<td>${smsEntity.appCode}</td>
									<td>${smsEntity.token}</td>
								</tr>
						</tbody>
						<thead>
							<tr>
								<th>短信级别</th>
								<th>短信通道</th>
							</tr>
						</thead>
						<tbody>
								<tr>
									<td>                          
	                                   <c:if test="${smsEntity.level == 'NORMAL'}">
	                                                   	普通
	                                   </c:if>
	                                   <c:if test="${smsEntity.level == 'WARN'}">
	                                                   	告警
	                                   </c:if>
	                                   <c:if test="${smsEntity.level == 'ERROR'}">
	                                                   	错误
	                                   </c:if>
	                                   <c:if test="${smsEntity.level == 'FATAL'}">
	                                                 	  严重
	                                   </c:if>
                                    </td>
									<td>${smsEntity.channelNo}</td>
								</tr>
						</tbody>
						<thead>
							<tr>
								<th colspan="2">短信内容</th>
							</tr>
						</thead>
						<tbody>
								<tr>
									<td  colspan="2">${smsEntity.content}</td>
								</tr>
						</tbody>
						<c:if test="${not empty smsEntity.msgid}">
							<thead>
								<tr>
									<th colspan="2">消息ID</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td  colspan="2">${smsEntity.msgid}</td>
								</tr>
							</tbody>
						</c:if>
						<c:if test="${not empty smsEntity.memo}">
							<thead>
								<tr>
									<th colspan="2">失败原因</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td  colspan="2">${smsEntity.memo}</td>
								</tr>
							</tbody>
						</c:if>
					</table>
				</div>
				</td>
			</tr>
		</table>
</body>
</html>
