package top.headfirst.funding.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.headfirst.funding.constant.FundingConstant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 表示当前类是一个配置类
@Configurable
// 启用Web环境下权限控制功能
@EnableWebSecurity
// 启用全局方法权限控制功能，并且设置prePostEnabled = true。保证@PreAuthority、@PostAuthority、@PreFilter、@PostFilter生效
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class FundingWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        // 与 SpringSecurity 环境下用户登录相关
        builder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        // 与 SpringSecurity 环境下请求授权相关
        security
                .authorizeRequests()	// 对请求进行授权
                .antMatchers("/admin/to/login/page.html")	// 针对登录页进行设置，无条件访问
                .permitAll()
                .antMatchers("/bootstrap/**")	// 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/crowd/**")       // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/css/**")         // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/fonts/**")       // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/img/**")         // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/jquery/**")      // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/layer/**")       // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/script/**")      // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/ztree/**")       // 针对静态资源进行设置，无条件访问
                .permitAll()
                .antMatchers("/admin/get/page.html")	// 针对分页显示Admin数据设定访问控制
                // .hasRole("经理")					// 要求具备经理角色
                .access("hasRole('经理') OR hasAuthority('user:get')")	// 要求具备“经理”角色和“user:get”权限二者之一
                .anyRequest()					// 其他任意请求
                .authenticated()				// 认证后访问
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        request.setAttribute("exception",new Exception(FundingConstant.MESSAGE_ACCESS_DENIED));
                        request.getRequestDispatcher("/WEB-INF/system-error.jsp").forward(request,response);
                    }
                })
                .and()
                .csrf()                         // 防跨站请求伪造功能，关闭
                .disable()
                .formLogin()                    // 开启表单登录功能
                .loginPage("/admin/to/login/page.html")
                .loginProcessingUrl("/security/do/login.html")
                .defaultSuccessUrl("/admin/to/main/page.html")
                .usernameParameter("loginAcct")
                .passwordParameter("userPswd")
                .and()
                .logout()                       // 开启退出登录功能
                .logoutUrl("/seucrity/do/logout.html")
                .logoutSuccessUrl("/admin/to/login/page.html")
                ;
    }

}