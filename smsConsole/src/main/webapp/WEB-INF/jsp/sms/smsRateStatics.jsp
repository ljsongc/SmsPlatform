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
  	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/combo.select.css">
  	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.structure.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.theme.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jqgrid/css/ui.jqgrid.css" />
	
	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/theme/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/jquery.jqGrid.src.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqgrid/js/i18n/grid.locale-cn.js"></script>
  	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.combo.select.js"></script>
  	<script type="text/javascript">

  		$(function() {
  			var ctx='<%=request.getContextPath()%>';
  			$("#reset").click(function(){
		   		$("#startTime").val("");
		   		$("#endTime").val("");
		   		$("#channelNo").val("");
		   		$("#appCode").val("");
		   		$("input:checkbox").removeAttr("checked");
		   	});
  			
  			
		   	
		   	$("#search").click(function(){
		   		$(this).attr("disabled","disabled");
		   		var channels = getchannelNo();
		   		var typeCodes = getTypeCode();
  				var appCode = $("#appCode").val();
  				var startTime = $("#startTime").val();
  				var endTime = $("#endTime").val();
  				window.location.href = "${pageContext.request.contextPath}/searchRateStatics.action?"
  					+"appCode="+appCode+"&startTime="+startTime+"&endTime="+endTime+"&channelCodes="+
  					channels+"&typeCodes="+typeCodes;
		   	});
		   
		   
  			
	    });
  		
  		function getchannelNo(){  
  	        var test = $("input[name='channelNo']:checked");  
  	        var checkBoxValue = "";   
  	        test.each(function(){  
  	            checkBoxValue += $(this).val()+",";  
  	        })  
  	        checkBoxValue = checkBoxValue.substring(0,checkBoxValue.length-1 ); 
  	        return checkBoxValue;
  		}
  		
  		function getTypeCode(){  
  	        var test = $("input[name='typeCode']:checked");  
  	        var checkBoxValue = "";   
  	        test.each(function(){  
  	            checkBoxValue += $(this).val()+",";  
  	        })  
  	        checkBoxValue = checkBoxValue.substring(0,checkBoxValue.length-1 ); 
  	        return checkBoxValue;
  		}
  	          
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
					<th align="left">短信计数查询</th>
				</tr>
				<tr>
					<td>
						<div align="center">
							<table class="table table-bordered ">
								<tr  height="15px">
									<td style="width: 150px; background-color:#f9f9f9">发送时间段</td>
									<td style="width: 600px">
									<input type="text"
											class="form-control" value="${startTime}" name="startTime" id="startTime"
											readonly
											onClick="WdatePicker({dateFmt:'yyyy-MM-dd',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									----		
									<input type="text"
										class="form-control"  value="${endTime}" name="endTime" readonly id="endTime"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									</td>
								</tr>
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信通道</td>
									<td style="width: 150px">
										<c:forEach items="${channels}" var="channel">
											<c:set var="isDoing" value="0"/> 
											<c:forEach items="${channelCodes}" var="channelCode">
												<c:if test="${channel.name == channelCode}">
													<input type="checkbox" name="channelNo" value="${channel.name}" checked="true" />${channel.name}
													<c:set var="isDoing" value="1"></c:set> 
												</c:if>
											</c:forEach>
											<c:if test="${isDoing!='1'}"> 
												<input type="checkbox" name="channelNo" value="${channel.name}" />${channel.name}
											</c:if>
										</c:forEach>
									</td>	
								</tr>
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信类型</td>
									<td style="width: 150px">
										<c:forEach items="${types}" var="type">
											<c:set var="isDoing" value="0"/> 
											<c:forEach items="${typeCodes}" var="typeCode">
												<c:if test="${type.typeCode == typeCode}">
													<input type="checkbox" name="typeCode" value="${type.typeCode}" checked="true" />${type.typeName}
													<c:set var="isDoing" value="1"></c:set> 
												</c:if>
											</c:forEach>
											<c:if test="${isDoing!='1'}"> 
												<input type="checkbox" name="typeCode" value="${type.typeCode}"/>${type.typeName}
											</c:if>
										</c:forEach>
									</td>	
								</tr>
								<tr  height="15px">
									<td style="width: 150px;  background-color:#f9f9f9">短信应用</td>
									<td style="width: 150px">
										<select id="appCode" class="combo-select" style="display:inline-block;border-style:none;width:300px">
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
				<!-- <th align="left"><a id="add" class="btn btn-primary">新增短信模板</a></th> -->
			</tr>
			<tr>
				<td>
					<div align="center" style="height:600px;overflow:auto;">
					<table class="table table-bordered ">
						<thead>
							<tr style="background-color:#f9f9f9">
								<th>统计时间</th>
								<th>短信通道</th>
								<th>短信类型</th>
								<th>短信应用</th>
								<th>发送短信数</th>
								<th>短信成功数</th>
								<th>短信失败数</th>
								<th>发送成功率</th>
								<th>送达成功数</th>
								<th>送达失败数</th>
								<th>送达成功率</th>
								<th>计费条数统计</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty page.result}">
								<tr><td colspan="13" align="center">暂无数据</td></tr>
							</c:if>
							<c:forEach items="${page.result}" var="statics">
								<tr>
									<td><fmt:formatDate value="${statics.time}" pattern="yyyy-MM-dd"/></td>
									<td>${statics.channelCode}</td>
									<td>${statics.typeName}</td>
									<td>${statics.appCode}</td>
									<td>${statics.sendTotal}</td>
									<td>${statics.sendSuccessTotal}</td>
									<td>${statics.sendFailTotal}</td>
									<td>${statics.sendSuccessRate}</td>
									<td>${statics.receiveSuccessTotal}</td>
									<td>${statics.receiveFailTotal}</td>
									<td>${statics.receiveSuccessRate}</td>
									<td>${statics.feeTotal}</td>
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
