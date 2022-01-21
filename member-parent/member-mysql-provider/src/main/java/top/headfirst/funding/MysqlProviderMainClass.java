package top.headfirst.funding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 扫描 MyBatis 的 Mapper 接口所在包
@MapperScan("top.headfirst.funding.mapper")
@SpringBootApplication
public class MysqlProviderMainClass {
    public static void main(String[] args) {
        SpringApplication.run(MysqlProviderMainClass.class, args);
    }
}
