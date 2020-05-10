package com.yunzhi.controller;
import com.yunzhi.entity.AccountEntity;
import com.yunzhi.entity.RechargeRecordEntity;
import com.yunzhi.service.AccountServiceI;
import com.yunzhi.service.RechargeRecordServiceI;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * @Description: 充值记录表
 * @author onlineGenerator
 * @date 2020-04-10 21:18:05
 * @version V1.0   
 *
 */
@Api(value="RechargeRecord",description="充值记录表",tags="rechargeRecordController")
@Controller
@RequestMapping("/rechargeRecordController")
public class RechargeRecordController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RechargeRecordController.class);

	@Autowired
	private RechargeRecordServiceI rechargeRecordService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	@Autowired
	private AccountServiceI accountService;


	/**
	 * 充值记录表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("yunzhi/rechargerecord/rechargeRecordList");
	}

	/**
	 * 客户列表 查看充值明细
	 * @param clientId
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "newList")
	public ModelAndView newList(String clientId, HttpServletRequest request) {
		request.setAttribute("clientId", clientId);
		return new ModelAndView("yunzhi/rechargerecord/newList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "datagrid")
	public void datagrid(RechargeRecordEntity rechargeRecord,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(RechargeRecordEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, rechargeRecord, request.getParameterMap());
		TSUser tsUser = ResourceUtil.getSessionUser();
		try{
			//自定义追加查询条件
			if(tsUser != null) {
				String sql = "select id from tb_account where user_id=?";
				Map<String, Object> map = systemService.findOneForJdbc(sql, tsUser.getId());
				if(map != null) {
					cq.eq("account.id", map.get("id"));
				}
			}
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.rechargeRecordService.getDataGridReturn(cq, true);
		List<RechargeRecordEntity> rechargeRecords = dataGrid.getResults();
		Map<String,Map<String,Object>> extMap = new HashMap<String, Map<String,Object>>();
		for(RechargeRecordEntity temp : rechargeRecords){
			//此为针对原来的行数据，拓展的新字段
			Map m = new HashMap();
			m.put("hotelInfo", temp.getAccount().getHotelInfo());
			extMap.put(temp.getId(), m);
		}
		TagUtil.datagrid(response, dataGrid, extMap);
	}

	@RequestMapping(params = "newDatagrid")
	public void newDatagrid(RechargeRecordEntity rechargeRecord,String clientId,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(RechargeRecordEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, rechargeRecord, request.getParameterMap());
		try{
			//自定义追加查询条件
			if(clientId != null) {
				String sql = "select id from tb_account where client_id=?";
				Map<String, Object> map = systemService.findOneForJdbc(sql, clientId);
				if(map != null) {
					cq.eq("account.id", map.get("id"));
				}
				else {
					cq.eq("account.id", "@1");
				}
			}
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.rechargeRecordService.getDataGridReturn(cq, true);
		List<RechargeRecordEntity> rechargeRecords = dataGrid.getResults();
		Map<String,Map<String,Object>> extMap = new HashMap<String, Map<String,Object>>();
		for(RechargeRecordEntity temp : rechargeRecords){
			//此为针对原来的行数据，拓展的新字段
			Map m = new HashMap();
			m.put("hotelInfo", temp.getAccount().getHotelInfo());
			extMap.put(temp.getId(), m);
		}
		TagUtil.datagrid(response, dataGrid, extMap);
	}
	
	/**
	 * 删除充值记录表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doDel")
	@ResponseBody
	public AjaxJson doDel(RechargeRecordEntity rechargeRecord, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		rechargeRecord = systemService.getEntity(RechargeRecordEntity.class, rechargeRecord.getId());
		message = "充值记录表删除成功";
		try{
			rechargeRecordService.delete(rechargeRecord);
			systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "充值记录表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 批量删除充值记录表
	 * 
	 * @return
	 */
	 @RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids,HttpServletRequest request){
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "充值记录表删除成功";
		try{
			for(String id:ids.split(",")){
				RechargeRecordEntity rechargeRecord = systemService.getEntity(RechargeRecordEntity.class, 
				id
				);
				rechargeRecordService.delete(rechargeRecord);
				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
			}
		}catch(Exception e){
			e.printStackTrace();
			message = "充值记录表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加充值记录表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doAdd")
	@ResponseBody
	public AjaxJson doAdd(RechargeRecordEntity rechargeRecord, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "充值记录表添加成功";
		TSUser user = ResourceUtil.getSessionUser();
		try{
			//充值，添加充值记录并更新账户表的余额
			rechargeRecord.setStatus("0");
			rechargeRecordService.saveRechargeRecord(user.getId(), rechargeRecord);
			/*Map<String, Object> account = systemService.findOneForJdbc("select id from tb_account where user_id=?", user.getId());
			String id = (String) account.get("id");
			AccountEntity accountEntity = accountService.get(AccountEntity.class, id);
			Double balance = accountEntity.getBalance() + rechargeRecord.getMoney();
			accountEntity.setBalance(balance);
			accountService.saveOrUpdate(accountEntity);
			String comment = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			Date now = new Date();
			String time = sdf.format(now);
			comment = time + " 充值" + rechargeRecord.getMoney() + "人民币";
			rechargeRecord.setComment(comment);
			rechargeRecord.setAccount(accountEntity);
			rechargeRecordService.save(rechargeRecord);*/
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "充值记录表添加失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 更新充值记录表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doUpdate")
	@ResponseBody
	public AjaxJson doUpdate(RechargeRecordEntity rechargeRecord, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "充值记录表更新成功";
		RechargeRecordEntity t = rechargeRecordService.get(RechargeRecordEntity.class, rechargeRecord.getId());
		try {
			MyBeanUtils.copyBeanNotNull2Bean(rechargeRecord, t);
			rechargeRecordService.saveOrUpdate(t);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			message = "充值记录表更新失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	

	/**
	 * 充值记录表新增页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goAdd")
	public ModelAndView goAdd(RechargeRecordEntity rechargeRecord, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(rechargeRecord.getId())) {
			rechargeRecord = rechargeRecordService.getEntity(RechargeRecordEntity.class, rechargeRecord.getId());
			req.setAttribute("rechargeRecord", rechargeRecord);
		}
		return new ModelAndView("yunzhi/rechargerecord/rechargeRecord-add");
	}
	/**
	 * 充值记录表编辑页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(RechargeRecordEntity rechargeRecord, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(rechargeRecord.getId())) {
			rechargeRecord = rechargeRecordService.getEntity(RechargeRecordEntity.class, rechargeRecord.getId());
			req.setAttribute("rechargeRecord", rechargeRecord);
		}
		return new ModelAndView("yunzhi/rechargerecord/rechargeRecord-update");
	}
	
	/**
	 * 导入功能跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "upload")
	public ModelAndView upload(HttpServletRequest req) {
		req.setAttribute("controller_name","rechargeRecordController");
		return new ModelAndView("common/upload/pub_excel_upload");
	}
	
	/**
	 * 导出excel
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXls")
	public String exportXls(RechargeRecordEntity rechargeRecord,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
		CriteriaQuery cq = new CriteriaQuery(RechargeRecordEntity.class, dataGrid);
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, rechargeRecord, request.getParameterMap());
		List<RechargeRecordEntity> rechargeRecords = this.rechargeRecordService.getListByCriteriaQuery(cq,false);
		modelMap.put(NormalExcelConstants.FILE_NAME,"充值记录表");
		modelMap.put(NormalExcelConstants.CLASS,RechargeRecordEntity.class);
		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("充值记录表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
			"导出信息"));
		modelMap.put(NormalExcelConstants.DATA_LIST,rechargeRecords);
		return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	/**
	 * 导出excel 使模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXlsByT")
	public String exportXlsByT(RechargeRecordEntity rechargeRecord,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
    	modelMap.put(NormalExcelConstants.FILE_NAME,"充值记录表");
    	modelMap.put(NormalExcelConstants.CLASS,RechargeRecordEntity.class);
    	modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("充值记录表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
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
				List<RechargeRecordEntity> listRechargeRecordEntitys = ExcelImportUtil.importExcel(file.getInputStream(),RechargeRecordEntity.class,params);
				for (RechargeRecordEntity rechargeRecord : listRechargeRecordEntitys) {
					rechargeRecordService.save(rechargeRecord);
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
	@ApiOperation(value="充值记录表列表信息",produces="application/json",httpMethod="GET")
	public ResponseMessage<List<RechargeRecordEntity>> list(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, HttpServletRequest request) {
		if(pageSize > Globals.MAX_PAGESIZE){
			return Result.error("每页请求不能超过" + Globals.MAX_PAGESIZE + "条");
		}
		CriteriaQuery query = new CriteriaQuery(RechargeRecordEntity.class);
		query.setCurPage(pageNo<=0?1:pageNo);
		query.setPageSize(pageSize<1?1:pageSize);
		List<RechargeRecordEntity> listRechargeRecords = this.rechargeRecordService.getListByCriteriaQuery(query,true);
		return Result.success(listRechargeRecords);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="根据ID获取充值记录表信息",notes="根据ID获取充值记录表信息",httpMethod="GET",produces="application/json")
	public ResponseMessage<?> get(@ApiParam(required=true,name="id",value="ID")@PathVariable("id") String id) {
		RechargeRecordEntity task = rechargeRecordService.get(RechargeRecordEntity.class, id);
		if (task == null) {
			return Result.error("根据ID获取充值记录表信息为空");
		}
		return Result.success(task);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="创建充值记录表")
	public ResponseMessage<?> create(@ApiParam(name="充值记录表对象")@RequestBody RechargeRecordEntity rechargeRecord, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<RechargeRecordEntity>> failures = validator.validate(rechargeRecord);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			rechargeRecordService.save(rechargeRecord);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("充值记录表信息保存失败");
		}
		return Result.success(rechargeRecord);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="更新充值记录表",notes="更新充值记录表")
	public ResponseMessage<?> update(@ApiParam(name="充值记录表对象")@RequestBody RechargeRecordEntity rechargeRecord) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<RechargeRecordEntity>> failures = validator.validate(rechargeRecord);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			rechargeRecordService.saveOrUpdate(rechargeRecord);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("更新充值记录表信息失败");
		}

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return Result.success("更新充值记录表信息成功");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value="删除充值记录表")
	public ResponseMessage<?> delete(@ApiParam(name="id",value="ID",required=true)@PathVariable("id") String id) {
		logger.info("delete[{}]" , id);
		// 验证
		if (StringUtils.isEmpty(id)) {
			return Result.error("ID不能为空");
		}
		try {
			rechargeRecordService.deleteEntityById(RechargeRecordEntity.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("充值记录表删除失败");
		}

		return Result.success();
	}
}
