package com.yunzhi.controller;
import com.yunzhi.entity.ClientEntity;
import com.yunzhi.entity.UserClientEntity;
import com.yunzhi.service.ClientServiceI;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yunzhi.service.UserClientServiceI;
import org.jeecgframework.core.util.*;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSRoleUser;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;

import java.io.OutputStream;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.jeecgframework.core.beanvalidator.BeanValidators;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.jwt.util.ResponseMessage;
import org.jeecgframework.jwt.util.Result;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**   
 * @Title: Controller  
 * @Description: 客户表
 * @author onlineGenerator
 * @date 2020-04-08 18:05:20
 * @version V1.0   
 *
 */
@Api(value="Client",description="客户表",tags="clientController")
@Controller
@RequestMapping("/clientController")
public class ClientController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	private SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private ClientServiceI clientService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	@Autowired
	private UserClientServiceI userClientService;
	
	/**
	 * 客户表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("yunzhi/client/clientList");
	}

	/**
	 * 今日新增客户
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "todayList")
	public ModelAndView todayList(HttpServletRequest request) {
		return new ModelAndView("yunzhi/client/todayList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "datagrid")
	public void datagrid(ClientEntity client,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(ClientEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, client, request.getParameterMap());
		TSUser tsUser = ResourceUtil.getSessionUser();
		List<TSRoleUser> rUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", tsUser.getId());
		TSRole role = rUser.get(0).getTSRole();
		String roleCode = role.getRoleCode();
		String departId = null;
		String sql;
		Map<String, Object> map;
		List<TSRoleUser> roleUser = null;
		//单独处理主管角色
		if("charge".equals(roleCode)) {
			sql = "select d.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.tier='3' and uo.user_id=?";
			map = systemService.findOneForJdbc(sql, tsUser.getId());
			if(map == null) {
				sql = "select d.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.tier='2' and uo.user_id=?";
				map = systemService.findOneForJdbc(sql, tsUser.getId());
			}
			departId = (String) map.get("id");
			TSRole tsRole = systemService.findUniqueByProperty(TSRole.class, "roleCode", "staff");
			roleUser = systemService.findByProperty(TSRoleUser.class, "TSRole.id", tsRole.getId());
		}
		String userId = null;
		String[] staffes = null;
		//单独处理主管角色
		if("charge".equals(roleCode)) {
			if (roleUser.size() > 0) {
				staffes = new String[roleUser.size()];
				for(int i = 0; i < roleUser.size(); i++){
					userId = roleUser.get(i).getTSUser().getId();
					sql = "select uo.id,bu.username from t_s_user_org uo join t_s_base_user bu on uo.user_id=bu.id join t_s_depart d on uo.org_id=d.id " +
							"where d.id=? and uo.user_id=?";
					map = systemService.findOneForJdbc(sql, departId, userId);
					if(map != null) {
						staffes[i] = (String) map.get("username");
					}
				}
			}else {
			}
		}
		//单独处理员工角色
		else if("staff".equals(roleCode)) {
			staffes = new String[1];
			staffes[0] = tsUser.getUserName();
		}
		try{
			//自定义追加查询条件
			if(staffes != null) {
				cq.in("createBy", staffes);
			}
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.clientService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 今日新增客户
	 * @param client
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "todayDatagrid")
	public void todayDatagrid(ClientEntity client,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(ClientEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, client, request.getParameterMap());
		Date nowDate = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(nowDate);
		calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
		Date tomorrowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
		String tomorrow = datetimeFormat.format(tomorrowDate);

		TSUser tsUser = ResourceUtil.getSessionUser();
		List<TSRoleUser> rUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", tsUser.getId());
		TSRole role = rUser.get(0).getTSRole();
		String roleCode = role.getRoleCode();
		String departId = null;
		String sql;
		Map<String, Object> map;
		List<TSRoleUser> roleUser = null;
		//单独处理主管角色
		if("charge".equals(roleCode)) {
			sql = "select d.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.tier='3' and uo.user_id=?";
			map = systemService.findOneForJdbc(sql, tsUser.getId());
			if(map == null) {
				sql = "select d.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.tier='2' and uo.user_id=?";
				map = systemService.findOneForJdbc(sql, tsUser.getId());
			}
			departId = (String) map.get("id");
			TSRole tsRole = systemService.findUniqueByProperty(TSRole.class, "roleCode", "staff");
			roleUser = systemService.findByProperty(TSRoleUser.class, "TSRole.id", tsRole.getId());
		}
		String userId = null;
		String[] staffes = null;
		//单独处理主管角色
		if("charge".equals(roleCode)) {
			if (roleUser.size() > 0) {
				staffes = new String[roleUser.size()];
				for(int i = 0; i < roleUser.size(); i++){
					userId = roleUser.get(i).getTSUser().getId();
					sql = "select uo.id,bu.username from t_s_user_org uo join t_s_base_user bu on uo.user_id=bu.id join t_s_depart d on uo.org_id=d.id " +
							"where d.id=? and uo.user_id=?";
					map = systemService.findOneForJdbc(sql, departId, userId);
					if(map != null) {
						staffes[i] = (String) map.get("username");
					}
				}
			}else {
			}
		}
		//单独处理员工角色
		else if("staff".equals(roleCode)) {
			staffes = new String[1];
			staffes[0] = tsUser.getUserName();
		}

		try{
			//自定义追加查询条件
			cq.ge("createDate", datetimeFormat.parse(datetimeFormat.format(nowDate) + " 00:00:00"));
			cq.le("createDate", datetimeFormat.parse(tomorrow + " 00:00:00"));
			if(staffes != null) {
				cq.in("createBy", staffes);
			}
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.clientService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}
	
	/**
	 * 删除客户表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doDel")
	@ResponseBody
	public AjaxJson doDel(ClientEntity client, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		client = systemService.getEntity(ClientEntity.class, client.getId());
		message = "客户表删除成功";
		try{
			clientService.delete(client);
			systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "客户表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 批量删除客户表
	 * 
	 * @return
	 */
	 @RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids,HttpServletRequest request){
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "客户表删除成功";
		try{
			for(String id:ids.split(",")){
				ClientEntity client = systemService.getEntity(ClientEntity.class, 
				id
				);
				clientService.delete(client);
				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
			}
		}catch(Exception e){
			e.printStackTrace();
			message = "客户表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加客户表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doAdd")
	@ResponseBody
	public AjaxJson doAdd(ClientEntity client, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "客户表添加成功";
		TSUser user = ResourceUtil.getSessionUser();
		List<TSRoleUser> rUsers = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
		try{
			//设置回款金额为0
			client.setAmount((double) 0);
			clientService.save(client);
			//保存员工客户关联表记录
			UserClientEntity userClient = new UserClientEntity();
			userClient.setClient(client);
			userClient.setUser(user);
			//0表示是员工与客户关系
			userClient.setType("0");
			userClientService.save(userClient);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "客户表添加失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 更新客户表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doUpdate")
	@ResponseBody
	public AjaxJson doUpdate(ClientEntity client, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "客户表更新成功";
		ClientEntity t = clientService.get(ClientEntity.class, client.getId());
		try {
			MyBeanUtils.copyBeanNotNull2Bean(client, t);
			clientService.saveOrUpdate(t);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			message = "客户表更新失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	

	/**
	 * 客户表新增页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goAdd")
	public ModelAndView goAdd(ClientEntity client, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(client.getId())) {
			client = clientService.getEntity(ClientEntity.class, client.getId());
			req.setAttribute("client", client);
		}
		return new ModelAndView("yunzhi/client/client-add");
	}
	/**
	 * 客户表编辑页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(ClientEntity client, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(client.getId())) {
			client = clientService.getEntity(ClientEntity.class, client.getId());
			req.setAttribute("client", client);
		}
		return new ModelAndView("yunzhi/client/client-update");
	}
	
	/**
	 * 导入功能跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "upload")
	public ModelAndView upload(HttpServletRequest req) {
		req.setAttribute("controller_name","clientController");
		return new ModelAndView("common/upload/pub_excel_upload");
	}
	
	/**
	 * 导出excel
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXls")
	public String exportXls(ClientEntity client,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
		CriteriaQuery cq = new CriteriaQuery(ClientEntity.class, dataGrid);
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, client, request.getParameterMap());
		List<ClientEntity> clients = this.clientService.getListByCriteriaQuery(cq,false);
		modelMap.put(NormalExcelConstants.FILE_NAME,"客户表");
		modelMap.put(NormalExcelConstants.CLASS,ClientEntity.class);
		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("客户表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
			"导出信息"));
		modelMap.put(NormalExcelConstants.DATA_LIST,clients);
		return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	/**
	 * 导出excel 使模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXlsByT")
	public String exportXlsByT(ClientEntity client,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
    	modelMap.put(NormalExcelConstants.FILE_NAME,"客户表");
    	modelMap.put(NormalExcelConstants.CLASS,ClientEntity.class);
    	modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("客户表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
    	"导出信息"));
    	modelMap.put(NormalExcelConstants.DATA_LIST,new ArrayList());
    	return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "importExcel", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson importExcel(HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<ClientEntity> listClientEntitys = ExcelImportUtil.importExcel(file.getInputStream(),ClientEntity.class,params);
				for (ClientEntity client : listClientEntitys) {
					clientService.save(client);
				}
				j.setMsg("文件导入成功！");
			} catch (Exception e) {
				j.setMsg("文件导入失败！");
				logger.error(ExceptionUtil.getExceptionMessage(e));
			}finally{
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return j;
	}
	
	
	@RequestMapping(value="/list/{pageNo}/{pageSize}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="客户表列表信息",produces="application/json",httpMethod="GET")
	public ResponseMessage<List<ClientEntity>> list(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, HttpServletRequest request) {
		if(pageSize > Globals.MAX_PAGESIZE){
			return Result.error("每页请求不能超过" + Globals.MAX_PAGESIZE + "条");
		}
		CriteriaQuery query = new CriteriaQuery(ClientEntity.class);
		query.setCurPage(pageNo<=0?1:pageNo);
		query.setPageSize(pageSize<1?1:pageSize);
		List<ClientEntity> listClients = this.clientService.getListByCriteriaQuery(query,true);
		return Result.success(listClients);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="根据ID获取客户表信息",notes="根据ID获取客户表信息",httpMethod="GET",produces="application/json")
	public ResponseMessage<?> get(@ApiParam(required=true,name="id",value="ID")@PathVariable("id") String id) {
		ClientEntity task = clientService.get(ClientEntity.class, id);
		if (task == null) {
			return Result.error("根据ID获取客户表信息为空");
		}
		return Result.success(task);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="创建客户表")
	public ResponseMessage<?> create(@ApiParam(name="客户表对象")@RequestBody ClientEntity client, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<ClientEntity>> failures = validator.validate(client);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			clientService.save(client);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("客户表信息保存失败");
		}
		return Result.success(client);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="更新客户表",notes="更新客户表")
	public ResponseMessage<?> update(@ApiParam(name="客户表对象")@RequestBody ClientEntity client) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<ClientEntity>> failures = validator.validate(client);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			clientService.saveOrUpdate(client);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("更新客户表信息失败");
		}

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return Result.success("更新客户表信息成功");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value="删除客户表")
	public ResponseMessage<?> delete(@ApiParam(name="id",value="ID",required=true)@PathVariable("id") String id) {
		logger.info("delete[{}]" , id);
		// 验证
		if (StringUtils.isEmpty(id)) {
			return Result.error("ID不能为空");
		}
		try {
			clientService.deleteEntityById(ClientEntity.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("客户表删除失败");
		}

		return Result.success();
	}

	/**
	 * 检查唯一标识
	 * @param postid
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "checkPostid")
	@ResponseBody
	public AjaxJson checkPostid(String postid, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		List<ClientEntity> clients = systemService.findByProperty(ClientEntity.class, "postid", postid);
		if(clients.size() > 0) {
			message = "酒店唯一标识已存在，请重新输入!";
			j.setSuccess(false);
		}
		j.setMsg(message);
		return  j;
	}
}
