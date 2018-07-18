<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
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



<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>

<script type="text/javascript">

var ctx='<%=request.getContextPath()%>';
function createchannel() {
	var name = $('#name').val();
	var channelContact = $('#channelContact').val();
	var remark = $('#remark').val();
	var channelCost = $('#channelCost').val();
	if(!check(name,channelCost)){
		return
	}
	var url = ctx + '/createchannel.action?name='
			+ name +'&channelContact='+channelContact+'&remark='+remark+'&channelCost='+channelCost;
	$.ajax({
		url : url,
		global : false,
		type : 'GET',
		async : false, //同步
		success : function(data) {
			if('success'==data){
				alert("新增成功！");
				refreshParent();
			}else{
				alert("新增失败:"+data);
			}

		}
	})
};


function check(name,channelCost,id){
	if(name==undefined||name==''){
		alert("请输入通道名！")
		return false;
	}

	if(channelCost==undefined||channelCost==''){
		alert("请输入通道成本！")
		return false;
	}
	return true;
}

function checkDistributeParam(masterTotal, spareTotal){
	if(!/^[1-9]\d*|0$/.test(masterTotal)){
		alert("主通道分流比必须为非负整数！")
		return false;
	}
	if(!/^[1-9]\d*|0$/.test(spareTotal)){
		alert("备通道分流比必须为非负整数！")
		return false;
	}
	if(masterTotal+spareTotal==0){
		alert("分流比之和不得为0！")
		return false;
	}
	return true;
}

function refreshParent() {
	var url = ctx + "/listchannel.action";
		window.location.href = url;
	 }

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
	<div align="left"><h4>新增短信通道</h4></div>
		</br>

		<div >
			<div>
				<table class="table table-bordered table-striped" width="600px" style="word-break:break-all; word-wrap:break-all;">
				<tr>
				<td>通道名称</td>
				<td><input id="name" /><font color="red">*</font></td>
				</tr>

				<tr>
				<td>通道成本</td>
				<td><input id="channelCost" /><font color="red">*</font></td>
				</tr>

				<tr>
				<td>通道联系方式</td>
				<td><input id="channelContact" /></td>
				</tr>
				<tr>
				<td>通道备注</td>
				<td><textarea rows="2" cols="40" maxlength="50" id="remark" name="remark"></textarea></td>
				</tr>
				</table>
					<center>
				<button class="btn btn-primary" onclick="refreshParent()">取消</button>
				<button class="btn btn-primary" onclick="createchannel()">确认</button>
			</center>
			</div>
		</div>

</div>
</body>
</html>
