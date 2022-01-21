package top.headfirst.funding.service.api;

import top.headfirst.funding.entity.vo.DetailProjectVO;
import top.headfirst.funding.entity.vo.PortalTypeVO;
import top.headfirst.funding.entity.vo.ProjectVO;

import java.util.List;

public interface ProjectService {
    void saveProject(ProjectVO projectVO, Integer memberId);

    List<PortalTypeVO> getPortalTypeVO();

    DetailProjectVO getDetailProjectVO(Integer projectId);
}
