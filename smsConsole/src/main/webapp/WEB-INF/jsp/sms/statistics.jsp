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
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrapV3.min.css">
  	<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
  	<script src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
  	<script src="<%=request.getContextPath()%>/js/bootstrapV3.min.js"></script>
  	<script src="<%=request.getContextPath()%>/js/echart/echarts.js"></script>
  	<script src="<%=request.getContextPath()%>/js/echart/macarons.js"></script>
  	<script type="text/javascript">
  		$(document).ready(function(){
  			$("#search").click(function(){
  				$(this).attr("disabled","disabled");
  				var startTime = $("input[name='startTime']").val();
  				startTime = encodeURIComponent(encodeURIComponent(startTime));
  				var endTime = $("input[name='endTime']").val();
  				endTime = encodeURIComponent(encodeURIComponent(endTime));
  				var phone = $("input[name='phone']").val();
  				phone = encodeURIComponent(encodeURIComponent(phone));
  				var content = $("input[name='content']").val();
  				content = encodeURIComponent(encodeURIComponent(content));
  				var channels = getCheckChannel();
  				channels = encodeURIComponent(encodeURIComponent(channels));
  				var type = $("#typeInput").val();
  				type = encodeURIComponent(encodeURIComponent(type));
  				
  				window.location.href = "${pageContext.request.contextPath}/sms/statistics.action"
  					+"?startTime="+startTime+"&endTime="+endTime+"&channels="+channels+"&type="+type; 
  			});
  			
  			//切换短信类型的按钮
  			$("#typeChange").click(function(){
  				var typeButton = $("#typeButton").text();
  				var typeChange = $("#typeChange").text();
  				$("#typeButton").text(typeChange);
  				$("#typeChange").text(typeButton);
  				var type = $("#typeInput").val();
  				if (type == "NOTICE") {
  					type = "SALE";
  				} else {
  					type="NOTICE";
  				}
  				$("#typeInput").val(type);
				//获取通道checkbox并勾选
  				$.ajax({
  					url:"${pageContext.request.contextPath}/sms/getAllChannels.action?"
  							+"type="+type,
  					type:'get',
  					dataType:'json',
  					success:function(data){
  						if(data.length>0){
  							$("#channels").html("");
  							var html = "";
  							$.each(data,function(index,item){
  								html+="<label>"
									+"<input type='checkbox' id='"+item+"' value='"+item+"' checked='checked'>"
									+item
									+"</label>";
  							})
  							$("#channels").html(html);
  						}
  					}
  				});
  			});
  			checkChannel();
  			//勾选通道框
  			function checkChannel(){
  				var channels = "${channels}".split(",");
  				$(channels).each(function(index,text){
					$("#"+text).prop("checked",true);	  					
  				});
  			}
  			//获得勾选的通道英文名，以逗号分隔
  			function getCheckChannel(){
  				var channels = "";
  				$('input:checkbox:checked').each(function(){
  					var channel = $(this).val();
					channels+=channel;
					channels+=",";
  				})
  				if(channels==""){
  					return channels;
  				}else{
  					channels = channels.substring(0,channels.length-1)
  					return channels;
  				}
  			}
  			
  			var height = $(document).height()*1/3;
  			$("#chart").css({"height" : height});
  			//基于准备好的dom，初始化echarts实例
  			var myChart = echarts.init(document.getElementById('chart'),'macarons');
  			initSmsStatisticsChartData();
  			//初始化echart
  			function initSmsStatisticsChartData(){
  				var data = ${smsStatisticsChartData};
  				if(data.length>0){
  					var sum = 0;//总条数
  					var max = 0;//最大条数()
  					var dateList = data.map(function (item) {
  					    return item.date;
  					});
  					var valueList = data.map(function (item) {
  					    var value = parseInt(item.number);
  						sum += value;
  					    if(value > max){
  					    	max = value;
  					    }
  						return value;
  					});
  					
  					option = {
  						    title: {
  						        left: 'center',
  						        text: '总计费条数：'+sum+'条'
  						    },
  						    tooltip: {
  						        trigger: 'axis'
  						    },
  						    xAxis: {
  						        data: dateList
  						    },
  						    yAxis: {
  						        splitLine: {show: false}
  						    },
  						    series: {
  						    	name:'计费条数',
  						        type: 'line',
  						      	showSymbol:true,
						      	symbolSize:4,
  						        data: valueList
  						    }
  						};
					$("#chart").show();
					myChart.setOption(option);
				}else{
					$("#chart").hide();
				}
  			}
  		});
  	</script>
</head>

<body>
<div style="height:950px;overflow:auto;">
	<div class="container">
		<div align="center">
			<h4>短信计数统计</h4>
		</div>
		</br>
		<div class="row">
			<div class="form-horizontal">
				<div class="form-group">
					<label class="col-sm-2 control-label">开始时间</label>
					<div class="col-sm-2">
						<input type="text" class="form-control" value="${startTime}" name="startTime" readonly
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
					</div>
					<label class="col-sm-2  control-label">结束时间</label>
					<div class="col-sm-2">
						<input type="text" class="form-control" value="${endTime}" name="endTime" readonly
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
					</div>
					<div class="col-sm-3">
						<c:if test="${type=='NOTICE' }">
							<div class="btn-group" role="group" aria-label="...">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"> 
									<span id="typeButton">通知类</span><span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li><a href="#" id="typeChange">营销类</a></li>
								</ul>
  								<button type="button" class="btn btn-default" id="search">搜索</button>
							</div>
						</c:if>
						<c:if test="${type=='SALE' }">
							<div class="btn-group" role="group" aria-label="...">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"> 
									<span id="typeButton">营销类</span><span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li><a href="#" id="typeChange">通知类</a></li>
								</ul>
  								<button type="button" class="btn btn-default" id="search">搜索</button>
							</div>
						</c:if>
					</div>
				</div>
				<input type="hidden" value="${type}" id="typeInput" />
				<!-- 通道行 -->
				<div class="form-group">
					<label class="col-sm-2 control-label">通道</label>
					<div class="col-sm-10" id="channels">
						<c:forEach items="${allChannels}" var="channel">
							<label>
								<input type="checkbox" id="${channel}" value="${channel}">${channel}
							</label>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		
		<!-- echart图表 -->
		<div class="row">
			<div class="col-sm-12">
				<div id="chart" style="width:100%;"></div>
			</div>
		</div>
		<!-- 统计明细 -->
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-striped">
  					<thead>
		  				<th>统计时间</th>
		  				<th>发送量</th>
		  				<th>成功量</th>
		  				<th>计费条数</th>
		  				<th>成功率</th>
  					<thead>
		  			<tbody>
		  				<c:if test="${empty result}">
		  					<tr><td colspan="5" align="center">暂无数据</td></tr>
		  				</c:if>
		  				<c:forEach items="${result}" var="smsEntity">
		  					<tr>
								<td>${smsEntity.date}</td>
			  					<td>${smsEntity.number}</td>
			  					<td>${smsEntity.successNumber}</td>
			  					<td>${smsEntity.countNumber}</td>
			  					<td>${smsEntity.successRate}%</td>		  					
		  					</tr>
		  				</c:forEach>
		  			</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
</body>
</html>
