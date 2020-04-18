<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.jeecgframework.core.util.SysThemesUtil,org.jeecgframework.core.enums.SysThemesEnum"%>
<%@include file="/context/mytags.jsp"%>
<%
  session.setAttribute("lang","zh-cn");
  SysThemesEnum sysTheme = SysThemesUtil.getSysTheme(request);
  String lhgdialogTheme = SysThemesUtil.getLhgdialogTheme(sysTheme);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <meta charset="utf-8" />
  <title>酒店设备续费管理平台</title>
   <link rel="shortcut icon" href="images/favicon.ico">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
  <t:base type="bootstrap,bootstrap-table,layer,validform,bootstrap-form"></t:base>
  <!-- bootstrap & fontawesome -->
  <link rel="stylesheet" href="plug-in/ace/css/bootstrap.css" />
  <link rel="stylesheet" href="plug-in/ace/css/font-awesome.css" />
  <link rel="stylesheet" type="text/css" href="plug-in/accordion/css/accordion.css">
  <!-- text fonts -->
  <link rel="stylesheet" href="plug-in/ace/css/ace-fonts.css" />

  <link rel="stylesheet" href="plug-in/ace/css/jquery-ui.css" />
  <!-- ace styles -->
  <link rel="stylesheet" href="plug-in/ace/css/ace.css" class="ace-main-stylesheet" id="main-ace-style" />
  <!--[if lte IE 9]>
  <link rel="stylesheet" href="plug-in/ace/css/ace-part2.css" class="ace-main-stylesheet" />
  <![endif]-->

  <!--[if lte IE 9]>
  <link rel="stylesheet" href="plug-in/ace/css/ace-ie.css" />
  <![endif]-->
  <!-- ace settings handler -->
  <script src="plug-in/ace/js/ace-extra.js"></script>

  <!--[if lte IE 8]>
  <script src="plug-in/ace/js/html5shiv.js"></script>
  <script src="plug-in/ace/js/respond.js"></script>
  <![endif]-->
  <style>
    .light-login {
      background: linear-gradient(-45.762deg,rgba(53,35,223,1) 0%,rgba(154,117,255,1) 100%);
    }
    .backimg {
      background: url(plug-in/login/images/backimg.png);
      width: 1200px;
      height: 641px;
      margin: 0 auto;
    }
    .left-text {
      font-size: 30px;
      color: #fff;
    }
    .btn-text {
      font-size: 25px;
      color: #fff;
    }
    .right-text {
      font-size: 25px;
      color: grey;
      float: left;
      padding-left: 100px;
    }
    .login-text {
      font-size: 15px;
      color: grey;
      float: left;
      padding-left: 78px;
    }
    .login-box-text {
      font-size: 15px;
      color: grey;
      float: left;
      padding-left: 100px;
    }
    .username-text{
      border-bottom:solid 2px #ccc;
      width:400px;
      height:40px;
      margin-left: 90px;
      background:url(plug-in/login/images/username.png) no-repeat 10px center;
    }
    .password-text{
      border-bottom:solid 2px #ccc;
      width:400px;
      height:40px;
      margin-left: 90px;
      background:url(plug-in/login/images/password.png) no-repeat 10px center;
    }
    .mobile-text{
      border-bottom:solid 2px #ccc;
      width:400px;
      height:40px;
      margin-left: 90px;
      background:url(plug-in/login/images/password.png) no-repeat 10px center;
    }
    .username-text input,
    .password-text input,
    .mobile-text input{
      float:left;
      border:none;
      background:none;
      height:40px;
      line-height:30px;
      width:100%;
      text-indent:32px;
    }
    input[type="text"]:focus {
      background-color: transparent;
    }
    .btn-primary, .btn-primary:focus {
      background-color: #2778ff !important;
    }
    input{
      animation: resetBg .1s forwards;
    }
    @keyframes resetBg {
      to {
        color: grey;
        background: transparent;
      }
    }
    .alert-dismissable .close, .alert-dismissible .close {
      position: relative;
      top: 4px;
      right: 10px;
      color: inherit;
    }
    .btn-style {
      float:right;
      margin-right:50px;
      height:60px;
      border:none;
      /*width: 50% !important;*/
    }
    .err-style {
      display:none;
      height:30px;
      margin-top:460px;
      line-height:30px;
      padding:0;
      width:200px;
      margin-left:200px;
    }
    input[type=checkbox].ace + .lbl::before, input[type=radio].ace + .lbl::before {
      margin-right: 5px;
    }
  </style>
</head>
<body class="login-layout light-login">
<div class="main-container">
  <div class="main-content">
    <div class="row">
      <div class="">
        <div class="" style="padding-top:50px">
          <div class="center backimg">
            <div class="col-md-6">
              <div style="margin-top: 200px;">
                <span class="left-text">云智省酒店资源综合管理平台</span>
              </div>
            </div>
            <div class="col-md-6">
              <div class="col-md-12" style="margin-top: 40px;">
                <span class="right-text">欢迎注册</span>
              </div>
              <form id="registerForm" class="form-horizontal" method="post" style="height: 360px;">
                <input type="hidden" name="devFlag" id="devFlag" value="0" />
                <input type="hidden" name="personType" id="personType" value="2" />
                <input type="hidden" name="status" id="status" value="0" />
                <div class="widget-main">
                  <div class="col-md-12">
                    <div class="col-md-12" style="margin-top: 30px;">
                      <span class="login-text">账户</span>
                    </div>
                    <div class="col-md-12 username-text">
                      <input class="login-box-text" type="text" name="userName" placeholder="请输入账户" id="userName" datatype="*"/>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <div class="col-md-12" style="margin-top: 20px;">
                      <span class="login-text">密码</span>
                    </div>
                    <div class="col-md-12 password-text">
                      <input class="login-box-text" type="password" name="password" placeholder="请输入密码" id="password" datatype="*"/>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <div class="col-md-12" style="margin-top: 20px;">
                      <span class="login-text">确认密码</span>
                    </div>
                    <div class="col-md-12 password-text">
                      <input class="login-box-text" type="password" name="confirmPwd" placeholder="请输入确认密码" id="confirmPwd" datatype="*"/>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <div class="col-md-12" style="margin-top: 20px;">
                      <span class="login-text">手机号</span>
                    </div>
                    <div class="col-md-12 mobile-text">
                      <input class="login-box-text" type="text" name="mobilePhone" placeholder="请输入手机号" id="mobilePhone" datatype="*"/>
                    </div>
                  </div>
                  <div class="col-md-12" style="margin-top: 20px;">
                    <div class="col-md-6" style="padding-left: 80px;">
                      <select name="role" id="role">
                        <option value="" disabled selected hidden>注册身份</option>
                        <option value ="1">城市合伙人 - 负责人</option>
                        <option value ="2">城市合伙人 - 主管</option>
                        <option value="3">城市合伙人 - 员工</option>
                      </select>
                    </div>
                    <div class="col-md-6">
                      <select name="province" id="province">
                        <option value="" disabled selected hidden>选择省份</option>
                      </select>
                    </div>
                  </div>
                  <div class="alert alert-warning alert-dismissible err-style" role="alert" id="errMsgContiner">
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <div id="showErrMsg" style="height: 30px;"></div>
                  </div>
                  <div class="col-md-12" id="btn-div" style="margin-top: 30px;">
                    <button type="button" id="but_login"  onclick="checkUser()" class="width-35 btn btn-sm btn-primary btn-style">
                      <span class="btn-text">确定</span>
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
            </div>
          </div>
        </div>
      </div>
    </div>

<script type="text/javascript" src="plug-in/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="plug-in/jquery/jquery.cookie.js"></script>
<script type="text/javascript" src="plug-in/mutiLang/en.js"></script>
<script type="text/javascript" src="plug-in/mutiLang/zh-cn.js"></script>
<script type="text/javascript" src="plug-in/login/js/jquery.tipsy.js"></script>
<script type="text/javascript" src="plug-in/login/js/iphone.check.js"></script>
<script type="text/javascript" src="webpage/login/login-ace.js"></script>
<script src="plug-in/jquery/jquery.regionselect.js" type="text/javascript"></script>
<%=lhgdialogTheme %>
<script type="text/javascript">
	$(function(){
		optErrMsg();
        $.ajax({
          type: "get",
          url: "jeecgFormDemoController.do?regionSelect",
          dataType:"json",
          data: {
            pid: 1
          },
          success: function(data){
            console.log(data);
            for(var i=0; i<data.length; i++) {
              $("#province").append("<option value='"+data[i].name+"'>"+data[i].name+"</option>");
            }
          }
        });
      $("#confirmPwd").blur(function() {
        var password = $("#password").val();
        var confirmPwd = $("#confirmPwd").val();
        if(password != confirmPwd) {
          showErrorMsg("两次密码不匹配");
          return false;
        }
        debugger;
      });
    });
	$("#errMsgContiner").hide();
    //表单验证
    function validRegisterForm(){
      if($.trim($("#userName").val()).length==0){
        showErrorMsg("请输入用户名");
        return false;
      }

      if($.trim($("#password").val()).length==0){
        showErrorMsg("请输入密码");
        return false;
      }
      if($.trim($("#confirmPwd").val()).length==0){
        showErrorMsg("请输入确认密码");
        return false;
      }
      if($.trim($("#mobilePhone").val()).length==0){
        showErrorMsg("请输入手机号");
        return false;
      }
      if($.trim($("#role").val()).length==0){
        showErrorMsg("请选择注册身份");
        return false;
      }
      if($.trim($("#province").val()).length==0){
        showErrorMsg("请选择省份");
        return false;
      }
      return true;
    }

   //输入验证码，回车登录
  $(document).bind('keyup', function(event) {
	　　if (event.keyCode == "13") {
	　　　　$('#but_login').click();
	　　}
  });

  //验证用户信息
  function checkUser(){
    if(!validRegisterForm()){
      return false;
    }
    register();
  }

  function register() {
    var actionurl = "loginController.do?doRegister";//提交路径
    var loginurl = "loginController.do?login";//提交路径
    // var checkurl="loginController.do?checkuser";//验证路径
    var formData = new Object();
    var data=$(":input").each(function() {
      formData[this.name] =$("#"+this.name ).val();
    });
    formData['role'] = $("#role").val();
    formData['province'] = $("#province").val();
    //语言
    // formData['langCode']=$("#langCode").val();
    // formData['langCode'] = $("#langCode option:selected").val();
    $.ajax({
      async : false,
      cache : false,
      type : 'POST',
      url : actionurl,// 请求的action路径
      data : formData,
      error : function() {// 请求失败处理函数
      },
      success : function(data) {
        alert("注册资料提交成功，待审核通过后可进行登录操作！");
        var d = $.parseJSON(data);
        if (d.success) {
          window.location.href = loginurl;
        }
        /*var d = $.parseJSON(data);
        if (d.success) {
          if (d.attributes.orgNum > 1) {
            //用户拥有多个部门，需选择部门进行登录
            var title, okButton;
            if($("#langCode").val() == 'en') {
              title = "Please select Org";
              okButton = "Ok";
            } else {
              title = "请选择组织机构";
              okButton = "确定";
            }
            $.dialog({
              id: 'LHG1976D',
              title: title,
              max: false,
              min: false,
              drag: false,
              resize: false,
              content: 'url:userController.do?userOrgSelect&userId=' + d.attributes.user.id,
              lock:true,
              button : [ {
                name : okButton,
                focus : true,
                callback : function() {
                  iframe = this.iframe.contentWindow;
                  var orgId = $('#orgId', iframe.document).val();
                  //----------------------------------------------------
                  //变更采用ajax方式提高效率
                  formData['orgId'] = orgId ? orgId : "";
                  $.ajax({
                    async : false,
                    cache : false,
                    type : 'POST',
                    url : 'loginController.do?changeDefaultOrg',// 请求的action路径
                    data : formData,
                    error : function() {// 请求失败处理函数
                    },
                    success : function(data) {
                      window.location.href = actionurl;
                    }
                  });
                  //----------------------------------------------------
                  this.close();
                  return false;
                }
              }],
              close: function(){
                setTimeout("window.location.href='"+actionurl+"'", 10);
              }
            });
          } else {
            window.location.href = actionurl;
          }
        } else {
          showErrorMsg(d.msg);
        }*/
      }
    });
  }
  
  /**
   * 刷新验证码
   */
  $('#randCodeImage').click(function(){
	    reloadRandCodeImage();
  });
	
</script>
</body>
</html>