package top.headfirst.funding.mvc.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.exception.AccessForbiddenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute(FundingConstant.ATTR_NAME_LOGIN_ADMIN);
        if (admin == null) {
            throw new AccessForbiddenException(FundingConstant.MESSAGE_ACCESS_FORBIDEN);
        }
        return true;
    }
}
