package com.yunzhi.controller;
import com.yunzhi.entity.StaffAccountEntity;
import com.yunzhi.service.StaffAccountServiceI;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.core.util.MyBeanUtils;

import java.io.OutputStream;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.jeecgframework.core.util.ResourceUtil;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import org.jeecgframework.core.util.ExceptionUtil;

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
import java.util.Set;
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
 * @Description: 员工账户表
 * @author onlineGenerator
 * @date 2020-04-18 17:06:00
 * @version V1.0   
 *
 */
@Api(value="StaffAccount",description="员工账户表",tags="staffAccountController")
@Controller
@RequestMapping("/staffAccountController")
public class StaffAccountController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(StaffAccountController.class);

	@Autowired
	private StaffAccountServiceI staffAccountService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 员工账户表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("yunzhi/account/staff/staffAccountList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param user
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(StaffAccountEntity staffAccount,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(StaffAccountEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, staffAccount, request.getParameterMap());
		TSUser user = ResourceUtil.getSessionUser();
		List<TSRoleUser> rUsers = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
		String rolecode = "";
		if(rUsers.size() > 0) {
			rolecode = rUsers.get(0).getTSRole().getRoleCode();
		}
		try{
		//自定义追加查询条件
			if(!"admin".equals(rolecode) && !"sysmanager".equals(rolecode)) {
				cq.eq("userId", user.getId());
				cq.add();
			}
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.staffAccountService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}
	
	/**
	 * 删除员工账户表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doDel")
	@ResponseBody
	public AjaxJson doDel(StaffAccountEntity staffAccount, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		staffAccount = systemService.getEntity(StaffAccountEntity.class, staffAccount.getId());
		message = "员工账户表删除成功";
		try{
			staffAccountService.delete(staffAccount);
			systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "员工账户表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 批量删除员工账户表
	 * 
	 * @return
	 */
	 @RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids,HttpServletRequest request){
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "员工账户表删除成功";
		try{
			for(String id:ids.split(",")){
				StaffAccountEntity staffAccount = systemService.getEntity(StaffAccountEntity.class, 
				id
				);
				staffAccountService.delete(staffAccount);
				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
			}
		}catch(Exception e){
			e.printStackTrace();
			message = "员工账户表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加员工账户表
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "doAdd")
	@ResponseBody
	public AjaxJson doAdd(StaffAccountEntity staffAccount, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "员工账户表添加成功";
		TSUser user = ResourceUtil.getSessionUser();
		try{
			staffAccount.setUserId(user.getId());
			staffAccount.setBalance((double) 0);
			staffAccount.setStatus("1");
			//1表示是合伙人
			staffAccount.setType("1");
			staffAccountService.save(staffAccount);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "员工账户表添加失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 更新员工账户表
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "doUpdate")
	@ResponseBody
	public AjaxJson doUpdate(StaffAccountEntity staffAccount, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "员工账户表更新成功";
		StaffAccountEntity t = staffAccountService.get(StaffAccountEntity.class, staffAccount.getId());
		try {
			MyBeanUtils.copyBeanNotNull2Bean(staffAccount, t);
			staffAccountService.saveOrUpdate(t);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			message = "员工账户表更新失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	

	/**
	 * 员工账户表新增页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goAdd")
	public ModelAndView goAdd(StaffAccountEntity staffAccount, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(staffAccount.getId())) {
			staffAccount = staffAccountService.getEntity(StaffAccountEntity.class, staffAccount.getId());
			req.setAttribute("staffAccount", staffAccount);
		}
		return new ModelAndView("yunzhi/account/staff/staffAccount-add");
	}
	/**
	 * 员工账户表编辑页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(StaffAccountEntity staffAccount, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(staffAccount.getId())) {
			staffAccount = staffAccountService.getEntity(StaffAccountEntity.class, staffAccount.getId());
			req.setAttribute("staffAccount", staffAccount);
		}
		return new ModelAndView("yunzhi/account/staff/staffAccount-update");
	}
	
	/**
	 * 导入功能跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "upload")
	public ModelAndView upload(HttpServletRequest req) {
		req.setAttribute("controller_name","staffAccountController");
		return new ModelAndView("common/upload/pub_excel_upload");
	}
	
	/**
	 * 导出excel
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXls")
	public String exportXls(StaffAccountEntity staffAccount,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
		CriteriaQuery cq = new CriteriaQuery(StaffAccountEntity.class, dataGrid);
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, staffAccount, request.getParameterMap());
		List<StaffAccountEntity> staffAccounts = this.staffAccountService.getListByCriteriaQuery(cq,false);
		modelMap.put(NormalExcelConstants.FILE_NAME,"员工账户表");
		modelMap.put(NormalExcelConstants.CLASS,StaffAccountEntity.class);
		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("员工账户表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
			"导出信息"));
		modelMap.put(NormalExcelConstants.DATA_LIST,staffAccounts);
		return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	/**
	 * 导出excel 使模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXlsByT")
	public String exportXlsByT(StaffAccountEntity staffAccount,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
    	modelMap.put(NormalExcelConstants.FILE_NAME,"员工账户表");
    	modelMap.put(NormalExcelConstants.CLASS,StaffAccountEntity.class);
    	modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("员工账户表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
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
				List<StaffAccountEntity> listStaffAccountEntitys = ExcelImportUtil.importExcel(file.getInputStream(),StaffAccountEntity.class,params);
				for (StaffAccountEntity staffAccount : listStaffAccountEntitys) {
					staffAccountService.save(staffAccount);
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
	@ApiOperation(value="员工账户表列表信息",produces="application/json",httpMethod="GET")
	public ResponseMessage<List<StaffAccountEntity>> list(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, HttpServletRequest request) {
		if(pageSize > Globals.MAX_PAGESIZE){
			return Result.error("每页请求不能超过" + Globals.MAX_PAGESIZE + "条");
		}
		CriteriaQuery query = new CriteriaQuery(StaffAccountEntity.class);
		query.setCurPage(pageNo<=0?1:pageNo);
		query.setPageSize(pageSize<1?1:pageSize);
		List<StaffAccountEntity> listStaffAccounts = this.staffAccountService.getListByCriteriaQuery(query,true);
		return Result.success(listStaffAccounts);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="根据ID获取员工账户表信息",notes="根据ID获取员工账户表信息",httpMethod="GET",produces="application/json")
	public ResponseMessage<?> get(@ApiParam(required=true,name="id",value="ID")@PathVariable("id") String id) {
		StaffAccountEntity task = staffAccountService.get(StaffAccountEntity.class, id);
		if (task == null) {
			return Result.error("根据ID获取员工账户表信息为空");
		}
		return Result.success(task);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="创建员工账户表")
	public ResponseMessage<?> create(@ApiParam(name="员工账户表对象")@RequestBody StaffAccountEntity staffAccount, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<StaffAccountEntity>> failures = validator.validate(staffAccount);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			staffAccountService.save(staffAccount);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("员工账户表信息保存失败");
		}
		return Result.success(staffAccount);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="更新员工账户表",notes="更新员工账户表")
	public ResponseMessage<?> update(@ApiParam(name="员工账户表对象")@RequestBody StaffAccountEntity staffAccount) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<StaffAccountEntity>> failures = validator.validate(staffAccount);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			staffAccountService.saveOrUpdate(staffAccount);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("更新员工账户表信息失败");
		}

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return Result.success("更新员工账户表信息成功");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value="删除员工账户表")
	public ResponseMessage<?> delete(@ApiParam(name="id",value="ID",required=true)@PathVariable("id") String id) {
		logger.info("delete[{}]" , id);
		// 验证
		if (StringUtils.isEmpty(id)) {
			return Result.error("ID不能为空");
		}
		try {
			staffAccountService.deleteEntityById(StaffAccountEntity.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("员工账户表删除失败");
		}

		return Result.success();
	}
}
