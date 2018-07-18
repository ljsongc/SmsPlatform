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
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrapV3.min.css">
  	<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
  	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  	<script src="<%=request.getContextPath()%>/js/bootstrapV3.min.js"></script>

<script type="text/javascript">

var ctx='<%=request.getContextPath()%>';

function add(){
	var url = ctx + "/sms/addSmsType.action";

	window.location.href = url;



}

function toupdate(typeCode){
	var url = ctx + "/sms/toUpdateSmsType.action?typeCode="+typeCode;

	window.location.href = url;
}


function searchfunc(){
	var typeCode = $("#typeCode").val();
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	window.location.href = "${pageContext.request.contextPath}/sms/listSmsType.action"
			+"?startTime="+startTime+"&endTime="+endTime+"&typeCode="+typeCode;

}

function resetc(){
	$("#typeCode").val("");
	$("#startTime").val("");
	$("#endTime").val("");

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
		<div align="center">
			<h4>短信类型管理</h4>
		</div>
		<div class="row">
			<form class="form-horizontal" align="center">
			<table width="1100px" height="100%" align="center">
				<tr>
					<td>
						<div align="center">
			<table class="table table-bordered ">
			<tr>
			<td>短信类型</td>
			<td>
			<select id="typeCode">
						      <option value="">全部</option>
										<c:forEach items="${liseType}" var="type">
												<c:if test="${type.typeCode == typeCode}">
													<option value="${type.typeCode}" selected="selected">${type.typeName}</option>
												</c:if>
												<c:if test="${type.typeCode != typeCode}">
													<option value="${type.typeCode}">${type.typeName}</option>
												</c:if>
										</c:forEach>



									</select>
			</td>
			<td style="background-color:#f9f9f9">创建时间段</td>
									<td style="width: 600px">
									<input type="text"
							  value="${startTime}" name="startTime" id="startTime"
							readonly
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									----
									<input type="text"
							  value="${endTime}" name="endTime" id="endTime" readonly
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
									</td>

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
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table width="1100px" align="center">
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
         <table class="table table-bordered table-striped" style="table-layout:fixed;">
            <thead>
              <tr>
               <th>类型编号</th>
                <th>短信类型</th>
                <th width="30%">短信路由</th>
                <th>创建时间</th>
                <th>说明</th>
                <th>操作员</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${page.result}" var="smsType">
                <tr>
                  <td>${smsType.typeCode}</td>
                  <td>${smsType.typeName}</td>
                  <td>${smsType.rateRemark}</td>
                  <td>
                  <fmt:formatDate value="${smsType.createTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
                  </td>
                  <td>${smsType.remark}</td>
                  <td>${smsType.operator}</td>
                  <td>
                  <button class="btn btn-primary" onclick="toupdate('${smsType.typeCode}')">编辑</button>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </td>
      </tr>
  </table>
				<div align="center">
					<tags:newPagination page="${page}" paginationSize="${page.pageSize}"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
