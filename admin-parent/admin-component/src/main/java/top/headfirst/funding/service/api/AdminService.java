package top.headfirst.funding.service.api;

import com.github.pagehelper.PageInfo;
import top.headfirst.funding.entity.Admin;

import java.util.List;

/**
 * @author tianyang
 */
public interface AdminService {
    /**
     * 存储 admin
     * @param admin
     */
    void saveAdmin(Admin admin);

    /**
     * 获取所有的 amdin
     * @return
     */
    List<Admin> getAll();

    /**
     * 根据用户名和密码查询
     * @param loginAcct
     * @param userPswd
     * @return
     */
    Admin getAdminByLoginAcct(String loginAcct, String userPswd);

    /**
     * 根据关键词，页码，每页数返回分页对象
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Admin> getPageInfo(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 根据 id 查询
     * @param adminId
     * @return
     */
    Admin getAdminByadminId(Integer adminId);

    /**
     * 更新
     * @param admin
     */
    void update(Admin admin);

    /**
     * 根据 id 删除
     * @param adminId
     */
    void remove(Integer adminId);

    /**
     * 分配管理员和对应的角色
     * @param adminId
     * @param roleIdList
     */
    void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList);

    /**
     *
     * @param username
     * @return
     */
    public Admin getAdminByLoginAcct(String username);
}
