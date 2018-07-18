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
	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>
  	
  	<script type="text/javascript">

  		$(function() {
  			var ctx='<%=request.getContextPath()%>';
  			$("#reset").click(function(){
		   		$("#appCode").val("");
		   		$("#startTime").val("");
		   		$("#endTime").val("");
		   	});
  			
  			$("#add").click(function(){
  				var url = ctx + "/sms/toAddToken.action";
  				if(window.showDialog == undefined){
  					var iWidth = 600;
  					var iHeight = 400;
  					var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
  					var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
  					var win = window.open(url, "", "width=" + iWidth + ", height=" + iHeight + ",top=" + iTop + ",left=" + iLeft + ",toolbar=no, menubar=no, scrollbars=yes, resizable=no,location=no, status=no,alwaysRaised=yes,depended=yes");
	  			}else{
	  				var rv = window.showDialog(url,"","dialogWidth=600px;dialogHeight=600px;dialogLeft=400px;dialogTop=200px;center=no;resizable=no");
	  			}  
  				/* window.parent.parent.parent.refresh("popframe-1", url);
				window.parent.parent.parent.showDialog("popdiv-1"); */
		   	});
  			
  			/* $(".update").click(function(){
  				var id = $(this).attr("id");
  				var appCode = $(this).attr("appCode");
  				window.location.href = "${pageContext.request.contextPath}/sms/toUpdateToken.action?id="+id+"&appCode="+appCode;
		   	}); */
  			
  			$(".delete").click(function(){
  				var flag = confirm("确定删除吗");
  				if(flag){
  					var id = $(this).attr("id");
  					var appCode = $(this).attr("appCode");
  	  				window.location.href = "${pageContext.request.contextPath}/sms/deleteToken.action?id="+id+"&appCode="+appCode;
  				}
		   	});
		   	
		   	$("#search").click(function(){
		   		$(this).attr("disabled","disabled");
		   		var appCode = $("#appCode").val();
  				var startTime = $("input[name='startTime']").val();
  				var endTime = $("input[name='endTime']").val();
  				if(startTime != '' && endTime == ''){
  					alert("请选择结束时间");
  					return;
  				}
  				if(startTime == '' && endTime != ''){
  					alert("请选择开始时间");
  					return;
  				}
  				window.location.href = "${pageContext.request.contextPath}/sms/searchToken.action?"
  					+"appCode="+appCode+"&startTime="+startTime+"&endTime="+endTime;
		   	});
		   
		   
  			
	    });
  		
  		
  		function update(id){
  			var ctx='<%=request.getContextPath()%>';
  			 var url = ctx + "/sms/toUpdateToken.action?id="+id;
  			if(window.showModalDialog == undefined){
					var iWidth = 600;
					var iHeight = 400;
					var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
					var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
					var win = window.open(url, "", "width=" + iWidth + ", height=" + iHeight + ",top=" + iTop + ",left=" + iLeft + ",toolbar=no, menubar=no, scrollbars=yes, resizable=no,location=no, status=no,alwaysRaised=yes,depended=yes");
  			}else{
  				var rv = window.showModalDialog(url,"","dialogWidth=600px;dialogHeight=600px;dialogLeft=400px;dialogTop=200px;center=no;resizable=no");
  			} 
  			/* window.parent.parent.parent.refresh("popframe-1", url);
			window.parent.parent.parent.showDialog("popdiv-1"); */
			
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
					<th align="left">短信应用</th>
				</tr>
				<tr>
					<td>
						<div align="center">
							<table class="table table-bordered ">
								<tr  height="15px">
									<td style="width: 300px; background-color:#f9f9f9">创建时间段</td>
									<td style="width: 800px">
									<input type="text"
											class="form-control" value="${startTime}" name="startTime" id="startTime"
											readonly
											onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									----		
									<input type="text"
										class="form-control" value="${endTime}" name="endTime"  id="endTime" readonly
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									</td>
								</tr>
								<tr  height="15px">
									<td style="width: 300px;  background-color:#f9f9f9">短信应用</td>
									<td style="width: 800px"><input id="appCode" type="text"
										name="appCode" style="width: 200px"  value="${appCode}"
										placeholder="请输入短信应用编码搜索" /></td>
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
			<!-- <tr>
				<th align="left"><a id="add" class="btn btn-primary">新增短信应用</a></th>
			</tr> -->
			<tr>
				<td>
					<div align="center">
					<table class="table table-bordered ">
						<thead>
							<tr style="background-color:#f9f9f9">
								<!-- <th>短信应用</th> -->
								<th>短信应用编码</th>
								<th>创建时间</th>
								<!-- <th>操作员</th> -->
								<!-- <th>操作</th> -->
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty page.result}">
								<tr><td colspan="5" align="center">暂无数据</td></tr>
							</c:if>
							<c:forEach items="${page.result}" var="token">
								<tr>
									<%-- <td>${token.appName}</td> --%>
									<td>${token.appCode}</td>
									<td>
										 <fmt:formatDate value="${token.createTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
									</td>
									<%-- <td>${token.operator}</td>
									<td>
										<a class="update" id = "${token.id}" appCode="${token.appCode}" href="#" onclick="update(${token.id});">编辑</a>&nbsp;&nbsp;&nbsp;
										<a class="delete" id = "${token.id}" appCode="${token.appCode}" href="#">删除</a>&nbsp;&nbsp;&nbsp;
									</td> --%>
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
