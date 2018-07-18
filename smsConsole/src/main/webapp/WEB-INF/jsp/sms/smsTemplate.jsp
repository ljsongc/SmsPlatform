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
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/combo.select.css">
	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.combo.select.js"></script>
  	
  	<script type="text/javascript">

  		$(function() {
  			var ctx='<%=request.getContextPath()%>';
  			$("#reset").click(function(){
		   		$("#appCode").val("");
		   		$("#startTime").val("");
		   		$("#endTime").val("");
		   		$("#templateCode").val("");
		   		$("#title").val("");
		   	});
  			
  			$("#add").click(function(){
  				var url = ctx + "/sms/toAddTemplate.action";
  				window.location.href = url;
		   	});
  			
  			/* $(".delete").click(function(){
  				var flag = confirm("确定删除吗");
  				if(flag){
  					var id = $(this).attr("id");
  					var appCode = $(this).attr("appCode");
  	  				window.location.href = "${pageContext.request.contextPath}/sms/deleteToken.action?id="+id+"&appCode="+appCode;
  				}
		   	}); */
		   	
		   	$("#search").click(function(){
		   		$(this).attr("disabled","disabled");
  				var appCode = $("#appCode").val();
  				var startTime = $("#startTime").val();
  				var endTime = $("#endTime").val();
  				var templateCode = $("#templateCode").val();
  				var title = $("#title").val();
  				var typeCode = $("#typeCode").val();
  				if(startTime != '' && endTime == ''){
  					alert("请选择结束时间");
  					return;
  				}
  				if(startTime == '' && endTime != ''){
  					alert("请选择开始时间");
  					return;
  				}
  				window.location.href = "${pageContext.request.contextPath}/sms/searchTemplate.action?"
  					+"appCode="+appCode+"&startTime="+startTime+"&endTime="+endTime+"&templateCode="+
  					templateCode+"&title="+title+"&typeCode="+typeCode;
		   	});
		   
		   
  			
	    });
  		
  		
  		function update(tempalteCode){
  			
  			var ctx='<%=request.getContextPath()%>'
  			var url = ctx + "/sms/toUpdateTemplate.action?templateCode="+tempalteCode;
			window.location.href = url;
			
	   	}  	
  		$(function () {
  		    $('#appCode').comboSelect();
  		})
  	
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
		
			<table width="1100px" height="100%" align="center">
				<tr>
					<th align="left">模板查询</th>
				</tr>
				<tr>
					<td>
						<div align="center">
							<table class="table table-bordered ">
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信模板编号</td>
									<td style="width: 150px"><input id="templateCode" type="text"
										name="templateCode" value="${templateCode}"
										style="width: 200px" placeholder="请输入模板编号搜索" /></td>
									<td style="width: 150px; background-color:#f9f9f9">创建时间段</td>
									<td style="width: 600px">
									<input type="text"
											class="form-control" value="${startTime}" name="startTime" id="startTime"
											readonly
											onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									----		
									<input type="text"
										class="form-control"  value="${endTime}" name="endTime" readonly id="endTime"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									</td>
								</tr>
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信类型</td>
									<td style="width: 150px">
										<select id="typeCode">
											<option value="">全部</option>
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
									<td style="width: 150px;  background-color:#f9f9f9">短信标题</td>
									<td style="width: 600px"><input id="title" type="text" value="${title}"
										name="title" style="width: 200px" placeholder="请输入短信标题搜索" /></td>
								</tr>
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信应用</td>
									<td style="width: 150px">
										<select id="appCode" class="combo-select" style="display:inline-block;border-style:none;">
											<option value="" >请选择或输入应用名</option>
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

							</table>
							<input id="search" type="button" class="btn btn-primary"
								value="搜索" />&nbsp; <input id="reset" type="button"
								class="btn btn-primary" value="重置" />
						</div>
					</td>
				</tr>
			</table>
		</div>
	</form>
	<table width="1100px" height="100%" align="center">
			<tr>
				<th align="left"><a id="add" class="btn btn-primary">新增短信模板</a></th>
			</tr>
			<tr>
				<td>
					<div align="center" style="height:600px;overflow:auto;">
					<table class="table table-bordered ">
						<thead>
							<tr style="background-color:#f9f9f9">
								<th style="width:200px">短信模板编号</th>
								<th style="width:150px">短信类型</th>
								<th style="width:150px">短信应用</th>
								<th style="width:150px">短信标题</th>
								<th style="width:400px">短信内容</th>
								<th style="width:50px">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty page.result}">
								<tr><td colspan="7" align="center">暂无数据</td></tr>
							</c:if>
							<c:forEach items="${page.result}" var="template">
								<tr>
									<td>${template.templateCode}</td>
									<td>${template.typeName}</td>
									<td>${template.appCode}</td>
									<td>${template.title}</td>
									<td>${template.content}</td>
									<td>
										<input id="reset" type="button" class="btn btn-primary" id = "${template.templateCode}" href="#" onclick="update('${template.templateCode}');" value="编辑" />
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
</body>
</html>
