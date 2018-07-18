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
  		});

  		function resetc(){
  				$("#phone").val("");
				$("#typeCode option:first").prop("selected", 'selected');
				$("#appCode").val("");
				$("#channelNo").val("");
				$("#status").val("");
				$("#templateCode").val("");
  		}
  		function searchfunc(){
				var startTime = $("input[name='startTime']").val();
				startTime = encodeURIComponent(encodeURIComponent(startTime));
				var endTime = $("input[name='endTime']").val();
				endTime = encodeURIComponent(encodeURIComponent(endTime));
				var phone = $("input[name='phone']").val();
				phone = encodeURIComponent(encodeURIComponent(phone));

				//短信类型
				var typeCode = $("#typeCode").val();
				typeCode = encodeURIComponent(encodeURIComponent(typeCode));
				//应用
				var appCode = $("#appCode").val();
				appCode = encodeURIComponent(encodeURIComponent(appCode));

				//通道
				var channelNo = $("#channelNo").val();
				channelNo = encodeURIComponent(encodeURIComponent(channelNo));

				//状态
				var status = $("#status").val();
				status = encodeURIComponent(encodeURIComponent(status));

				//模板编号
				var templateCode = $("#templateCode").val();
				templateCode = encodeURIComponent(encodeURIComponent(templateCode));

				window.location.href = "${pageContext.request.contextPath}/sms/smsListSearch.action"
					+"?startTime="+startTime+"&endTime="+endTime+"&phone="+phone+"&typeCode="+typeCode+"&appCode="+appCode
					+"&channelNo="+channelNo+"&status="+status+"&templateCode="+templateCode;
  		}
  	</script>
  	<style>
  		.ellipsis {
			white-space: nowrap;
			text-overflow: ellipsis;
			overflow: hidden;
		}

		.small-alert {
			padding-top:3px;
			padding-bottom:3px;
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
			<h4>通知短信送达查询</h4>
		</div>
		<div class="row">
			<form class="form-horizontal" align="center" method="get" action="${pageContext.request.contextPath}/sms/noticeCallbackSearch.action">
			<table width="1100px" height="100%" align="center">
				<tr>
					<th align="left">模板查询</th>
				</tr>
				<tr>
					<td>
						<div align="center">
							<table class="table table-bordered ">
								<tr  height="15px">
								<td style="width: 150px; background-color:#f9f9f9">手机号</td>
								<td>
								<input type="text"
							class="form-control form_date" value="${phone}" id="phone" name="phone">
								</td>


									<td style="width: 150px; background-color:#f9f9f9">创建时间段</td>
									<td style="width: 600px">
									<input type="text"
							  value="${startTime}" name="startTime"
							readonly
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									----
									<input type="text"
							  value="${endTime}" name="endTime" readonly
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									</td>
								</tr>
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信类型</td>
									<td style="width: 150px">
										<select id="typeCode">
											<c:forEach items="${types}" var="type">
												<c:if test="${type.typeCode == typeCode}">
													<option value="${type.typeCode}" selected="selected">${type.typeName}</option>
												</c:if>
												<c:if test="${type.typeCode != typeCode}">
													<option value="${type.typeCode}">${type.typeName}</option>
												</c:if>
											</c:forEach>
										</select>
									</td>
									<td style="width: 150px;  background-color:#f9f9f9">短信应用</td>
									<td style="width: 150px">
										<select id="appCode">
											<option value="">全部</option>
											<c:forEach items="${tokens}" var="token">
												<c:if test="${token.appCode == appCode}">
													<option value="${token.appCode}"  selected="selected">${token.appCode}</option>
												</c:if>
												<c:if test="${token.appCode != appCode}">
													<option value="${token.appCode}" >${token.appCode}</option>
												</c:if>
											</c:forEach>
									</select>
								</tr>
							<tr>
							<td style="width: 150px;  background-color:#f9f9f9">短信通道</td>
									<td style="width: 150px">
										<select id="channelNo">
											<option value="">全部</option>
											<c:forEach items="${channels}" var="type">
												<c:if test="${type.channelCode == channelNo}">
													<option value="${type.channelCode}" selected="selected">${type.name}</option>
												</c:if>
												<c:if test="${type.channelCode != channelNo}">
													<option value="${type.channelCode}">${type.name}</option>
												</c:if>
											</c:forEach>
										</select>
									</td>


									<td style="width: 150px;  background-color:#f9f9f9">发送状态</td>
									<td style="width: 150px">
										<select id="status">
											<option value="">全部</option>
													<c:if test="${status == 'SUCCESS'}">
													 <option value="SUCCESS" selected="selected">成功</option>
													<option value="FAIL" >失败</option>
												  </c:if>
												  <c:if test="${status == 'FAIL'}">
												   <option value="SUCCESS">成功</option>
													 <option value="FAIL" selected="selected">失败</option>
												  </c:if>

												   <c:if test="${status != 'FAIL' && status !='SUCCESS'}">
												   <option value="SUCCESS">成功</option>
													 <option value="FAIL">失败</option>
												  </c:if>




										</select>
									</td>
							</tr>
							<td>模板编号</td>
							<td><input type="text"
							class="form-control form_date"  value="${templateCode}" name="templateCode" id="templateCode"></td>

							</tr>

							</table>
							<input id="search" type="button" class="btn btn-primary"
								value="搜索"  onclick="searchfunc();"/>&nbsp;
								<input id="reset" type="button" class="btn btn-primary" onclick="resetc();" value="重置" />
						</div>
					</td>
				</tr>
			</table>

			</form>
		</div>

		</br>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-bordered table-striped" style="table-layout:fixed;">
  					<thead>
		  				<th width="110px;">手机号</th>
		  				<th>短信通道</th>
		  				<th width="60px;">短信类型</th>
		  				<th width="160px;">短信应用</th>
		  				<th width="160px;">短信模板ID</th>
		  				<th>发送时间</th>
		  				<th>发送状态</th>
		  				<th width="60px;">操作</th>
  					<thead>
		  			<tbody>
		  				<c:if test="${empty page.result}">
		  					<tr><td colspan="8" align="center">暂无数据</td></tr>
		  				</c:if>
		  				<c:forEach items="${page.result}" var="smsEntity">
		  					<tr>
		  					   <td>${smsEntity.phone}</td>
		  					    <td>${smsEntity.channelName}</td>
		  					 	<td>${smsEntity.typeName}</td>
		  						<td>${smsEntity.appcode}</td>
		  						<td>${smsEntity.templateCode}</td>
		  						<td>
		  							<fmt:formatDate value="${smsEntity.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
		  						</td>

		  						<c:choose>
		  							<c:when test="${smsEntity.statusCode=='DELIVRD' || smsEntity.statusCode=='0'}">
		  								<td>成功</td>
		  							</c:when>
		  							<c:otherwise>
		  								<td>失败</td>
		  							</c:otherwise>
		  						</c:choose>

							<td>
		  							<a class="btn btn-primary btn-xs"
		  								href="${pageContext.request.contextPath}/sms/smsQueryView.action?id=${smsEntity.id}">查看</a>
		  						</td>
		  					</tr>
		  				</c:forEach>
		  			</tbody>
				</table>
				<div align="center">
				<c:if test="${page !=null}">
					<tags:newPagination page="${page}" paginationSize="${page.pageSize}"/>
				</c:if>
				</div>
			</div>
		</div>
	</div>
</body>
</html>