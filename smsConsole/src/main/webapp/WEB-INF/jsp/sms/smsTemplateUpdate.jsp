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
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/bootstrap.min.css">
<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script
	src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.structure.min.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.theme.min.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/js/jqgrid/css/ui.jqgrid.css" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/combo.select.css">

<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.combo.select.js"></script>
<script type="text/javascript">

var ctx='<%=request.getContextPath()%>';
function returnIndex() {
	var url = ctx + "/sms/searchTemplate.action";
	window.location.href = url;
}
function createtemplate() {
	var typeCode = $('#typeCode').val();
	var appCode = $('#appCode').val();
	var title = $('#title').val();
	var content = $('#content').val();
	var templateCode = $('#templateCode').val();
	var flag = true;
	if(title == null || title == ''){
		alert("标题不能为空");
		flag=false;
	}else{
		if(title.length > 50){
			alert("标题长度不能超过50");
			flag=false;
		}
	}
	if(content == null || content == ''){
		alert("内容不能为空");
		flag=false;
	}else{
		if(content == '[]'){
			alert("内容不能只有变量");
			flag=false;
			
		}
	}
	if(flag){
		var url = ctx + '/sms/updateTemplate.action' ;
			$.ajax({
				url : url,
				global : false,
				type : 'POST',
				data : {'typeCode':typeCode,'appCode':appCode,'title':title,'content':content,'templateCode':templateCode},
				async : false, //同步
				success : function(data) {
					if('success'==data){
						alert("修改成功！");
						var url = ctx + "/sms/searchTemplate.action";
		  				window.location.href = url;
					}else if('repeat'==data){
						alert("同一应用下不能有重复标题");
					}else{
						alert("修改失败");
					}
				}
			}) 
	}
	
};



function addParam() {
	var content = $("#content").val();
	$("#content").val(content+"[]");
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
	<br />
<div class="container">
		<div align="left">
			<h4>编辑短信模板</h4>
		</div>
		</br>
		<div >
			<div>
				<table class="table table-bordered table-striped" width="600px" style="word-break:break-all; word-wrap:break-all;">
				<tr>
				<td>短信类型</td>
				<td><select id="typeCode" style="width:200px;">
						<c:forEach items="${types}" var="type">
							<c:if test="${type.typeCode == smsTemplate.typeCode}">
								<option value="${type.typeCode}" selected="selected" >${type.typeName}</option>
							</c:if>
							<c:if test="${type.typeCode != smsTemplate.typeCode}">
								<option value="${type.typeCode}" >${type.typeName}</option>
							</c:if>
							
						</c:forEach>
					</select><font color="red">*</font></td>
				</tr>

				<tr>
				<td>短信应用</td>
				<td>
					<select id="appCode" class="combo-select" style="display:inline-block;border-style:none;width:200px;">
						<option value="" >请选择或输入应用名</option>
						<c:forEach items="${tokens}" var="token">
							<c:if test="${token.appCode == smsTemplate.appCode}">
								<option value="${token.appCode}" selected="selected">${token.appCode}</option>
							</c:if>
							<c:if test="${token.appCode!=smsTemplate.appCode}">
								<option value="${token.appCode}" >${token.appCode}</option>
							</c:if>
							
						</c:forEach>
					</select>
				<font color="red">*</font></font></td>
				</tr>
				<tr>
				<td>短信标题</td>
				<td><input id="title" type="text" value="${smsTemplate.title}" /><font color="red">*</font></td>
				</tr>
				<tr style="height: 100px">
				<input id="templateCode" type="hidden" value="${smsTemplate.templateCode}" />
				<td>短信内容</td>
				<td>
					<font color="red">点击输入参数</font>
					<input type="button" value="添加参数" class="global_btn" onclick="addParam();"></input>
					<textarea id="content" style="height: 80px;width:90%"  >${smsTemplate.content}</textarea></td>
				</tr>
				

				</table>
			<center>
				<input type="button" value="取消" class="btn btn-primary" onclick="returnIndex();"></input>
				<input type="button" value="确认" class="btn btn-primary" onclick="createtemplate();"></input>
			</center>
			</div>
		</div>

</div>
</body>
</html>
