package top.headfirst.funding.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.entity.Role;
import top.headfirst.funding.service.api.AdminService;
import top.headfirst.funding.service.api.AuthService;
import top.headfirst.funding.service.api.RoleService;

import java.util.ArrayList;
import java.util.List;

@Component
public class FundingUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthService authService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据账号名，获取 Admin 对象
        Admin admin = adminService.getAdminByLoginAcct(username);
        // 获取 adminId
        Integer adminId = admin.getId();
        // 获得角色信息
        List<Role> assignedRoleList = roleService.getAssignedRole(adminId);
        // 获得权限信息
        List<String> authNameList = authService.getAssignedAuthNameByAdminId(adminId);
        // 创建集合对象，存储 GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 遍历角色信息，存入 GrantedAuthority 集合
        for (Role role:
             assignedRoleList) {
            String roleName = "ROLE_" + role.getName();

            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleName);
            authorities.add(simpleGrantedAuthority);
        }
        // 遍历权限信息，存入 GrantedAuthority 集合
        for (String authName:
             authNameList) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authName);
            authorities.add(simpleGrantedAuthority);
        }
        // 封装SecurityAdmin对象
        SecurityAdmin securityAdmin = new SecurityAdmin(admin, authorities);
        return securityAdmin;
    }
}
