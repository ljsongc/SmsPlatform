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
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrapV3.min.css">
  	<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
  	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  	<script src="<%=request.getContextPath()%>/js/bootstrapV3.min.js"></script>
  	
  	<script type="text/javascript">
  		$(document).ready(function(){
  			$("#search").click(function(){
  				$(this).attr("disabled","disabled");
  				var startTime = $("input[name='startTime']").val();
  				startTime = encodeURIComponent(encodeURIComponent(startTime));
  				var endTime = $("input[name='endTime']").val();
  				endTime = encodeURIComponent(encodeURIComponent(endTime));
  				var phone = $("input[name='phone']").val();
  				phone = encodeURIComponent(encodeURIComponent(phone));
  				var content = $("input[name='content']").val();
  				content = encodeURIComponent(encodeURIComponent(content));
  				window.location.href = "${pageContext.request.contextPath}/sms/failureSearch.action"
  					+"?startTime="+startTime+"&endTime="+endTime+"&phone="+phone+"&content="+content;
  			});
  			
  			$("#clean").click(function(){
  				$("input[name='phone']").val("");
  				$("input[name='content']").val("");
  			});
  		});
  	</script>
  	<style>
  		.ellipsis {
			white-space: nowrap;
			text-overflow: ellipsis;
			overflow: hidden;
		}
  	</style>
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
		<div align="center">
			<h4>失败短信查询</h4>
		</div>
		<div class="row">
			<form class="form-horizontal" align="center">
				<div class="form-group">
					<label class="col-sm-2 control-label">开始时间</label>
					<div class="col-sm-3">
						<input type="text"
							class="form-control" value="${startTime}" name="startTime"
							readonly
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
					</div>
					<label class="col-sm-2 control-label">结束时间</label>
					<div class="col-sm-3">
						<input type="text"
							class="form-control" value="${endTime}" name="endTime" readonly
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-2 control-label">手机号</label>
					<div class="col-sm-3">
						<input type="text"
							class="form-control form_date" value="${phone}" name="phone">
					</div>
					<label class="col-sm-2 control-label">内容</label>
					<div class="col-sm-3">
						<input type="text"
							class="form-control form_date" value="${content}" name="content">
					</div>
				</div>
			</form>
			<div align="center">
				<button class="btn btn-primary" id="search">搜索</button>
				<input type="reset" class="btn" value="清空" id="clean"/>
			</div>
		</div>
		
		</br>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-bordered table-striped" style="table-layout:fixed;">
  					<thead>
		  				<th width="110px;">手机号</th>
		  				<th>内容</th>
		  				<th width="60px;">类型</th>
						<th width="80px;">通道</th>
		  				<th width="160px;">失败原因</th>
		  				<th width="60px;">操作</th>
  					<thead>
		  			<tbody>
		  				<c:if test="${empty page.result}">
		  					<tr><td colspan="6" align="center">暂无数据</td></tr>
		  				</c:if>
		  				<c:forEach items="${page.result}" var="smsEntity">
		  					<tr>
		  						<td>${smsEntity.to}</td>
		  						<td class="ellipsis">${smsEntity.content}</td>
		  						<c:choose>
										<c:when test="${smsEntity.type=='NOTICE'}"><td>通知</td></c:when>
										<c:when test="${smsEntity.type=='SALE'}"><td>营销</td></c:when>
										<c:otherwise><td>其他</td></c:otherwise>		  						
		  						</c:choose>
		  						<td class="ellipsis">${smsEntity.channelNo}</td>
		  						<!-- 去除memo的FAILURE前缀 -->
		  						<c:choose>
		  							<c:when test="${fn:startsWith(smsEntity.memo,'FAILURE')}">
		  								<td class="ellipsis">${fn:substring(smsEntity.memo,7,fn:length(smsEntity.memo))}</td>
		  							</c:when>
		  							<c:otherwise><td class="ellipsis">${smsEntity.memo}</td></c:otherwise>
		  						</c:choose>
		  						
		  						<td>
		  							<a class="btn btn-primary btn-xs" 
		  								href="${pageContext.request.contextPath}/sms/failureView.action?id=${smsEntity.id}">查看</a>
		  						</td>
		  					</tr>
		  				</c:forEach>
		  			</tbody>
				</table>
				<div align="center">
					<tags:newPagination page="${page}" paginationSize="${page.pageSize}"/>				
				</div>
			</div>
		</div>
	</div>
</body>
</html>