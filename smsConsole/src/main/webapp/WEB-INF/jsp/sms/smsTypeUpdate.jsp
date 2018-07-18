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



$(function() {

	var tb=document.getElementById('rateList');    //获取table对像
    var rows=tb.rows;


    var tbb=document.getElementById('hiddenRate');    //获取table对像
    var rowsb=tbb.rows;
      for(var i=0;i<rows.length;i++){
        if(i>0){
		var channelCode = $("#channel"+i).val();
		var smsState = $("#smsState"+i).val();


		 for(var ib=0;ib<rowsb.length;ib++){
			 var channelCodeb = $("#hidrate"+ib).val();
			 var channelrate = $("#ratevalue"+ib).val();


			 if(channelCode == channelCodeb){
				 document.getElementById("channel"+i).checked = true;
				 $("#rate"+i).val(channelrate)
			 }

		 }

        }
     }
});

var ctx='<%=request.getContextPath()%>';
function createchannel() {
	var strLis = tableGet();
	var typeName = $('#typeName').val();
	var channelCode = $('#channelCode').val();
	var remark = $('#remark').val();
	var spareChannelCode = $('#spareChannelCode').val();
	var typeCode = $('#typeCode').val();
	if(!check(typeName,channelCode,typeCode,strLis)){
		return
	}
	var url = ctx + '/sms/updateSmsType.action?typeName='
			+ typeName +'&remark='+remark+'&typeCode='+typeCode+'&strLis=' + strLis;
	$.ajax({
		url : url,
		global : false,
		type : 'GET',
		async : false, //同步
		success : function(data) {
			if('success'==data){
				alert("修改成功！");
				refreshParent();
			}else{
				alert("修改失败:"+data);
			}
		}
	})
};


function check(name,channelCode,typeCode,strLis){
	if(name==undefined||name==''){
		alert("请输入通道类型！")
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
				alert("相同短信类型已经存在，请重新填写短信类型！");

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
		<div align="left"><h4>修改短信类型</h4></div>
		</br>
		<div style="max-height:600px; overflow: auto;">
			<div>
				<table   class="table table-bordered table-striped" width="600px" style="word-break:break-all; word-wrap:break-all;">
				<input type="hidden" id="listChannel" value="${listChannel}"/>

				<tr>
				<td>短信编码</td>
				<td><input id="typeCode" value="${smsType.typeCode }"  disabled="disabled"/></td>
				</tr>
				<tr>
				<td>短信类型</td>
				<td><input id="typeName" value="${smsType.typeName }" /></td>
				</tr>

				<tr>
				<td>说明</td>
				<td><textarea rows="2" cols="40" maxlength="50" id="remark" name="remark">${smsType.remark }</textarea></td>
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
				<c:forEach items="${listChannel}" var="liseType"  varStatus="entiy">
					 <tr>
					<td><input type="checkbox" id="channel${entiy.index+1 }" name="channel"  value="${liseType.channelCode}"/>${liseType.name }</td>
					<td><input type="text" id="rate${entiy.index+1 }"/>
					</td>
					<td>
					<c:if test="${liseType.state=='TRUE' }">生效</c:if>
					<c:if test="${liseType.state=='FALSE' }">失效</c:if>
					</td>
					</tr>
				</c:forEach>

				</table>
				</td>
				</table>

				<table id="hiddenRate" style="display: none">
				<c:forEach items="${rateList}" var="rate" varStatus="entiyr">
				<tr>
				<td>
				<input type="checkbox" id="hidrate${entiyr.index }" value="${rate.channelCode}"/>
				<input type="text" id="ratevalue${entiyr.index }" value="${rate.rate }"/>
				</td>
				</tr>

				</c:forEach>
				</table>
				<center>
				<input type="button"  class="btn btn-primary" value="取消" onclick="refreshParent();"></input>
				<input type="button"  class="btn btn-primary" value="确认" onclick="createchannel();"></input>
				</center>
			</div>
		</div>

</div>
</body>
</html>
