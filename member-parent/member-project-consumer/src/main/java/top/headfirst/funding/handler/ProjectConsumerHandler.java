package top.headfirst.funding.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.headfirst.funding.api.MySQLRemoteService;
import top.headfirst.funding.config.OSSProperties;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.entity.vo.*;
import top.headfirst.funding.util.FundingUtil;
import top.headfirst.funding.util.ResultEntity;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProjectConsumerHandler {

    @Autowired
    private OSSProperties ossProperties;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/get/project/detail/{projectId}")
    public String getProjectDetail(@PathVariable("projectId") Integer projectId, Model model){
        ResultEntity<DetailProjectVO> detailProjectVOResultEntity = mySQLRemoteService.getDetailProjectVORemote(projectId);
        String result = detailProjectVOResultEntity.getResult();
        if (ResultEntity.SUCCESS.equals(result)) {
            DetailProjectVO detailProjectVO = detailProjectVOResultEntity.getData();
            model.addAttribute("detailProjectVO",detailProjectVO);
        }
        return "project-show-detail";
    }



    @RequestMapping("/create/confirm")
    public String saveConfirm(Model model, HttpSession session, MemberConfirmInfoVO memberConfirmInfoVO) {
        // 1、从 session 域读取之前临时存储的 ProjectVO 对象
        ProjectVO projectVO = (ProjectVO)  session.getAttribute(FundingConstant.ATTR_NAME_TEMPLE_PROJECT);
        // 2、如果 projectVO 为 null
        if (projectVO == null){
            throw new RuntimeException(FundingConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
        }
        // 3.将确认信息数据设置到projectVO对象中
        projectVO.setMemberConfirmInfoVO(memberConfirmInfoVO);
        // 4.从Session域读取当前登录的用户
        MemberLoginVO memberLoginVO = (MemberLoginVO)session.getAttribute(FundingConstant.ATTR_NAME_LOGIN_MEMBER);
        Integer memberId = memberLoginVO.getId();
        // 5.调用远程方法保存projectVO对象
        ResultEntity<String> saveResultEntity = mySQLRemoteService.saveProjectVORemote(projectVO, memberId);
        // 6.判断远程的保存操作是否成功
        String result = saveResultEntity.getResult();
        if (ResultEntity.FAILED.equals(result)){
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE, saveResultEntity.getMessage());
            return "project-confirm";
        }
        // 7.将临时的ProjectVO对象从Session域移除
        session.removeAttribute(FundingConstant.ATTR_NAME_TEMPLE_PROJECT);
        // 8.如果远程保存成功则跳转到最终完成页面
        return "redirect:http://localhost/project/create/success";
    }

    /**
     * 收集回报信息 操作 2：接收整个回报信息数据
     * @param returnVO
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/create/save/return.json")
    public ResultEntity<String> saveReturn(ReturnVO returnVO, HttpSession session) {

        try {
            // 1.从session域中读取之前缓存的ProjectVO对象
            ProjectVO projectVO = (ProjectVO) session.getAttribute(FundingConstant.ATTR_NAME_TEMPLE_PROJECT);
            // 2.判断projectVO是否为null
            if (projectVO == null) {
                return ResultEntity.failed(FundingConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
            }
            // 3.从projectVO对象中获取存储回报信息的集合
            List<ReturnVO> returnVOList = projectVO.getReturnVOList();
            // 4.判断returnVOList集合是否有效
            if (returnVOList == null || returnVOList.size() == 0) {
                // 5.创建集合对象对returnVOList进行初始化
                returnVOList  = new ArrayList<>();
                // 6.为了让以后能够正常使用这个集合，设置到projectVO对象中
                projectVO.setReturnVOList(returnVOList);
            }
            // 7.将收集了表单数据的returnVO对象存入集合
            returnVOList.add(returnVO);
            // 8.把数据有变化的ProjectVO对象重新存入Session域，以确保新的数据最终能够存入Redis
            session.setAttribute(FundingConstant.ATTR_NAME_TEMPLE_PROJECT,projectVO);
            // 9.所有操作成功完成返回成功
            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    /**
     * 收集回报信息 操作 1：接收页面异步上传的图片
     * @param returnPicture
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/create/upload/return/picture.json")
    public ResultEntity<String> uploadReturnPicture(
            // 接收用户上传的文件
            @RequestParam("returnPicture") MultipartFile returnPicture) throws IOException {
        ResultEntity<String> uploadReturnPicResultEntity = FundingUtil.uploadFileToOss(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                returnPicture.getInputStream(),
                ossProperties.getBucketName(),
                ossProperties.getBucketDomain(),
                returnPicture.getOriginalFilename());
        return uploadReturnPicResultEntity;
    }

    /**
     * 接收表单数据
     * @param projectVO 接收除了上传图片之外的其他普通数据
     * @param headerPicture 接收上传的头图
     * @param detailPictureList 接收上传的详情图片
     * @param session 用来将收集了一部分数据的ProjectVO对象存入Session域
     * @param model 用来在当前操作失败后返回上一个表单页面时携带提示消息
     * @return
     */
    @RequestMapping("/create/project/information")
    public String saveProjectBasicInfo(
            ProjectVO projectVO,
            MultipartFile headerPicture,
            List<MultipartFile> detailPictureList,
            HttpSession session,
            Model model) throws IOException {
        // 一、完成头图上传
        // 1、如果没有上传头图则返回到表单页面并显示错误消息
        boolean headerPictureEmpty = headerPicture.isEmpty();
        if (headerPictureEmpty) {
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_HEADER_PIC_EMPTY);
            return "project-launch";
        }
        // 2、如果用户确实上传了有内容的文件，则执行上传
        ResultEntity<String> uploadHeaderPicResultEntity = FundingUtil.uploadFileToOss(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                headerPicture.getInputStream(),
                ossProperties.getBucketName(),
                ossProperties.getBucketDomain(),
                headerPicture.getOriginalFilename());
        String result = uploadHeaderPicResultEntity.getResult();

        // 3、判断是否上传成功
        if (ResultEntity.SUCCESS.equals(result)) {
            // 4、如果成功则从返回的数据中获取图片访问路径,
            String headerPicturePath = uploadHeaderPicResultEntity.getData();
            // 5、存入ProjectVO对象中
            projectVO.setHeaderPicturePath(headerPicturePath);
        } else {
            // 如果上传失败则返回到表单页面并显示错误消息
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_HEADER_PIC_UPLOAD_FAILED);
        }

        // 二、上传详情图片
        List<String> detailPicturePathList = new ArrayList<>();
        // 检查detailPictureList是否有效
        if (detailPictureList == null || detailPictureList.size() == 0) {
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_DETAIL_PIC_EMPTY);
            return "project-launch";
        }
        // 1、遍历detailPictureList集合
        for (MultipartFile detailPicture:
             detailPictureList) {
            // 2、检测当前 detailPicture 是否为空
            if (detailPicture.isEmpty()){
                model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE, FundingConstant.MESSAGE_DETAIL_PIC_EMPTY);
                return "project-launch";
            }
            // 3、执行上传
            ResultEntity<String> detailUploadResultEntity = FundingUtil.uploadFileToOss(ossProperties.getEndPoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret(),
                    detailPicture.getInputStream(),
                    ossProperties.getBucketName(),
                    ossProperties.getBucketDomain(),
                    detailPicture.getOriginalFilename());
            // 4、检查上传结果
            String detailUploadResult = detailUploadResultEntity.getResult();
            if (ResultEntity.SUCCESS.equals(detailUploadResult)){
                String detailPicturePath = detailUploadResultEntity.getData();
                detailPicturePathList.add(detailPicturePath);
            } else {
                model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE, FundingConstant.MESSAGE_DETAIL_PIC_UPLOAD_FAILED);
                return  "project-launch";
            }
        }
        // 5.将存放了详情图片访问路径的集合存入ProjectVO中
        projectVO.setDetailPicturePathList(detailPicturePathList);
        // 三、后续操作
        // 1.将ProjectVO对象存入Session域
        session.setAttribute(FundingConstant.ATTR_NAME_TEMPLE_PROJECT,projectVO);
        // 2.以完整的访问路径前往下一个收集回报信息的页面
        return "redirect:http://localhost/project/return/info/page";
    }
}
