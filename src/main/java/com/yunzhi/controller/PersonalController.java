package com.yunzhi.controller;

import com.yunzhi.entity.ClientEntity;
import com.yunzhi.entity.UserClientEntity;
import com.yunzhi.service.ClientServiceI;
import com.yunzhi.service.UserClientServiceI;
import io.swagger.annotations.Api;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="Personal",description="个人资料",tags="personalController")
@Controller
@RequestMapping("/personalController")
public class PersonalController extends BaseController {
    @Autowired
    private ClientServiceI clientService;
    @Autowired
    private UserClientServiceI userClientService;
    @Autowired
    private SystemService systemService;

    @RequestMapping(params = "index")
    public ModelAndView index(HttpServletRequest request) {
        return new ModelAndView("yunzhi/personal/index");
    }

    @RequestMapping(params = "about")
    public ModelAndView about(HttpServletRequest request) {
        return new ModelAndView("yunzhi/personal/about");
    }

    /**
     * 酒店信息
     * @param request
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {
        return new ModelAndView("yunzhi/personal/clientList");
    }

    @RequestMapping(params = "goClients")
    public ModelAndView goClients(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("yunzhi/personal/clients");
        String ids = oConvertUtils.getString(request.getParameter("ids"));
        mv.addObject("ids", ids);
        return mv;
    }

    @RequestMapping(params = "datagrid")
    public void datagrid(ClientEntity client, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(ClientEntity.class, dataGrid);
        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, client, request.getParameterMap());
        TSUser user = ResourceUtil.getSessionUser();
        String sql = "select uc.client_id from tb_user_client uc where uc.user_id=? and uc.type='1'";
        List<Map<String, Object>> map = systemService.findForJdbc(sql, user.getId());
        String id = null;
        if(map.size() > 0) {
            id = (String) map.get(0).get("client_id");
        }
        try{
            //自定义追加查询条件
            if(id != null) {
                cq.eq("id", id);
            }
        }catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        cq.add();
        this.userClientService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
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
        return new ModelAndView("yunzhi/personal/client-update");
    }

    @RequestMapping(params = "goDetail")
    public ModelAndView goDetail(ClientEntity client, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(client.getId())) {
            client = clientService.getEntity(ClientEntity.class, client.getId());
            req.setAttribute("client", client);
        }
        return new ModelAndView("yunzhi/personal/client-detail");
    }
}
