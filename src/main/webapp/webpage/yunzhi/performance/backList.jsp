<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>回款率统计</title>
<t:base type="bootstrap,bootstrap-table,layer"></t:base>
<link rel="stylesheet" href="${webRoot}/plug-in/themes/naturebt/css/search-form.css">
</head>
<body>
	<div class="panel-body" style="padding-top: 0px; padding-bottom: 0px;">
		<!-- toolbar -->
		<div id="toolbar">
			<span style="font-size: 20px">回款情况</span>
		</div>
		<!-- data table -->
		<div class="table-responsive">
			<!-- class="text-nowrap" 强制不换行 -->
			<table id="clientList"></table>
		</div>
	</div>
	<script type="text/javascript">
		$(".laydate-datetime").each(function(){
			var _this = this;
			laydate.render({
			  elem: this,
			  format: 'yyyy-MM-dd HH:mm:ss',
			  type: 'datetime'
			});
		});
		$(".laydate-date").each(function(){
			var _this = this;
			laydate.render({
			  elem: this
			});
		});
		var clientListdictsData = {};
		$(function() {
			var promiseArr = [];
			
			Promise.all(promiseArr).then(function(results) {
			    
			}).catch(function(err) {
			 	console.log('Catch: ', err);
			});
			
			//1.初始化Table
			var oTable = new TableInit();
			oTable.Init();

			//判断是否选中表格中的数据，选中后可编辑或删除
			$('#clientList').on(
					'check.bs.table uncheck.bs.table load-success.bs.table '
							+ 'check-all.bs.table uncheck-all.bs.table',
					function() {
						$('#btn_delete').prop('disabled',!$('#clientList').bootstrapTable('getSelections').length);
						$('#btn_edit').prop('disabled',$('#clientList').bootstrapTable('getSelections').length != 1);
					});

		});
		
		var TableInit = function() {
			var oTableInit = new Object();
			//初始化Table
			oTableInit.Init = function() {
				$('#clientList').bootstrapTable({
									url : 'performanceController.do?backDatagrid', //请求后台的URL（*）
									method : 'get', //请求方式（*）
									toolbar : '#toolbar', //工具按钮用哪个容器
									striped : true, //是否显示行间隔色
									cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
									pagination : true, //是否显示分页（*）
									queryParams : oTableInit.queryParams,//传递参数（*）
									sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
									pageNumber : 1, //初始化加载第一页，默认第一页
									pageSize : 10, //每页的记录行数（*）
									pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
									strictSearch : true,
									// showColumns : true, //是否显示所有的列
									showRefresh : true, //是否显示刷新按钮
									minimumCountColumns : 2, //最少允许的列数
									clickToSelect : true, //是否启用点击选中行
									height : $(window).height() - 35, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
									uniqueId : "id", //每一行的唯一标识，一般为主键列
									// showToggle : true, //是否显示详细视图和列表视图的切换按钮
									cardView : false, //是否显示详细视图
									detailView : false, //是否显示父子表
									showExport : true, //显示到处按钮
									sortName:'createDate',
									sortOrder:'desc',
									columns : [
											{
												title : '序号',
												width : 5,
												align : 'center',
												switchable : false,
												formatter : function(value,row, index) {
													//return index+1; //序号正序排序从1开始
													var pageSize = $('#clientList')
															.bootstrapTable('getOptions').pageSize;
													var pageNumber = $('#clientList').bootstrapTable('getOptions').pageNumber;
													return pageSize* (pageNumber - 1) + index + 1;
												}
											},
											{
												field : 'hotel_info',
												title : '酒店名称',
												valign : 'middle',
												width : 120,
												align : 'center',
												switchable : true,
											},
											{
												field : 'receivable',
												title : '服务费',
												valign : 'middle',
												width : 120,
												align : 'center',
												switchable : true,
											},
											{
												field : 'deposit',
												title : '保证金',
												valign : 'middle',
												width : 120,
												align : 'center',
												switchable : true,
											},
											{
												field : 'amount',
												title : '回款率',
												valign : 'middle',
												width : 120,
												align : 'center',
												switchable : true,
												formatter:function(value,rec,index){
													return setAmountText(value,rec,index);
											 	}
											}],
									onLoadSuccess : function() { //加载成功时执行
										console.info("加载成功");
									},
									onLoadError : function() { //加载失败时执行
										console.info("加载数据失败");
									}
								});
			};

			//得到查询的参数
			oTableInit.queryParams = function(params) {
				var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
					pageSize : params.limit, // 每页要显示的数据条数
					offset : params.offset, // 每页显示数据的开始行号
					sort : params.sort, // 排序规则
					order : params.order,
					rows : params.limit, //页面大小
					page : (params.offset / params.limit) + 1, //页码
					pageIndex : params.pageNumber,//请求第几页
					field : 'hotel_info,receivable,deposit,amount'
				};

				var params = $("#searchForm").serializeArray();
				for (x in params) {
					temp[params[x].name] = params[x].value;
				}
				return temp;
			};
			return oTableInit;
		};
		
		function searchList() {
			reloadTable();
		}

		function reloadTable() {
			$('#clientList').bootstrapTable('refresh');
		}

		function setAmountText(value,rec,index) {
			var rate = parseFloat(value)/parseFloat(rec.deposit+rec.receivable);
			rate = (Math.floor(rate * 100)/100)*100;
			if(rate != 0) {
				rate += "%";
			}
			return rate;
		}

	</script>
</body>
</html>
<t:authFilter/>