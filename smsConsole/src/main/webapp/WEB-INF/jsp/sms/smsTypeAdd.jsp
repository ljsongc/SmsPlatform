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
	var strLis = tableGet();
	var typeName = $('#typeName').val();
	var channelCode = $('#channelCode').val();
	var typeCode = $('#typeCode').val();
	var remark = $('#remark').val();
	var spareChannelCode = $('#spareChannelCode').val();
	if(!check(typeCode,typeName,channelCode,strLis)){
		return
	}
	var url = ctx + '/sms/createSmsType.action?typeName='
			+ typeName + '&strLis=' + strLis+'&remark='+remark+'&typeCode='+typeCode;
	$.ajax({
		url : url,
		global : false,
		type : 'post',
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

function tableGet(){
	var tb=document.getElementById('rateList');    //获取table对像
    var rows=tb.rows;
    var temp = null;
      for(var i=0;i<rows.length;i++){
    	  //--循环所有的行
        var cells=rows[i].cells;
        var channel = null ;
        var rate = null ;
        var temprow=null;
        if(i>0){
        	for(var j=1;j<=cells.length;j++){   //--循环所有的列
        		if(j==1){
        			//获取通道
        			var check = document.getElementById("channel"+i);
        			if(check.checked){
        				channel	= document.getElementById("channel"+i).value;
        			}
        		}
        		if(j==2){
        			if(channel!= null){
        				//获取分流比
            			rate = document.getElementById("rate"+i).value;
        			}


        		}
		        }
        	//拼接单行值
        	if(channel!=null && channel!='' && rate!=''&& rate!=null){
        		temprow = channel+"~~"+rate+"~~~~"
        	}

    		//
    		if(temp==null){
    			temp =temprow;
    		}else{
    			if(temprow!=null && temprow!=''){
    				 temp += temprow ;
    			}

    		}
        }
     }
      return temp;
}
function check(typeCode,name,channelCode,strLis){

	if(typeCode==undefined || typeCode==''){
		alert("请输入类型编码！")
		return false;
	}


	if(name==undefined||name==''){
		alert("请输入类型名称！")
		return false;
	}

	if(strLis==null || strLis==''){
		alert("请选择通道并设置分流比！");
		return false;
	}

	var rate =0;
	var num=0;
	var str = strLis.split("~~~~");
	if(str!=null && str !=''){
		for(i=0;i<str.length-1;i++){
			var listvalue=str[i];
			if(listvalue!=null && listvalue!=''){
				var ratestr = listvalue.split("~~");
				var checknum = ratestr[1];
				num =  parseInt(ratestr[1]);
				//if(!/^[1-9]\d*|0$/.test(num)){
					//alert("分流比必须为非负整数！")
					//return false;
				//}

			}
			rate += num;
		}
		if(!(rate ==10 ||rate ==100 || rate ==1000 || rate ==10000)){
			alert("通道比之和必须是10,100,1000,10000中一个请重新检查！");
			return false;
		}
	}


	var check=false;
	var url = ctx + '/sms/checksms.action?typeName='
	+ name + '&channelCode=' + channelCode+'&typeCode='+typeCode;
		$.ajax({
		url : url,
		global : false,
		type : 'GET',
		async : false, //同步
		success : function(data) {
			if('existence'==data){
				alert("同一个通道相同短信类型已经存在，请重新填写短信类型！");

			}else{
				check=true;
			}
		}
		});

	if(check){
		return true;
	}else{
		return false;
	}

}


function refreshParent() {
	var url = ctx + "/sms/listSmsType.action";
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
		<div align="left"><h4>新增短信类型</h4></div>
		</br>
		<div style="max-height:600px; overflow: auto;">
			<div>
				<table  class="table table-bordered table-striped" width="600px" style="word-break:break-all; word-wrap:break-all;">

				<tr>
				<td>类型编码</td>
				<td><input id="typeCode"/><font color="red">*</font></td>
				</tr>

				<tr>
				<td>短信类型</td>
				<td><input id="typeName" /><font color="red">*</font></td>
				</tr>

				<tr>
				<td>说明</td>
				<td><textarea rows="2" cols="40" maxlength="50" id="remark" name="remark"></textarea></td>
				</tr>

				<tr>
				<td>短信通道路由</td>
				<td>
				<table id="rateList">
				<tr>
				<td>通道</td>
				<td>分流比</td>
				<td>状态</td>
				</tr>
				<c:forEach items="${smsEntity}" var="liseType"  varStatus="entiy">
				<tr>
				<td><input type="checkbox" id="channel${entiy.index+1 }" name="channel"  value="${liseType.channelCode}"/>${liseType.name }</td>
				<td><input type="text" id="rate${entiy.index+1 }"/></td>
				<td>
					<c:if test="${liseType.state=='TRUE' }">生效</c:if>
					<c:if test="${liseType.state=='FALSE' }">失效</c:if>
				</td>
				</tr>
				</c:forEach>
				</table>

				</td>
				</tr>

				<!-- <tr>
				<td>备用通道</td>
				<td>
				<select id="spareChannelCode" name="spareChannelCode">
										<c:forEach items="${smsEntity}" var="liseType">
											<option value="${liseType.channelCode}">${liseType.name }</option>
										</c:forEach>
				</select>
				</td>
				</tr> -->
				</table>
					<center>
				<input type="button"  class="btn btn-primary"  value="取消"  onclick="refreshParent();"></input>
				<input type="button"  class="btn btn-primary" value="确认"  onclick="createchannel();"></input>
			</center>
			</div>
		</div>

</div>
</body>
</html>
