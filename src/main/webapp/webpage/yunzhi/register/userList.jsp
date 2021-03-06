<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<t:datagrid name="roleUserList" title="common.operation"
			actionUrl="accountController.do?userAuditDatagrid&roleId=${roleId}" fit="true" fitColumns="true" idField="id" queryMode="group">
	<t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
	<t:dgCol title="common.username" sortable="false" field="userName" width="100" query="true"></t:dgCol>
	<t:dgCol title="common.real.name" field="realName" width="100" query="true"></t:dgCol>
	<t:dgCol title="common.user.type" field="userType" dictionary="user_type" width="80"></t:dgCol>
	<t:dgCol title="common.department" sortable="false" field="userOrgList.tsDepart.departname" query="false" width="100"></t:dgCol>
	<t:dgCol title="common.role" field="userKey" width="100"></t:dgCol>
	<t:dgCol title="common.createby" field="createBy" hidden="true" width="100"></t:dgCol>
	<t:dgCol title="common.createtime" field="createDate" formatter="yyyy-MM-dd"  width="100" hidden="false"></t:dgCol>
	<t:dgCol title="common.updateby" field="updateBy" hidden="true"></t:dgCol>
	<t:dgCol title="common.updatetime" field="updateDate" formatter="yyyy-MM-dd" hidden="true"></t:dgCol>
	<t:dgCol title="common.status" sortable="true" width="100" field="status" replace="common.active_1,common.inactive_0,super.admin_-1"></t:dgCol>
	<t:dgCol title="common.operation" field="opt" width="200" ></t:dgCol>
	<t:dgDelOpt title="common.delete" url="roleController.do?forceDelUserRole&userid={id}&roleid=${roleId }" urlclass="ace_button"  urlfont="fa-trash-o"/>
	<%--<t:dgToolBar title="common.add.param" langArg="common.user" icon="icon-add" url="userController.do?addorupdatePage&roleId=${roleId}" funname="add"></t:dgToolBar>--%>
	<t:dgToolBar title="common.edit.param" langArg="common.user" icon="icon-edit" url="userController.do?addorupdatePage&roleId=${roleId}" funname="update"></t:dgToolBar>
	<t:dgToolBar title="审核通过" icon="icon-edit" url="userController.do?lock&lockvalue=1" funname="unlockObj"></t:dgToolBar>
	<%--<t:dgToolBar title="common.add.exist.user" icon="icon-add" url="roleController.do?goAddUserToRole&roleId=${roleId}" funname="add" width="600"></t:dgToolBar>--%>
</t:datagrid>
<script>
	$(function() {
		var datagrid = $("#userListtb");
		datagrid.find("div[name='searchColums']").find("form#userListForm").append($("#realNameSearchColums div[name='searchColumsRealName']").html());
		$("#realNameSearchColums").html('');
		datagrid.find("div[name='searchColums']").find("form#userListForm").append($("#tempSearchColums div[name='searchColums']").html());
		$("#tempSearchColums").html('');
	});
</script>
<div id="realNameSearchColums" style="display: none;">
	<div name="searchColumsRealName">
		<t:userSelect hasLabel="true" selectedNamesInputId="realName" windowWidth="1000px" windowHeight="600px" title="用户名称"></t:userSelect>
	</div>
</div>
<div id="tempSearchColums" style="display: none;">
	<div name="searchColums">
		<t:departSelect hasLabel="true" selectedNamesInputId="orgNames"></t:departSelect>
	</div>
</div>
<script type="text/javascript">
	function deleteDialog(id){
		var url = "userController.do?deleteDialog&id=" + id
		createwindow("删除模式", url, 200, 100);
	}
	function lockObj(title,url, id) {

		gridname=id;
		var rowsData = $('#'+id).datagrid('getSelections');
		if (!rowsData || rowsData.length==0) {
			tip('<t:mutiLang langKey="common.please.select.edit.item"/>');
			return;
		}
		url += '&id='+rowsData[0].id;

		$.dialog.confirm('<t:mutiLang langKey="common.lock.user.tips"/>', function(){
			lockuploadify(url, '&id');
		}, function(){
		});
	}
	function unlockObj(title,url, id) {
		gridname=id;
		var rowsData = $('#'+id).datagrid('getSelections');
		if (!rowsData || rowsData.length==0) {
			tip('<t:mutiLang langKey="common.please.select.edit.item"/>');
			return;
		}

		if(rowsData[0].status == 1){
			tip('<t:mutiLang langKey="common.please.select.user.status.inactive"/>');
			return;
		}

		url += '&id='+rowsData[0].id;

		$.dialog.confirm('<t:mutiLang langKey="common.unlock.user.tips"/>', function(){
			lockuploadify(url, '&id');
		}, function(){
		});
	}


	function lockuploadify(url, id) {
		$.ajax({
			async : false,
			cache : false,
			type : 'POST',
			url : url,// 请求的action路径
			error : function() {// 请求失败处理函数

			},
			success : function(data) {
				var d = $.parseJSON(data);
				if (d.success) {
					var msg = d.msg;
					tip(msg);
					reloadTable();
				}
			}
		});
	}
</script>

<script type="text/javascript">
	//导入
	function ImportXls() {
		openuploadwin('Excel导入', 'userController.do?upload', "userList");
	}

	//导出
	function ExportXls() {
		JeecgExcelExport("userController.do?exportXls", "userList");
	}

	//模板下载
	function ExportXlsByT() {
		JeecgExcelExport("userController.do?exportXlsByT", "userList");
	}
</script>