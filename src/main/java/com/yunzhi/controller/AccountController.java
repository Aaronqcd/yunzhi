package com.yunzhi.controller;

import com.yunzhi.entity.AccountEntity;
import com.yunzhi.entity.ClientEntity;
import com.yunzhi.service.AccountServiceI;
import com.yunzhi.service.ClientServiceI;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.pojo.base.*;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AccountController
 * @Description: TODO(账户管理处理类)
 * @author aaron
 */
@Controller
@RequestMapping("/accountController")
public class AccountController extends BaseController {
	private SystemService systemService;
	@Autowired
	private AccountServiceI accountService;
	@Autowired
	private ClientServiceI clientService;

	@Autowired
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	/**
	 * 主管列表
	 * @return
	 */
	@RequestMapping(params = "chargeList")
	public ModelAndView chargeList(HttpServletRequest request) {
		request.setAttribute("roleId", request.getParameter("roleId"));
		return new ModelAndView("yunzhi/account/charge/chargeList");
	}

	/**
	 * 注册用户信息审核
	 * @return
	 */
	@RequestMapping(params = "goAudit")
	public ModelAndView goAudit(HttpServletRequest request) {
		return new ModelAndView("yunzhi/register/userList");
	}

	/**
	 * 主管列表查询
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "roleUserDatagrid")
	public void roleUserDatagrid(TSUser user,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);
		TSUser tsUser = ResourceUtil.getSessionUser();
		List<TSRoleUser> rUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", tsUser.getId());
		TSRole role = rUser.get(0).getTSRole();
		String roleCode = role.getRoleCode();
		String departId = null;
		String sql;
		Map<String, Object> map;
		//单独处理主管角色
		if("charge".equals(roleCode)) {
			sql = "select d.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.tier='3' and uo.user_id=?";
			map = systemService.findOneForJdbc(sql, tsUser.getId());
			if(map == null) {
				sql = "select d.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.tier='2' and uo.user_id=?";
				map = systemService.findOneForJdbc(sql, tsUser.getId());
			}
			departId = (String) map.get("id");
		}

//		systemService.findByDetached()
//		List<TSUserOrg> us = systemService.findByProperty(TSRoleUser.class, "TSUser.id", tsUser.getId());


		//查询条件组装器
		String roleId = request.getParameter("roleId");
		List<TSRoleUser> roleUser = systemService.findByProperty(TSRoleUser.class, "TSRole.id", roleId);
        /*
        // zhanggm：这个查询逻辑也可以使用这种 子查询的方式进行查询
        CriteriaQuery subCq = new CriteriaQuery(TSRoleUser.class);
        subCq.setProjection(Property.forName("TSUser.id"));
        subCq.eq("TSRole.id", roleId);
        subCq.add();
        cq.add(Property.forName("id").in(subCq.getDetachedCriteria()));
        cq.add();
        */
		String userId = null;
		Criterion cc = null;
		int count = 0;
		if (roleUser.size() > 0) {
			for(int i = 0; i < roleUser.size(); i++){
				userId = roleUser.get(i).getTSUser().getId();
				String userOrgId;
				//单独处理主管角色
				if("charge".equals(roleCode)) {
					sql = "select uo.id from t_s_user_org uo join t_s_depart d on uo.org_id=d.id where d.id=? and uo.user_id=?";
					map = systemService.findOneForJdbc(sql, departId, userId);
					if(map != null) {
						if(count == 0){
							cc = Restrictions.eq("id", userId);
						}else{
							cc = cq.getor(cc, Restrictions.eq("id", userId));
						}
						count++;
					}
				}
				else {
					if(i == 0){
						cc = Restrictions.eq("id", userId);
					}else{
						cc = cq.getor(cc, Restrictions.eq("id", userId));
					}
				}
			}
		}else {
			cc =Restrictions.eq("id", "-1");
		}
		cq.add(cc);
		cq.eq("deleteFlag", Globals.Delete_Normal);
		cq.add();
		cq.eq("province", tsUser.getProvince());
		cq.add();
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, user);
		this.systemService.getDataGridReturn(cq, true);
		List<TSUser> cfeList = new ArrayList<TSUser>();
		for (Object o : dataGrid.getResults()) {
			if (o instanceof TSUser) {
				TSUser cfe = (TSUser) o;
				if (cfe.getId() != null && !"".equals(cfe.getId())) {
					if (roleUser.size() > 0) {
						TSRoleUser ru = roleUser.get(0);
						String roleName = ru.getTSRole().getRoleName();
						cfe.setUserKey(roleName);
//						String roleName = "";
//						for (TSRoleUser ru : roleUser) {
//							roleName += ru.getTSRole().getRoleName() + ",";
//						}
//						roleName = roleName.substring(0, roleName.length() - 1);
//						cfe.setUserKey(roleName);
					}
				}
				cfeList.add(cfe);
			}
		}
		TagUtil.datagrid(response, dataGrid);
	}

	@RequestMapping(params = "userAuditDatagrid")
	public void userAuditDatagrid(TSUser user,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);

		//status=0表示需要审核
		cq.eq("status", (short)0);

		cq.eq("deleteFlag", Globals.Delete_Normal);
		cq.add();
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, user);
		this.systemService.getDataGridReturn(cq, true);
		List<TSUser> cfeList = new ArrayList<TSUser>();
		for (Object o : dataGrid.getResults()) {
			if (o instanceof TSUser) {
				TSUser cfe = (TSUser) o;
				if (cfe.getId() != null && !"".equals(cfe.getId())) {
					List<TSRoleUser> roleUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", cfe.getId());
					if (roleUser.size() > 0) {
						String roleName = "";
						for (TSRoleUser ru : roleUser) {
							roleName += ru.getTSRole().getRoleName() + ",";
						}
						roleName = roleName.substring(0, roleName.length() - 1);
						cfe.setUserKey(roleName);
					}
				}
				cfeList.add(cfe);
			}
		}
		TagUtil.datagrid(response, dataGrid);
	}

	@RequestMapping(params = "message", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson getMessage(ClientEntity client, HttpServletRequest request, HttpServletResponse response) {
		String message = null;
		AjaxJson j = new AjaxJson();
		j.setSuccess(false);
		String ipAddress = client.getIpAddress();
		String postid = client.getPostid();
		ClientEntity clientEntity = clientService.findUniqueByProperty(ClientEntity.class, "postid", postid);
		clientEntity.setIpAddress(ipAddress);
		try {
			clientService.saveOrUpdate(clientEntity);
			j.setSuccess(true);
			message = "酒店的ip地址保存成功";
		} catch (Exception e) {
			e.printStackTrace();
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 客户表编辑页面跳转
	 *
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(AccountEntity account, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(account.getId())) {
			account = accountService.getEntity(AccountEntity.class, account.getId());
			req.setAttribute("account", account);
		}
		return new ModelAndView("yunzhi/ledger/account-update");
	}

	@RequestMapping(params = "goDetail")
	public ModelAndView goDetail(AccountEntity account, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(account.getId())) {
			account = accountService.getEntity(AccountEntity.class, account.getId());
			req.setAttribute("account", account);
		}
		return new ModelAndView("yunzhi/ledger/account-detail");
	}

}