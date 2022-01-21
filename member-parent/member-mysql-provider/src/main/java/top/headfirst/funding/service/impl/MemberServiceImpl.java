package top.headfirst.funding.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.headfirst.funding.entity.po.MemberPO;
import top.headfirst.funding.entity.po.MemberPOExample;
import top.headfirst.funding.mapper.MemberPOMapper;
import top.headfirst.funding.service.api.MemberService;

import javax.annotation.Resource;
import java.util.List;


// 在类上使用@Transactional(readOnly = true)针对查询操作设置事务属性
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {

    //@Autowired
    @Resource
    private MemberPOMapper memberPOMapper;

    @Override
    public MemberPO getMemberPOByLoginAcct(String loginacct) {

        MemberPOExample example = new MemberPOExample();

        MemberPOExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(loginacct);

        List<MemberPO> list = memberPOMapper.selectByExample(example);
        return list.get(0);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class,
            readOnly = false
    )
    @Override
    public void saveMember(MemberPO memberPO) {
        memberPOMapper.insert(memberPO);
    }
}
