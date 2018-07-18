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
  	
  	<script type="text/javascript">
	    //表单校验
	    function validate_form()
	    {
	    	var appCode = $("#appCode").val();
	    	if(appCode == ''){
	       		alert("请录入应用标识！");
	       		return false;
	       	}
	    	
	    	var to = $("#to").val();
	    	if(to == ''){
	       		alert("请录入手机号码！");
	       		return false;
	       	}
	    	
	    	var token = $("#token").val();
	    	if(token == ''){
	       		alert("请录入认证标识！");
	       		return false;
	       	}
	    	
    		var content = $("#content").val();
    		if(content == ""){
    			alert("请录入短信内容！");
    			return false;
    		}
	    	
    		return true;
	    }
	    
		$(function() {
			
		   	$("#submit").click(function(){
		   		$("#inputForm").submit();
		   	});
		   	
	    });
  	
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
	<form id="inputForm" class="form-horizontal" action="${pageContext.request.contextPath}/sms/send.action" method="post" onsubmit="return validate_form()">
		<table width="800px" height="100%" align="center">
			<tr>
				<th>短信发送</th>
			</tr>
			<tr>
				<td>
					<br/>
					<div align="center">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th>应用标识</th>
								<th>手机号码</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" id="appCode" name="appCode"/></td>
								<td><input type="text" id="to" name="to"/></td>
							</tr>
						</tbody>
						<thead>
							<tr>
								<th>认证标识</th>
								<th>发送时间</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" id="token" name="token"/></td>
								<td>
									<input class="Wdate" type="text" id="timeStr" name="timeStr" 
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'%y-%M-{%d}',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})" readOnly/>
								</td>
							</tr>
						</tbody>
						<thead>
							<tr>
								<th>短信级别</th>
								<th>短信类型</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>
									<select id="level" name="level">
										<option value="default">———请选择短信级别———</option>
										<c:forEach items="${smsSendLevels}" var="smsSendLevel">
											<c:if test="${smsSendLevel == 'NORMAL'}"><option value="${smsSendLevel}">普通</option></c:if>
											<c:if test="${smsSendLevel == 'WARN'}"><option value="${smsSendLevel}">告警</option></c:if>
											<c:if test="${smsSendLevel == 'ERROR'}"><option value="${smsSendLevel}">错误</option></c:if>
											<c:if test="${smsSendLevel == 'FATAL'}"><option value="${smsSendLevel}">严重</option></c:if>
										</c:forEach>
									</select>
								</td>
								<td>
									<select id="type" name="type">
										<option value="default">———请选择短信类型——</option>
										<c:forEach items="${smsSendTypes}" var="smsSendType">
											<c:if test="${smsSendType == 'SALE'}"><option value="${smsSendType}">营销</option></c:if>
											<c:if test="${smsSendType == 'NOTICE'}"><option value="${smsSendType}">通知</option></c:if>
										</c:forEach>
									</select>
								</td>
							</tr>
						</tbody>
						<thead>
							<tr>
								<th colspan="2">短信内容</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="2"><textarea style="width:90%;heigh:80%" id="content" name="content" maxlength='2048' rows='4'></textarea></td>
							</tr>
						</tbody>
						<tbody>
							<tr>
								<td colspan="2">
									<br/>
									<font color="red" size="4"><B>使用说明：</B></font><br/><br/>
									1、短信内容不得超过450字。</br>
									2、短信单条允许64字，超出拆分为多条。<br/>
									3、短信级别不指定，则默认使用普通级别。<br/>
									4、发送时间不指定，则使用服务端接收时的当前时间。<br/>
									5、应用标识、手机号码、认证标识、短信内容必须指定。<br/>
									6、多联系人，请使用英文逗号分隔<br/>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				</td>
			</tr>
		</table>
		<div align="center"><a id="submit" class="btn btn-default btn-lg active">提交</a></div>
	</form>
</body>
</html>
