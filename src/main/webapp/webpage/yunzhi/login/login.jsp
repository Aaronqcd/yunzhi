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
  <%--<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />--%>
  <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
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
    .username-text input,
    .password-text input{
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
      float:left;
      margin-left:90px;
      height:60px;
      border:none
    }
    .err-style {
      display:none;
      height:30px;
      margin-top:350px;
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
              <%--<div style="margin-top: 30px;">
                <span class="left-text">酒店用户端</span>
              </div>--%>
            </div>
            <div class="col-md-6">
              <div class="col-md-12" style="margin-top: 120px;">
                <span class="right-text">用户登录</span>
              </div>
              <form id="loinForm" class="form-horizontal" method="post" style="height: 360px;">
                <input type="hidden" id="ReturnURL"  name="ReturnURL" value="${ReturnURL }"/>
                <div class="widget-main">
                  <div class="col-md-12">
                    <div class="col-md-12" style="margin-top: 50px;">
                      <span class="login-text">账户</span>
                    </div>
                    <div class="col-md-12 username-text">
                      <input class="login-box-text" type="text" name="userName" iscookie="true" placeholder="请输入账户" id="userName"/>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <div class="col-md-12" style="margin-top: 20px;">
                      <span class="login-text">密码</span>
                    </div>
                    <div class="col-md-12 password-text">
                      <input class="login-box-text" type="password" name="password" iscookie="true" placeholder="请输入密码" id="password"/>
                    </div>
                  </div>
                  <div class="alert alert-warning alert-dismissible err-style" role="alert" id="errMsgContiner">
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <div id="showErrMsg" style="height: 30px;"></div>
                  </div>
                  <div class="col-md-12" id="btn-div" style="margin-top: 30px;">
                    <button type="button" id="but_login"  onclick="checkUser()" class="width-35 btn btn-sm btn-primary btn-style">
                      <span class="btn-text">立即登录</span>
                    </button>
                  </div>
                  <div class="col-md-12" style="margin-top: 20px;">
                    <div style="float: left;padding-left: 90px;">
                      <input type="checkbox" class="ace" id="on_off" name="remember" value="yes"/>
                      <span class="lbl" style="font-size:14px">记住密码</span>
                    </div>
                  </div>
                  <div class="col-md-12" style="margin-top: 20px;">
                    <div style="float: right;padding-right: 50px;">
                      <a style="font-size:14px;color:#2778ff" href="loginController.do?register">立即注册></a>
                    </div>
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
<%=lhgdialogTheme %>
<script type="text/javascript">
	$(function(){
		optErrMsg();
	});
	$("#errMsgContiner").hide();

   //输入验证码，回车登录
  $(document).bind('keyup', function(event) {
	　　if (event.keyCode == "13") {
	　　　　$('#but_login').click();
	　　}
  });

  //验证用户信息
  function checkUser(){
    if(!validForm()){
      return false;
    }
    newLogin();
  }
  
  /**
   * 刷新验证码
   */
  $('#randCodeImage').click(function(){
	    reloadRandCodeImage();
  });
	
</script>

<script>
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "https://hm.baidu.com/hm.js?098e6e84ab585bf0c2e6853604192b8b";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
</script>

</body>
</html>