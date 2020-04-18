package com.yunzhi.controller;

import io.swagger.annotations.Api;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Title: Controller
 * @Description: 默认Controller
 * @author aaron
 * @date 2020-04-08 18:05:20
 * @version V1.0
 *
 */
@Api(value="DefaultController",description="默认Controller",tags="defaultController")
@Controller
@RequestMapping("/defaultController")
public class DefaultController extends BaseController {
    /**
     * 待开发页面
     * @return
     */
    @RequestMapping(params = "dev")
    public ModelAndView dev(HttpServletRequest request) {
        return new ModelAndView("yunzhi/common/dev");
    }
}
