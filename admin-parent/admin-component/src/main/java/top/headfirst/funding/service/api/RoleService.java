package top.headfirst.funding.service.api;

import com.github.pagehelper.PageInfo;
import top.headfirst.funding.entity.Admin;
import top.headfirst.funding.entity.Role;

import java.util.List;

public interface RoleService {

    /**
     * 根据关键词，页码，每页数进行查询
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Role> getPageInfo(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 存储 role
     * @param role
     */
    void saveRole(Role role);

    /**
     * 更新 role
     * @param role
     */
    void updateRole(Role role);

    /**
     * 根据 roleId 删除
     * @param roleIdList
     */
    void removeRole(List<Integer> roleIdList);

    /**
     * 根据管理员 id 获取角色的已分配角色
     * @param adminId
     * @return
     */
    List<Role> getAssignedRole(Integer adminId);

    /**
     * 根据管理员 id 获取角色的未分配角色
     * @param adminId
     * @return
     */
    List<Role> getUnAssignedRole(Integer adminId);
}
