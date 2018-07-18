<%@tag import="java.net.URLEncoder"%>
<%@tag pageEncoding="UTF-8"%>
<%@ attribute name="page" type="com.pay.sms.console.web.util.Page" required="true"%>
<%@ attribute name="paginationSize" type="java.lang.Integer" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
int current =  page.getPageNo();
long begin = Math.max(1, current - paginationSize/2);
long end = Math.min(begin + (paginationSize - 1), page.getTotalPages());
request.setAttribute("current", current);
request.setAttribute("begin", begin);
request.setAttribute("end", end);
String condition = "";
java.util.Map<String,String> map = page.getDatas();
for(java.util.Map.Entry<String, String> entry : map.entrySet()){
	if(entry.getValue() != null && !"".equals(entry.getValue())){
		condition += "&" + entry.getKey() + "=" + URLEncoder.encode(URLEncoder.encode(entry.getValue(), "UTF-8"), "UTF-8");
	}
}
request.setAttribute("condition", condition);
%>
<div class="pagination pagination-centered">
	<ul>
		
		<!-- 总的条数 -->
		<li class="disabled"><a>共${page.totalCount}条数据</a></li>
		
		<!-- 左边的< 箭头 -->
		 <% if ((page.isHasNext() && current != 1) || (current == end && current != 1)){%>
               	<li><a href="?p=1&ps=${page.pageSize}${condition}">&lt;&lt;</a></li>
                <li><a href="?p=${current-1}&ps=${page.pageSize}${condition}">&lt;</a></li>
         <%}else{%>
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
                <li class="disabled"><a href="#">&lt;</a></li>
         <%} %>

		<!-- 中间的页码 -->
		<c:forEach var="i" begin="${begin}" end="${end}">
            <c:choose>
                <c:when test="${i == current}">
                    <li class="active"><a href="?p=${i}&ps=${page.pageSize}${condition}">${i}</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="?p=${i}&ps=${page.pageSize}${condition}">${i}</a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>

		<!-- 右边的> 箭头 -->
	  	 <% if (page.isHasNext()){%>
               	<li><a href="?p=${current+1}&ps=${page.pageSize}${condition}">&gt;</a></li>
                <li><a href="?p=${page.totalPages}&ps=${page.pageSize}${condition}">&gt;&gt;</a></li>
         <%}else{%>
                <li class="disabled"><a href="#">&gt;</a></li>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
         <%} %>
	</ul>
</div>