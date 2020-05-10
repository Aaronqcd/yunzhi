<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>客户表</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1">
<t:base type="bootstrap,bootstrap-table,layer,validform,webuploader,bootstrap-form"></t:base>
</head>
 <body style="overflow:hidden;overflow-y:auto;">
 <div class="container" style="width:100%;">
	<div class="panel-heading"></div>
	<div class="panel-body">
	<form class="form-horizontal" role="form" id="formobj" action="clientController.do?doUpdate" method="POST">
		<input type="hidden" id="btn_sub" class="btn_sub"/>
		<input type="hidden" id="id" name="id" value="${client.id}"/>
	<div class="form-group">
		<label for="hotelInfo" class="col-sm-3 control-label">酒店信息：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="hotelInfo" name="hotelInfo" value='${client.hotelInfo}' type="text" maxlength="100" class="form-control input-sm" placeholder="请输入酒店信息"  ignore="ignore" />
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="corpInfo" class="col-sm-3 control-label">法人信息：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="corpInfo" name="corpInfo" value='${client.corpInfo}' type="text" maxlength="50" class="form-control input-sm" placeholder="请输入法人信息"  ignore="ignore" />
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="license" class="col-sm-3 control-label">营业执照：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<t:webUploader name="license" outJs="true" auto="true" showImgDiv="filediv_license" pathValues="${client.license}"></t:webUploader>
				<div class="form" id="filediv_license"></div>
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="address" class="col-sm-3 control-label">酒店地址：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="address" name="address" value='${client.address}' type="text" maxlength="255" class="form-control input-sm" placeholder="请输入酒店地址"  ignore="ignore" />
			</div>
		</div>
	</div>
	<%--<div class="form-group">
		<label for="state" class="col-sm-3 control-label">状态：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="state" name="state" value='${client.state}' type="text" maxlength="1" class="form-control input-sm" placeholder="请输入状态"  ignore="ignore" />
			</div>
		</div>
	</div>--%>
	<div class="form-group">
		<label for="contact" class="col-sm-3 control-label">联系方式：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="contact" name="contact" value='${client.contact}' type="text" maxlength="20" class="form-control input-sm" placeholder="请输入联系方式"  ignore="ignore" />
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="contact" class="col-sm-3 control-label">应收服务费：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="receivable" name="receivable" value='${client.receivable}' type="text" maxlength="20" class="form-control input-sm" placeholder="请输入应收服务费" datatype="d" ignore="checked" />
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="contact" class="col-sm-3 control-label">应收营运保证金：</label>
		<div class="col-sm-7">
			<div class="input-group" style="width:100%">
				<input id="deposit" name="deposit" value='${client.deposit}' type="text" maxlength="20" class="form-control input-sm" placeholder="请输入应收营运保证金" datatype="d" ignore="checked" />
			</div>
		</div>
	</div>
	</form>
	</div>
 </div>
<script type="text/javascript">
var subDlgIndex = '';
$(document).ready(function() {
	//单选框/多选框初始化
	$('.i-checks').iCheck({
		labelHover : false,
		cursor : true,
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green',
		increaseArea : '20%'
	});
	
	//表单提交
	$("#formobj").Validform({
		tiptype:function(msg,o,cssctl){
			if(o.type==3){
				validationMessage(o.obj,msg);
			}else{
				removeMessage(o.obj);
			}
		},
		btnSubmit : "#btn_sub",
		btnReset : "#btn_reset",
		ajaxPost : true,
		beforeSubmit : function(curform) {
		},
		usePlugin : {
			passwordstrength : {
				minLen : 6,
				maxLen : 18,
				trigger : function(obj, error) {
					if (error) {
						obj.parent().next().find(".Validform_checktip").show();
						obj.find(".passwordStrength").hide();
					} else {
						$(".passwordStrength").show();
						obj.parent().next().find(".Validform_checktip").hide();
					}
				}
			}
		},
		callback : function(data) {
			var win = frameElement.api.opener;
			if (data.success == true) {
				frameElement.api.close();
			    win.reloadTable();
			    win.tip(data.msg);
			} else {
			    if (data.responseText == '' || data.responseText == undefined) {
			        $.messager.alert('错误', data.msg);
			        $.Hidemsg();
			    } else {
			        try {
			            var emsg = data.responseText.substring(data.responseText.indexOf('错误描述'), data.responseText.indexOf('错误信息'));
			            $.messager.alert('错误', emsg);
			            $.Hidemsg();
			        } catch (ex) {
			            $.messager.alert('错误', data.responseText + "");
			            $.Hidemsg();
			        }
			    }
			    return false;
			}
		}
	});
});
</script>
</body>
</html>