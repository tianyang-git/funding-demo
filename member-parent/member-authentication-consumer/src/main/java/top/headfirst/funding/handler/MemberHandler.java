package top.headfirst.funding.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.headfirst.funding.api.MySQLRemoteService;
import top.headfirst.funding.api.RedisRemoteService;
import top.headfirst.funding.config.ShortMessageProperties;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.entity.po.MemberPO;
import top.headfirst.funding.entity.vo.MemberLoginVO;
import top.headfirst.funding.entity.vo.MemberVO;
import top.headfirst.funding.util.FundingUtil;
import top.headfirst.funding.util.ResultEntity;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
public class MemberHandler {

    @Autowired
    private ShortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    private Logger logger = LoggerFactory.getLogger(MemberHandler.class);

    @RequestMapping("/auth/member/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:http://localhost/";
    }

    @RequestMapping("/auth/member/do/login")
    public String login(
            @RequestParam("loginacct") String loginacct,
            @RequestParam("userpswd") String userpswd,
            HttpSession session,
            Model model){
        ResultEntity<MemberPO> resultEntity = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
        if (resultEntity.FAILED.equals(resultEntity.getResult())){
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
            return "member-login";
        }

        MemberPO memberPO = resultEntity.getData();
        if (memberPO == null){
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        String userpswdDataBase = memberPO.getUserpswd();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(userpswd, userpswdDataBase);
        if (!matches){
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 创建MemberLoginVO对象存入Session域
        MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getId(), memberPO.getUsername(), memberPO.getEmail());
        session.setAttribute(FundingConstant.ATTR_NAME_LOGIN_MEMBER,memberLoginVO);
        return "redirect:http://localhost/auth/member/to/center/page";
    }

    @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, Model model){
        // 获取手机号
        String phone_number = memberVO.getPhone_number();
        // 从Redis读取Key对应的value
        String key = FundingConstant.REDIS_CODE_PREFIX + phone_number;
        System.out.println("key=" + key);
        // 从Redis读取Key对应的value
        ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKeyRemote(key);

        // 检查查询操作是否有效
        String result = resultEntity.getResult();
        System.out.println("result=" + result);
        if (ResultEntity.FAILED.equals(result)) {
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
            return "member-reg";
        }

        String redisCode = resultEntity.getData();
        System.out.println("redisCode=" + redisCode);
        if (redisCode == null) {
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_CODE_NOT_EXISTS);
            return "member-reg";
        }

        // 如果从Redis能够查询到value则比较表单验证码和Redis验证码
        String formCode = memberVO.getCode();
        System.out.println(formCode);
        if (!Objects.equals(formCode, redisCode)) {
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,FundingConstant.MESSAGE_CODE_INVALID);
            return "member-reg";
        } else {
            // 如果验证码一致，则从Redis删除
            redisRemoteService.removeRedisKeyRemote(key);
        }

        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String userpswdBeforeEncode = memberVO.getUserpswd();
        String userpswdAfterEncode = passwordEncoder.encode(userpswdBeforeEncode);
        memberVO.setUserpswd(userpswdAfterEncode);

        // 执行保存
        // 创建空的MemberPO对象
        MemberPO memberPO = new MemberPO();
        BeanUtils.copyProperties(memberVO,memberPO);
        ResultEntity<String> saveMemberResultEntity = mySQLRemoteService.saveMember(memberPO);
        if (ResultEntity.FAILED.equals(saveMemberResultEntity.getResult())){
            model.addAttribute(FundingConstant.ATTR_NAME_MESSAGE,saveMemberResultEntity.getMessage());
            return "member-reg";
        }
        // 使用重定向避免刷新浏览器导致重新执行注册流程
        return "redirect:http://localhost/auth/member/to/login/page";
    }

    @ResponseBody
    @RequestMapping("/auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam("phone_number") String phone_number){
        // 1.发送验证码到phoneNum手机
        ResultEntity<String> sendMessageResultEntity = FundingUtil.sendCodeByShortMessage(
                shortMessageProperties.getHost(),
                shortMessageProperties.getPath(),
                shortMessageProperties.getMethod(),
                shortMessageProperties.getAppcode(),
                shortMessageProperties.getTemplate_id(),
                phone_number);

        // 2.判断短信发送结果
        if (ResultEntity.SUCCESS.equals(sendMessageResultEntity.getResult())) {
            // 3.如果发送成功，则将验证码存入Redis
            // ①从上一步操作的结果中获取随机生成的验证码
            String code = sendMessageResultEntity.getData();
            // ②拼接一个用于在Redis中存储数据的key
            String key = FundingConstant.REDIS_CODE_PREFIX + phone_number;
            // ③调用远程接口存入Redis
            ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, code, 15, TimeUnit.MINUTES);
            if (ResultEntity.SUCCESS.equals(saveCodeResultEntity.getResult())){
                return ResultEntity.successWithoutData();
            } else {
                return saveCodeResultEntity;
            }
        } else {
            return sendMessageResultEntity;
        }
    }
}
