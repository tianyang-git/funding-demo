package top.headfirst.funding.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.entity.Role;
import top.headfirst.funding.mapper.AdminMapper;
import top.headfirst.funding.mapper.RoleMapper;
import top.headfirst.funding.service.api.AdminService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Spring 整合 junit4
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
        {"classpath:spring-persist-mybatis.xml","classpath:spring-persist-tx.xml"})

public class EnvironmentTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void testInsertRole(){
        for (int i = 1; i < 139; i++) {
            roleMapper.insert(new Role(null, "测试角色" + i));
        }
    }

    @Test
    public void testConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

    @Test
    public void testInsertAdmin(){
        Admin admin = new Admin(null, "tom", "123123", "汤姆", "tom@qq.com", "null");
        int count = adminMapper.insert(admin);
        System.out.println("受影响的行数" + count);
    }

    @Test
    public void textTransactionManager(){
        Admin admin = new Admin(null, "jerry", "123456", "杰瑞", "jerry@qq.com", null);
        adminService.saveAdmin(admin);
    }

    @Test
    public void addTestDate(){
        for (int i = 0; i < 239; i++) {
            adminMapper.insert(new Admin(null, "testAcct"+i, "testPswd"+i, "测试昵称"+i, "test@qq.com"+i, null));
        }
    }

}
