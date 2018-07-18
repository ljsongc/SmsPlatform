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
//提交分流比
function submitDistributeForm(){
	var masterTotal = $("input[name=masterTotal]").val();
	var spareTotal = $("input[name=spareTotal]").val();
	var flag = checkDistributeParam(masterTotal, spareTotal);
	if(flag){
		$("#distributeForm").submit();
	}
}
//ajax获得通道分流比
function getDistributeRate(){
	$.ajax({
		url:'getDistributeRate.action',
		type:'POST',
		dataType:'json',
		success:function(data){
			var error = data.error;
			if(error!==undefined){
				alert("error:"+error);
			}else{
				var masterTotal = data.masterTotal;
				var spareTotal = data.spareTotal;
				$("input[name=masterTotal]").val(masterTotal);
				$("input[name=spareTotal]").val(spareTotal);
				$("#modal").modal('show');
			}
		}
	})
}

function updateDistributeRate(){
	getDistributeRate();
}

function setsatatus(id,status) {
		var r = confirm("确认更改通道状态？");
		if (r == true) {
			var url = ctx + '/setsatatus.action?id=' + id+"&status="+status;
			$.ajax({
				url : url,
				global : false,
				type : 'GET',
				async : false, //同步
				success : function(data) {
					if('success'==data){
						alert("修改成功！");
					}else{
						alert("修改失败:"+data);
					}
					location.reload();
				}
			})
		}
	}


function createchannel() {
	var name = $('#channel-name').val();
	var type = $('.channel-type:checked').val();
	var state = $('.channel-state:checked').val();
	var weight = $('#channel-weight').val();
	if(!check(name,type,state,weight)){
		return
	}
	var url = ctx + '/createchannel.action?name='
			+ name + '&type=' + type + '&state=' + state + '&weight=' + weight;
	$.ajax({
		url : url,
		global : false,
		type : 'GET',
		async : false, //同步
		success : function(data) {
			if('success'==data){
				alert("新增成功！");
			}else{
				alert("新增失败:"+data);
			}
			location.reload();
		}
	})
};
function updatechannel(id) {
	var name = $('#' + id + 'name').val();
	var type = $('.' + id + 'type:checked').val();
	var state = $('.' + id + 'state:checked').val();
	var weight = $('#' + id + 'weight').val();
	if(!check(name,type,state,weight,id)){
		return
	}
	var url = ctx + '/updatechannel.action?id=' + id + '&name='
			+ name + '&type=' + type + '&state=' + state + '&weight=' + weight;
	$.ajax({
		url : url,
		global : false,
		type : 'GET',
		async : false, //同步
		success : function(data) {
			if('success'==data){
				alert("修改成功！");
			}else{
				alert("修改失败:"+data);
			}
			location.reload();
		}
	})
};

function existWeight(weight,id){
	var flag = false;
	var type;
	if(id === undefined){
		type = $('[name=channel-type]:checked').val();
	}else{
		type = $('[name='+id+'type]:checked').val();
	}
	$('.' + type + 'weight').each(function(){
		var idWeight = $(this).attr("id");
		if(id + 'weight' != idWeight){
			var value = $(this).val();
			if(value == weight){
				flag = true;
				return false;//在each中，等同于break;
			}
		}
	});
	return flag;
}

function check(name,type,state,weight,id){
	if(name==undefined||name==''){
		alert("请输入通道名！")
		return false;
	}
	if(type==undefined||type==''){
		alert("请选择通道类型！")
		return false;
	}
	if(state==undefined||state==''){
		alert("请选择通道状态！")
		return false;
	}
	if(weight==undefined||weight==''){
		alert("请输入通道权重！")
		return false;
	}
	if(!/^[1-9]\d*$/.test(weight)){
		alert("通道权重必须为正整数！")
		return false;
	}
	if(existWeight(weight,id)){
		alert("通道权重已存在！")
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




function add(){
	var url = ctx + "/addchannel.action";
	window.location.href = url;
}

function toupdate(id){
	var url = ctx + "/toUpdatechannel.action?id="+id;

	window.location.href = url;

}

function reloadadd(){
	location.reload();
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
	<div style="height:750px;overflow:auto;">
	<table width="1100px" align="center">
		<tr>
			<th>短信通道管理</th>
		</tr>
		<tr>
			<td>
				<div align="center">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<button class="btn btn-primary" onclick="add()">新增</button>

					<table class="table table-bordered">
						<thead>
							<tr>
								<th>通道名称</th>
								<th>通道状态</th>
								<th>通道成本</th>
								<th>通道联系方式</th>
								<th>备注说明</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.result}" var="smsChannelEntity">
								<tr>
									<td>${smsChannelEntity.name}</td>
									<td>
									<c:if test="${smsChannelEntity.state == 'TRUE'}">启用</c:if>
									<c:if test="${smsChannelEntity.state == 'FALSE'}">停用</c:if>
									</td>
									<td>${smsChannelEntity.channelCost}</td>
									<td>${smsChannelEntity.channelContact}</td>
									<td>${smsChannelEntity.remark}</td>
									<td>
									<button class="btn btn-primary" onclick="toupdate('${smsChannelEntity.id}')">编辑</button>
									<c:if test="${smsChannelEntity.state == 'TRUE'}">
									<button class="btn btn-primary" onclick="setsatatus(${smsChannelEntity.id},'${smsChannelEntity.state}')">停用</button>
									</c:if>
									<c:if test="${smsChannelEntity.state == 'FALSE'}">
									<button class="btn btn-primary" onclick="setsatatus(${smsChannelEntity.id},'${smsChannelEntity.state}')">启用</button>
									</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div>共 ${page.totalCount} 个短信通道</div>
				</div>
			</td>
		</tr>
	</table>
	</div>

	<!-- 通道分流比模拟框 -->
	<div class="modal hide fade" id="modal">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<div align="center">
				<h3>修改通道分流比</h3>
			</div>
		</div>
		<div class="modal-body">
			<form class="form-horizontal" method="get" action="${pageContext.request.contextPath}/sms/updateDistributeRate.action"
			id="distributeForm">
				<div class="control-group">
					<label class="control-label">主通道</label>
					<div class="controls">
						<input type="text" name="masterTotal"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">备通道</label>
					<div class="controls">
						<input type="text" name="spareTotal"/>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button class="btn btn-primary" onclick="submitDistributeForm()">保存</button>
		</div>
	</div>
</body>
</html>
