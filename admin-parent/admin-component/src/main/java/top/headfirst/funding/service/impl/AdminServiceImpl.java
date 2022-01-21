package top.headfirst.funding.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.entity.AdminExample;
import top.headfirst.funding.exception.LoginAcctAlreadyInUseException;
import top.headfirst.funding.exception.LoginAcctAlreadyInUseForUpdateException;
import top.headfirst.funding.exception.LoginFailedException;
import top.headfirst.funding.mapper.AdminMapper;
import top.headfirst.funding.service.api.AdminService;
import top.headfirst.funding.util.FundingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author tianyang
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Override
    public void saveAdmin(Admin admin) {
        // 密码加密处理
        String userPswd = admin.getUserPswd();
//        admin.setUserPswd(FundingUtil.md5(userPswd));
        userPswd = passwordEncoder.encode(userPswd);
        admin.setUserPswd(userPswd);
        // 创建时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
        String creatTime = simpleDateFormat.format(date.getTime());
        admin.setCreateTime(creatTime);

        try {
            adminMapper.insert(admin);
        } catch (Exception e) {
            logger.info("异常全类名="+e.getClass().getName());
            if(e instanceof DuplicateKeyException) {
                throw new LoginAcctAlreadyInUseException(FundingConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
            }
        }
//        throw new RuntimeException();
    }

    @Override
    public List<Admin> getAll() {
        return adminMapper.selectByExample(new AdminExample());
    }

    @Override
    public Admin getAdminByLoginAcct(String loginAcct, String userPswd) {
        // 调用 Mybatis 逆向工程的条件查询,并封装查询条件
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andLoginAcctEqualTo(loginAcct);

        List<Admin> adminList = adminMapper.selectByExample(adminExample);
        if (adminList == null || adminList.size() == 0) {
            // 用户名不存在，登录异常
            throw new LoginFailedException(FundingConstant.MESSAGE_LOGIN_FAILED);
        }
        if (adminList.size() > 1) {
            // 用户名重复
            throw new RuntimeException(FundingConstant.MESSAGE_SYSTEM_ERROR_LOGIN_NOT_UNIQUE);
        }
        Admin admin = adminList.get(0);
        if (admin == null) {
            // 用户不存在
            throw new LoginFailedException(FundingConstant.MESSAGE_LOGIN_FAILED);
        }
        String userPswdDB = admin.getUserPswd();
        // 加密输入的密码
        String userPswdFrom = FundingUtil.md5(userPswd);

        if (!Objects.equals(userPswdFrom,userPswdDB)){
            // 密码错误
            throw new LoginFailedException(FundingConstant.MESSAGE_LOGIN_FAILED);
        }
        // 密码正确
        return admin;
    }

    @Override
    public PageInfo<Admin> getPageInfo(String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Admin> list = adminMapper.selectAdminByKeyword(keyword);
        return new PageInfo<>(list);
    }

    @Override
    public Admin getAdminByadminId(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }

    @Override
    public void update(Admin admin) {
        try {
            // “Selective”表示有选择的更新，对于null值的字段不更新
            // "adminMapper.updateByPrimaryKey(admin);"
            adminMapper.updateByPrimaryKeySelective(admin);
        } catch (Exception e) {
            logger.info("异常全类名="+e.getClass().getName());
            if(e instanceof DuplicateKeyException) {
                throw new LoginAcctAlreadyInUseForUpdateException(FundingConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
            }
        }
    }

    @Override
    public void remove(Integer adminId) {
        adminMapper.deleteByPrimaryKey(adminId);
    }

    @Override
    public void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList) {
        adminMapper.deleteOldRelationship(adminId);
        if (roleIdList != null && roleIdList.size() > 0) {
            adminMapper.insertNewRelationship(adminId, roleIdList);
        }
    }

    @Override
    public Admin getAdminByLoginAcct(String username) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andLoginAcctEqualTo(username);
        List<Admin> adminList = adminMapper.selectByExample(adminExample);
        Admin admin = adminList.get(0);
        return admin;
    }
}
