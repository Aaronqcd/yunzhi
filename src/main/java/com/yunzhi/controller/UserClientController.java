package com.yunzhi.controller;
import com.yunzhi.entity.AccountEntity;
import com.yunzhi.entity.ClientEntity;
import com.yunzhi.entity.UserClientEntity;
import com.yunzhi.service.AccountServiceI;
import com.yunzhi.service.ClientServiceI;
import com.yunzhi.service.UserClientServiceI;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * @Description: 员工客户关联表
 * @author onlineGenerator
 * @date 2020-04-19 16:08:00
 * @version V1.0   
 *
 */
@Api(value="UserClient",description="员工客户关联表",tags="userClientController")
@Controller
@RequestMapping("/userClientController")
public class UserClientController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UserClientController.class);

	@Autowired
	private UserClientServiceI userClientService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	@Autowired
	private AccountServiceI accountService;
	@Autowired
	private ClientServiceI clientService;

	/**
	 * 员工客户关联表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("yunzhi/personal/tbUserClientList");
	}

	@RequestMapping(params="getHotelUser")
	@ResponseBody
	public List<Map<String, Object>> getHotelUser(HttpServletRequest req) throws Exception{
		String sql = "select bu.id,bu.username from t_s_base_user bu join t_s_role_user ru on bu.id=ru.userid join t_s_role r on ru.roleid=r.id " +
				"where r.rolecode='hotel_user' and bu.id not in (select uc.user_id from tb_user_client uc where uc.type='1')";
		List<Map<String, Object>> list = systemService.findForJdbc(sql);
		return list;
	}

	@RequestMapping(params="getClient")
	@ResponseBody
	public List<Map<String, Object>> getClient(HttpServletRequest req) throws Exception{
		String sql = "select c.id,c.hotel_info from tb_client c where c.id not in (select uc.client_id from tb_user_client uc where uc.type='1')";
		List<Map<String, Object>> list = systemService.findForJdbc(sql);
		return list;
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(UserClientEntity tbUserClient,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(UserClientEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, tbUserClient, request.getParameterMap());
		try{
		//自定义追加查询条件
			cq.eq("type", "1");
		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.userClientService.getDataGridReturn(cq, true);
		List<UserClientEntity> results = dataGrid.getResults();
		Map<String,Map<String,Object>> extMap = new HashMap<String, Map<String,Object>>();
		for(UserClientEntity temp : results){
			//此为针对原来的行数据，拓展的新字段
			Map m = new HashMap();
			m.put("hotelInfo", temp.getClient().getHotelInfo());
			m.put("username", temp.getUser().getUserName());
			extMap.put(temp.getId(), m);
		}
		TagUtil.datagrid(response, dataGrid, extMap);
	}
	
	/**
	 * 删除员工客户关联表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doDel")
	@ResponseBody
	public AjaxJson doDel(UserClientEntity tbUserClient, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		tbUserClient = systemService.getEntity(UserClientEntity.class, tbUserClient.getId());
		message = "员工客户关联表删除成功";
		try{
			userClientService.delete(tbUserClient);
			systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "员工客户关联表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 批量删除员工客户关联表
	 * 
	 * @return
	 */
	 @RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids,HttpServletRequest request){
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "员工客户关联表删除成功";
		try{
			for(String id:ids.split(",")){
				UserClientEntity userClient = systemService.getEntity(UserClientEntity.class,
				id
				);
				userClientService.delete(userClient);
				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
			}
		}catch(Exception e){
			e.printStackTrace();
			message = "员工客户关联表删除失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加员工客户关联表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doAdd")
	@ResponseBody
	public AjaxJson doAdd(UserClientEntity userClient, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "员工客户关联表添加成功";
		try{
			//1表示是酒店用户与酒店关系
			userClient.setType("1");
			userClientService.save(userClient);
			ClientEntity clientEntity = clientService.getEntity(ClientEntity.class, userClient.getClient().getId());
			AccountEntity account = new AccountEntity();
			account.setBalance((double) 0);
			account.setStatus("1");//1表示正常
			account.setClient(userClient.getClient());
			account.setHotelInfo(clientEntity.getHotelInfo());
			account.setUser(userClient.getUser());
			accountService.save(account);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "员工客户关联表添加失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	
	/**
	 * 更新员工客户关联表
	 * 
	 * @return
	 */
	@RequestMapping(params = "doUpdate")
	@ResponseBody
	public AjaxJson doUpdate(UserClientEntity tbUserClient, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		message = "员工客户关联表更新成功";
		UserClientEntity t = userClientService.get(UserClientEntity.class, tbUserClient.getId());
		try {
			MyBeanUtils.copyBeanNotNull2Bean(tbUserClient, t);
			userClientService.saveOrUpdate(t);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			message = "员工客户关联表更新失败";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	

	/**
	 * 员工客户关联表新增页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goAdd")
	public ModelAndView goAdd(UserClientEntity userClient, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userClient.getId())) {
			userClient = userClientService.getEntity(UserClientEntity.class,  userClient.getId());
			req.setAttribute("tbUserClient", userClient);
		}
		return new ModelAndView("yunzhi/personal/tbUserClient-add");
	}
	/**
	 * 员工客户关联表编辑页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(UserClientEntity userClient, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userClient.getId())) {
			userClient = userClientService.getEntity(UserClientEntity.class, userClient.getId());
			req.setAttribute("tbUserClient", userClient);
		}
		return new ModelAndView("yunzhi/personal/tbUserClient-update");
	}

	@RequestMapping(params = "goDetail")
	public ModelAndView goDetail(UserClientEntity userClient, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userClient.getId())) {
			userClient = userClientService.getEntity(UserClientEntity.class, userClient.getId());
			req.setAttribute("tbUserClient", userClient);
		}
		return new ModelAndView("yunzhi/personal/tbUserClient-detail");
	}
	
	/**
	 * 导入功能跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "upload")
	public ModelAndView upload(HttpServletRequest req) {
		req.setAttribute("controller_name","tbUserClientController");
		return new ModelAndView("common/upload/pub_excel_upload");
	}
	
	/**
	 * 导出excel
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXls")
	public String exportXls(UserClientEntity userClient,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
		CriteriaQuery cq = new CriteriaQuery(UserClientEntity.class, dataGrid);
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userClient, request.getParameterMap());
		List<UserClientEntity> tbUserClients = this.userClientService.getListByCriteriaQuery(cq,false);
		modelMap.put(NormalExcelConstants.FILE_NAME,"员工客户关联表");
		modelMap.put(NormalExcelConstants.CLASS,UserClientEntity.class);
		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("员工客户关联表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
			"导出信息"));
		modelMap.put(NormalExcelConstants.DATA_LIST,tbUserClients);
		return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	/**
	 * 导出excel 使模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "exportXlsByT")
	public String exportXlsByT(UserClientEntity userClient,HttpServletRequest request,HttpServletResponse response
			, DataGrid dataGrid,ModelMap modelMap) {
    	modelMap.put(NormalExcelConstants.FILE_NAME,"员工客户关联表");
    	modelMap.put(NormalExcelConstants.CLASS,UserClientEntity.class);
    	modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("员工客户关联表列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
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
				List<UserClientEntity> listTbUserClientEntitys = ExcelImportUtil.importExcel(file.getInputStream(),UserClientEntity.class,params);
				for (UserClientEntity userClient : listTbUserClientEntitys) {
					userClientService.save(userClient);
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
	@ApiOperation(value="员工客户关联表列表信息",produces="application/json",httpMethod="GET")
	public ResponseMessage<List<UserClientEntity>> list(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, HttpServletRequest request) {
		if(pageSize > Globals.MAX_PAGESIZE){
			return Result.error("每页请求不能超过" + Globals.MAX_PAGESIZE + "条");
		}
		CriteriaQuery query = new CriteriaQuery(UserClientEntity.class);
		query.setCurPage(pageNo<=0?1:pageNo);
		query.setPageSize(pageSize<1?1:pageSize);
		List<UserClientEntity> listTbUserClients = this.userClientService.getListByCriteriaQuery(query,true);
		return Result.success(listTbUserClients);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="根据ID获取员工客户关联表信息",notes="根据ID获取员工客户关联表信息",httpMethod="GET",produces="application/json")
	public ResponseMessage<?> get(@ApiParam(required=true,name="id",value="ID")@PathVariable("id") String id) {
		UserClientEntity task = userClientService.get(UserClientEntity.class, id);
		if (task == null) {
			return Result.error("根据ID获取员工客户关联表信息为空");
		}
		return Result.success(task);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="创建员工客户关联表")
	public ResponseMessage<?> create(@ApiParam(name="员工客户关联表对象")@RequestBody UserClientEntity tbUserClient, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserClientEntity>> failures = validator.validate(tbUserClient);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			userClientService.save(tbUserClient);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("员工客户关联表信息保存失败");
		}
		return Result.success(tbUserClient);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value="更新员工客户关联表",notes="更新员工客户关联表")
	public ResponseMessage<?> update(@ApiParam(name="员工客户关联表对象")@RequestBody UserClientEntity tbUserClient) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserClientEntity>> failures = validator.validate(tbUserClient);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//保存
		try{
			userClientService.saveOrUpdate(tbUserClient);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("更新员工客户关联表信息失败");
		}

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return Result.success("更新员工客户关联表信息成功");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value="删除员工客户关联表")
	public ResponseMessage<?> delete(@ApiParam(name="id",value="ID",required=true)@PathVariable("id") String id) {
		logger.info("delete[{}]" , id);
		// 验证
		if (StringUtils.isEmpty(id)) {
			return Result.error("ID不能为空");
		}
		try {
			userClientService.deleteEntityById(UserClientEntity.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("员工客户关联表删除失败");
		}

		return Result.success();
	}
}
