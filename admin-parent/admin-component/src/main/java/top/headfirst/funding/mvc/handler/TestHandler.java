package top.headfirst.funding.mvc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.service.api.AdminService;

import java.util.List;


@Controller
public class TestHandler {

    @Autowired
    private AdminService adminService;

    //private Logger logger = LoggerFactory.getLogger(TestHandler.class);
    //@ResponseBody
    @RequestMapping("/test/ssm.html")
    public String testSsm(Model model){
        List<Admin> adminList = adminService.getAll();
        model.addAttribute("adminList", adminList);
        return "ssmTest";
    }

}
