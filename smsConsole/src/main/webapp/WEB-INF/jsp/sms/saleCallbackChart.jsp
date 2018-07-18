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
  			option = {
  				    title : {
  				        text: '营销短信通达统计',
  				        subtext:[],
  				        x:'center'
  				    },
  				    tooltip : {
  				        trigger: 'item',
  				        formatter: "{a} <br/>{b} : {c} 条({d}%)"
  				    },
  				    legend: {
  				        orient: 'vertical',
  				        left: 'left',
  				      	data: [],
  				    },
  				    series : [
  				        {
  				            name: '状态码',
  				            type: 'pie',
  				            radius : '40%',
  				            center: ['50%', '40%'],
  				            data:[
  				            ],
  				            itemStyle: {
  				                emphasis: {
  				                    shadowBlur: 10,
  				                    shadowOffsetX: 0,
  				                  	shadowColor: 'rgba(0, 0, 0, 0.5)'
  				                }
  				            }
  				        }
  				    ]
  				};
  			var height = $(document).height()*3/4;
  			$("#chart").css({"height" : height});
  			//基于准备好的dom，初始化echarts实例
  			var myChart = echarts.init(document.getElementById('chart'),'macarons');
  			//初始化chart
  			getSaleCallbackChartData("${startTime}","${endTime}","");
  			myChart.setOption(option);
  			$("#search").click(function(){
  				var startTime = $("input[name='startTime']").val();
  				var endTime = $("input[name='endTime']").val();
  				var content = $("[name='content']").val();
  				getSaleCallbackChartData(startTime, endTime, content);
  			});
  			
  			function getSaleCallbackChartData(startTime, endTime, content){
  				$.ajax({
  					url:"${pageContext.request.contextPath}/sms/getSaleCallbackChartData.action?"
  							+"startTime="+encodeURIComponent(encodeURIComponent(startTime))
  							+"&endTime="+encodeURIComponent(encodeURIComponent(endTime))
  							+"&content="+encodeURIComponent(encodeURIComponent(content)),
  					type:'post',
  					dataType:'json',
  					beforeSend:function(XMLHttpRequest){
  						$("#noData").hide();
  						myChart.showLoading();
  					},
  					success:function(data,textStatus){
  						//有数据
  						if(data.length>0){
  							$("#chart").show();
	  						myChart.setOption({
	  							title : {
	  		  				        subtext:getCountDetails(data)
	  		  				    },
	  							legend : {
	  								data : data
	  							},
	  							series : {
	  								data : data
	  							}
	  						});
  						}else{
  							$("#chart").hide();
  							$("#noData").show();
  						}
  						myChart.hideLoading();
  					}
  				});
  			}
  			
  			function getCountDetails(data){
  				var successCount = 0;
  				var failCount = 0;
  				var count = data.pop().value;//移除最后一个元素
  				$.each(data,function(index,name){
					if(data[index].name=='DELIVRD'){
						successCount+=Number(data[index].value);		
					}else{
						failCount+=Number(data[index].value);
					}
				});
  				return '成功:'+successCount+'/失败:'+failCount+'/已发送:'+count;
  			}
  		})
  	</script>
</head>

<body>
	<div class="container">
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
		
		<div align="center">
			<h4>营销短信送达分析</h4>
		</div>
		</br>
		<div class="row">
			<div class="col-sm-9 col-sm-offset-2">
				<div class="alert alert-info" role="alert">
					<strong>提示：</strong>送达分析有延迟，若成功加失败数小于总数，表示还有数据正在拉取，请稍候查询。
				</div>
			</div>
		</div>
		<div class="row">
			<div class="form-horizontal">
				<div class="form-group">
					<label class="col-sm-2 control-label">开始时间</label>
					<div class="col-sm-3">
						<input type="text" class="form-control" value="${startTime}" name="startTime" readonly
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
					</div>
					<label class="col-sm-2  control-label">结束时间</label>
					<div class="col-sm-3">
						<input type="text" class="form-control" value="${endTime}" name="endTime" readonly
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',
											readOnly:true,isShowClear:false,isShowToday:true,isShowOK:false})">
					</div>
					<div class="col-sm-1">
						<button type="submit" class="btn btn-default" id="search">搜索</button>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">内容</label>
					<div class="col-sm-9">
						<textarea rows="7" class="form-control" name="content"></textarea>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-9 col-sm-offset-2">
				<div id="chart" style="width:100%;"></div>
				<div id="noData" class="panel panel-default" style="width:100%;dispaly:none;">
					<div class="panel-body" align="center">暂无数据</div>
				</div>
			</div>
		</div>
		
	</div>
</body>
</html>
