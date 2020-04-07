package com.yunzhi.controller;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.pojo.base.TSRoleUser;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
	 * 主管列表查询
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "roleUserDatagrid")
	public void roleUserDatagrid(TSUser user,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);

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

		Criterion cc = null;
		if (roleUser.size() > 0) {
			for(int i = 0; i < roleUser.size(); i++){
				if(i == 0){
					cc = Restrictions.eq("id", roleUser.get(i).getTSUser().getId());
				}else{
					cc = cq.getor(cc, Restrictions.eq("id", roleUser.get(i).getTSUser().getId()));
				}
			}
		}else {
			cc =Restrictions.eq("id", "-1");
		}
		cq.add(cc);
		cq.eq("deleteFlag", Globals.Delete_Normal);
		cq.add();
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, user);
		this.systemService.getDataGridReturn(cq, true);
		List<TSUser> cfeList = new ArrayList<TSUser>();
		for (Object o : dataGrid.getResults()) {
			if (o instanceof TSUser) {
				TSUser cfe = (TSUser) o;
				if (cfe.getId() != null && !"".equals(cfe.getId())) {
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

}