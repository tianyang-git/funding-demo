package top.headfirst.funding.mvc.handler;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.service.api.AdminService;
import top.headfirst.funding.util.ResultEntity;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminHandler {

    @Autowired
    private AdminService adminService;

    @RequestMapping("/admin/do/login.html")
    public String doLogin(
            @RequestParam("loginAcct") String loginAcct,
            @RequestParam("userPswd") String userPswd,
            HttpSession session){
        // 调用Service方法执行登录检查
        // 这个方法如果能够返回admin对象说明登录成功，如果账号、密码不正确则会抛出异常
        Admin admin= adminService.getAdminByLoginAcct(loginAcct, userPswd);
        // 将登录成功返回的admin对象存入Session域
        session.setAttribute(FundingConstant.ATTR_NAME_LOGIN_ADMIN,admin);
        return "redirect:/admin/to/main/page.html";
    }

    @RequestMapping("/admin/do/logout.html")
    public String doLogout(HttpSession session){
        // 强制Session失效
        session.invalidate();
        return "redirect:/admin/to/login/page.html";
    }

    @RequestMapping("/admin/get/page.html")
    public String getPageInfo(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            Model model){

        PageInfo<Admin> pageInfo = adminService.getPageInfo(keyword,pageNum,pageSize);
        model.addAttribute(FundingConstant.ATTR_NAME_PAGE_INFO,pageInfo);
        return "admin-page";
    }

    @PreAuthorize("hasAuthority('user:save')")
    @RequestMapping("/admin/save.html")
    public String save(Admin admin){
        adminService.saveAdmin(admin);
        return "redirect:/admin/get/page.html?pageNum=" + Integer.MAX_VALUE;
    }

    @RequestMapping("/admin/to/edit/page.html")
    public String toEditPage(
            @RequestParam(value = "adminId") Integer adminId,
            Model model){
        Admin admin = adminService.getAdminByadminId(adminId);
        model.addAttribute("admin",admin);
        return "admin-edit";
    }

    @RequestMapping("/admin/update.html")
    public String update(
            Admin admin,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){
        adminService.update(admin);
        return "redirect:/admin/get/page.html?keyword=" + keyword +"&pageNum=" + pageNum;
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @RequestMapping("/admin/remove/{adminId}/{pageNum}/{keyword}.html")
    public String remove(
            @PathVariable("adminId") Integer adminId,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("keyword") String keyword){
        adminService.remove(adminId);
        return "redirect:/admin/get/page.html?pageNum="+pageNum+"&keyword="+keyword;
    }



    @ResponseBody
    @PostAuthorize("returnObject.data.loginAcct == principal.username")
    @RequestMapping("/admin/test/post/filter.json")
    public ResultEntity<Admin> getAdminById() {
        Admin admin = new Admin();
        admin.setLoginAcct("adminOperator");
        return ResultEntity.successWithData(admin);
    }

    @PreFilter(value="filterObject % 2==0")
    @ResponseBody
    @RequestMapping("/admin/test/pre/filter")
    public ResultEntity<List<Integer>> saveList(@RequestBody List<Integer> valueList) {
        return ResultEntity.successWithData(valueList);
    }
}
