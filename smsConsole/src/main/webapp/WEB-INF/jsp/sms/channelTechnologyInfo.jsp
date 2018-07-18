<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>短信管理平台</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.min.css">
</head>

<body>
	<table width="90%" height="100%" align="center">
			<tr>
				<th>短信通道商信息</th>
			</tr>
			<tr>
				<td>
					<br/>
					<div align="center">
					<table class="table table-bordered table-striped" style="word-break:break-all; word-wrap:break-all;">
						<thead>
							<tr>
								<th width="50%">通道商</th>
								<th width="50%">联系方式</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>信鸽通知通道(XinGe)</td>
								<td>QQ群号:634187222</td>
							</tr>
							<tr>
								<td>信鸽营销通道(XinGeSale)</td>
								<td>QQ群号:634187222</td>
							</tr>
							<tr>
								<td>创蓝通知通道(ChuangLan)</td>
								<td>QQ群号:641896350</td>
							</tr>
							<tr>
								<td>创蓝营销通道(ChuangLanSale)</td>
								<td>QQ群号:641896350</td>
							</tr>
							<tr>
								<td>阿里通知通道(Ali)</td>
								<td>暂无</td>
							</tr>
						</tbody>
					</table>
				</div>
				</td>
			</tr>
		</table>
</body>
</html>
