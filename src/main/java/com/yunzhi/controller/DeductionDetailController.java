package com.yunzhi.controller;
import com.yunzhi.entity.DeductionDetailEntity;
import com.yunzhi.service.DeductionDetailServiceI;

import java.util.ArrayList;
import java.util.List;
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
 * @Description: 扣费明细表
 * @author onlineGenerator
 * @date 2020-04-11 18:00:43
 * @version V1.0   
 *
 */
@Api(value="DeductionDetail",description="扣费明细表",tags="deductionDetailController")
@Controller
@RequestMapping("/deductionDetailController")
public class DeductionDetailController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(DeductionDetailController.class);

	@Autowired
	private DeductionDetailServiceI deductionDetailService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 扣费明细表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("yunzhi/deduction/deductionDetailList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(DeductionDetailEntity deductionDetail,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(DeductionDetailEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, deductionDetail, request.getParameterMap());
		try{
		//自定义追加查询条件
		
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.deductionDetailService.getDataGridReturn(cq, true);
		List<DeductionDetailEntity> deductionRecords = dataGrid.getResults();
		Map<String,Map<String,Object>> extMap = new HashMap<String, Map<String,Object>>();
		for(DeductionDetailEntity temp : deductionRecords){
			//此为针对原来的行数据，拓展的新字段
			Map m = new HashMap();
			m.put("hotelInfo", temp.getAccount().getHotelInfo());
			extMap.put(temp.getId(), m);
		}
		TagUtil.datagrid(response, dataGrid, extMap);
	}
	
	/**
	 * 删除扣费明细表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doDel")
	@ResponseBody
	public AjaxJson doDel(DeductionDetailEntity deductionDetail, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		deductionDetail = systemService.getEntity(DeductionDetailEntity.class, deductionDetail.getId());
		message = "扣费明细表删除成功";
		try{
			deductionDetailService.delete(deductionDetail);
			systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "扣费明细表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 批量删除扣费明细表
	 * 
	 * @return
	 */
	 @RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids,HttpServletRequest request){
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "扣费明细表删除成功";
		try{
			for(String id:ids.split(",")){
				DeductionDetailEntity deductionDetail = systemService.getEntity(DeductionDetailEntity.class, 
				id
				);
				deductionDetailService.delete(deductionDetail);
				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
			}
		}catch(Exception e){
			e.printStackTrace();
			message = "扣费明细表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加扣费明细表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doAdd")
	@ResponseBody
	public AjaxJson doAdd(DeductionDetailEntity deductionDetail, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "扣费明细表添加成功";
		TSUser user = ResourceUtil.getSessionUser();
		try{
			deductionDetail.setStatus("0");
			deductionDetailService.saveDeductionDetail(user.getId(), deductionDetail);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "扣费明细表添加失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 更新扣费明细表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doUpdate")
	@ResponseBody
	public AjaxJson doUpdate(DeductionDetailEntity deductionDetail, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "扣费明细表更新成功";
		DeductionDetailEntity t = deductionDetailService.get(DeductionDetailEntity.class, deductionDetail.getId());
		try {
			MyBeanUtils.copyBeanNotNull2Bean(deductionDetail, t);
			deductionDetailService.saveOrUpdate(t);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			message = "扣费明细表更新失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	

	/**
	 * 扣费明细表新增页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goAdd")
	public ModelAndView goAdd(DeductionDetailEntity deductionDetail, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(deductionDetail.getId())) {
			deductionDetail = deductionDetailService.getEntity(DeductionDetailEntity.class, deductionDetail.getId());
			req.setAttribute("deductionDetail", deductionDetail);
		}
		return new ModelAndView("yunzhi/deduction/deductionDetail-add");
	}
	/**
	 * 扣费明细表编辑页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(DeductionDetailEntity deductionDetail, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(deductionDetail.getId())) {
			deductionDetail = deductionDetailService.getEntity(DeductionDetailEntity.class, deductionDetail.getId());
			req.setAttribute("deductionDetail", deductionDetail);
		}
		return new ModelAndView("yunzhi/deduction/deductionDetail-update");
	}
	
	/**
	 * 导入功能跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "upload")
	public ModelAndView upload(HttpServletRequest req) {
		req.setAttribute("controller_name","deductionDetailController");
		return new ModelAndView("common/upload/pub_excel_upload");
	}
	
	/**
	 * 导出excel
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXls")
	public String exportXls(DeductionDetailEntity deductionDetail,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
		CriteriaQuery cq = new CriteriaQuery(DeductionDetailEntity.class, dataGrid);
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, deductionDetail, request.getParameterMap());
		List<DeductionDetailEntity> deductionDetails = this.deductionDetailService.getListByCriteriaQuery(cq,false);
		modelMap.put(NormalExcelConstants.FILE_NAME,"扣费明细表");
		modelMap.put(NormalExcelConstants.CLASS,DeductionDetailEntity.class);
		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("扣费明细表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
			"导出信息"));
		modelMap.put(NormalExcelConstants.DATA_LIST,deductionDetails);
		return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	/**
	 * 导出excel 使模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXlsByT")
	public String exportXlsByT(DeductionDetailEntity deductionDetail,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
    	modelMap.put(NormalExcelConstants.FILE_NAME,"扣费明细表");
    	modelMap.put(NormalExcelConstants.CLASS,DeductionDetailEntity.class);
    	modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("扣费明细表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
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
				List<DeductionDetailEntity> listDeductionDetailEntitys = ExcelImportUtil.importExcel(file.getInputStream(),DeductionDetailEntity.class,params);
				for (DeductionDetailEntity deductionDetail : listDeductionDetailEntitys) {
					deductionDetailService.save(deductionDetail);
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
	@ApiOperation(value="扣费明细表列表信息",produces="application/json",httpMethod="GET")
	public ResponseMessage<List<DeductionDetailEntity>> list(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, HttpServletRequest request) {
		if(pageSize > Globals.MAX_PAGESIZE){
			return Result.error("每页请求不能超过" + Globals.MAX_PAGESIZE + "条");
		}
		CriteriaQuery query = new CriteriaQuery(DeductionDetailEntity.class);
		query.setCurPage(pageNo<=0?1:pageNo);
		query.setPageSize(pageSize<1?1:pageSize);
		List<DeductionDetailEntity> listDeductionDetails = this.deductionDetailService.getListByCriteriaQuery(query,true);
		return Result.success(listDeductionDetails);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="根据ID获取扣费明细表信息",notes="根据ID获取扣费明细表信息",httpMethod="GET",produces="application/json")
	public ResponseMessage<?> get(@ApiParam(required=true,name="id",value="ID")@PathVariable("id") String id) {
		DeductionDetailEntity task = deductionDetailService.get(DeductionDetailEntity.class, id);
		if (task == null) {
			return Result.error("根据ID获取扣费明细表信息为空");
		}
		return Result.success(task);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="创建扣费明细表")
	public ResponseMessage<?> create(@ApiParam(name="扣费明细表对象")@RequestBody DeductionDetailEntity deductionDetail, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<DeductionDetailEntity>> failures = validator.validate(deductionDetail);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			deductionDetailService.save(deductionDetail);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("扣费明细表信息保存失败");
		}
		return Result.success(deductionDetail);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="更新扣费明细表",notes="更新扣费明细表")
	public ResponseMessage<?> update(@ApiParam(name="扣费明细表对象")@RequestBody DeductionDetailEntity deductionDetail) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<DeductionDetailEntity>> failures = validator.validate(deductionDetail);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			deductionDetailService.saveOrUpdate(deductionDetail);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("更新扣费明细表信息失败");
		}

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return Result.success("更新扣费明细表信息成功");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value="删除扣费明细表")
	public ResponseMessage<?> delete(@ApiParam(name="id",value="ID",required=true)@PathVariable("id") String id) {
		logger.info("delete[{}]" , id);
		// 验证
		if (StringUtils.isEmpty(id)) {
			return Result.error("ID不能为空");
		}
		try {
			deductionDetailService.deleteEntityById(DeductionDetailEntity.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("扣费明细表删除失败");
		}

		return Result.success();
	}
}
