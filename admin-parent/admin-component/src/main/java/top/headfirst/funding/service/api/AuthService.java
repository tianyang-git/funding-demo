package top.headfirst.funding.service.api;

import top.headfirst.funding.entity.Auth;

import java.util.List;
import java.util.Map;

public interface AuthService {

    /**
     * 获取所有权限
     * @return
     */
    List<Auth> getAll();

    /**
     * 根据角色 roleId 获取分配的权限
     * @param roleId
     * @return
     */
    List<Integer> getAssignedAuthIdByRoleId(Integer roleId);

    /**
     * 存储角色和权限的关系
     * @param map
     */
    void saveRoleAuthRelathinship(Map<String, List<Integer>> map);

    /**
     * 根据管理员查询已分配的权限
     * @param adminId
     * @return
     */
    public List<String> getAssignedAuthNameByAdminId(Integer adminId);
}
