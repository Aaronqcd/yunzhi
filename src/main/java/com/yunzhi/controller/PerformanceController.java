package com.yunzhi.controller;

import io.swagger.annotations.Api;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.pojo.base.TSUserOrg;
import org.jeecgframework.web.system.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(value="Performance",description="业绩统计",tags="performanceController")
@Controller
@RequestMapping("/performanceController")
public class PerformanceController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);
    @Autowired
    private SystemService systemService;

    /**
     * 团队业绩页面
     * @param request
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {
        return new ModelAndView("yunzhi/performance/teamList");
    }

    /**
     * 回款率统计页面
     * @param request
     * @return
     */
    @RequestMapping(params = "backList")
    public ModelAndView backList(HttpServletRequest request) {
        return new ModelAndView("yunzhi/performance/backList");
    }

    /**
     * 团队业绩列表
     * @param request
     * @param response
     * @param dataGrid
     */
    @RequestMapping(params = "datagrid")
    public void datagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        TSUser user = ResourceUtil.getSessionUser();
        List<TSUserOrg> userOrgs = systemService.findByProperty(TSUserOrg.class, "tsUser.id", user.getId());
        String address = "";
        if(userOrgs.size() > 0) {
            address = userOrgs.get(0).getTsDepart().getAddress();
        }
        String sql = "select d.departname as departname,count(*) as count from t_s_depart d left join t_s_user_org uo on d.id=uo.org_id join tb_user_client uc on uo.user_id=uc.user_id\n" +
                "where d.address=? and d.description='部门' group by d.departname order by count desc";
        List<Map<String,Object>> data = systemService.findForJdbc(sql, address);
        dataGrid.setResults(data);
        dataGrid.setTotal(data.size());
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 回款情况列表
     * @param request
     * @param response
     * @param dataGrid
     */
    @RequestMapping(params = "backDatagrid")
    public void backDatagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        TSUser user = ResourceUtil.getSessionUser();
        List<TSUserOrg> userOrgs = systemService.findByProperty(TSUserOrg.class, "tsUser.id", user.getId());
        String address = "";
        if(userOrgs.size() > 0) {
            address = userOrgs.get(0).getTsDepart().getAddress();
        }
        String sql = "select c.hotel_info,c.receivable,c.deposit,c.amount from t_s_depart d join t_s_user_org uo on d.id=uo.org_id join " +
                "tb_user_client uc on uo.user_id=uc.user_id join tb_client c on c.id=uc.client_id where d.address=? and d.description='部门'";
        List<Map<String,Object>> data = systemService.findForJdbc(sql, address);
        dataGrid.setResults(data);
        dataGrid.setTotal(data.size());
        TagUtil.datagrid(response, dataGrid);
    }
}
