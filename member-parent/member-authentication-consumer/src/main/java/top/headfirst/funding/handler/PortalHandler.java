package top.headfirst.funding.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import top.headfirst.funding.api.MySQLRemoteService;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.entity.vo.PortalTypeVO;
import top.headfirst.funding.util.FundingUtil;
import top.headfirst.funding.util.ResultEntity;

import java.util.List;

@Controller
public class PortalHandler {

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/")
    public String showPortalPage(Model model){
        // 1、调用MySQLRemoteService提供的方法查询首页要显示的数据
        ResultEntity<List<PortalTypeVO>> portalTypeProjectData = mySQLRemoteService.getPortalTypeProjectDataRemote();
        // 2.检查查询结果
        String result = portalTypeProjectData.getResult();
        if(ResultEntity.SUCCESS.equals(result)){
            // 3.获取查询结果数据
            List<PortalTypeVO> portalTypeVOList = portalTypeProjectData.getData();
            // 4.存入模型
            model.addAttribute(FundingConstant.ATTR_NAME_PORTAL_DATA, portalTypeVOList);
        }
        return "portal";
    }
}
