<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<body>
	<table width="760" height="100%" align="center">
		<tr>
			<td>
			<div align="center">
				<fieldset>
					<legend><small>无法访问</small></legend>
					<table width="600" height="100">
						<tr>
							<td>原因：</td>
						</tr>
						<tr>
							<td><B><font color="red" size="5">${error}</font></B></td>
						</tr>
					</table>
				</fieldset>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>
