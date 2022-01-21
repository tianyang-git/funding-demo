package top.headfirst.funding.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.headfirst.funding.entity.po.AddressPO;
import top.headfirst.funding.entity.po.AddressPOExample;
import top.headfirst.funding.entity.po.OrderPO;
import top.headfirst.funding.entity.po.OrderProjectPO;
import top.headfirst.funding.entity.vo.AddressVO;
import top.headfirst.funding.entity.vo.OrderProjectVO;
import top.headfirst.funding.entity.vo.OrderVO;
import top.headfirst.funding.mapper.AddressPOMapper;
import top.headfirst.funding.mapper.OrderPOMapper;
import top.headfirst.funding.mapper.OrderProjectPOMapper;
import top.headfirst.funding.service.api.OrderService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    //@Autowired
    @Resource
    private OrderProjectPOMapper orderProjectPOMapper;

    //@Autowired
    @Resource
    private OrderPOMapper orderPOMapper;

    //@Autowired
    @Resource
    private AddressPOMapper addressPOMapper;

    @Override
    public OrderProjectVO getOrderProjectVO(Integer projectId, Integer returnId) {
        return orderProjectPOMapper.selectOrderProjectVO(returnId);
    }

    @Transactional(
            readOnly = false,
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class
    )
    @Override
    public void saveAddress(AddressVO addressVO) {
        AddressPO addressPO = new AddressPO();
        BeanUtils.copyProperties(addressVO,addressPO);
        addressPOMapper.insert(addressPO);
    }


    @Override
    public List<AddressVO> getAddressVOList(Integer memberId) {
        AddressPOExample addressPOExample = new AddressPOExample();
        addressPOExample.createCriteria().andMemberIdEqualTo(memberId);
        List<AddressPO> addressPOList = addressPOMapper.selectByExample(addressPOExample);
        List<AddressVO> addressVOList = new ArrayList<AddressVO>();

        for (AddressPO addressPO:
             addressPOList) {
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(addressPO,addressVO);
            addressVOList.add(addressVO);
        }
        return addressVOList;
    }

    @Transactional(
            readOnly = false,
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class)
    @Override
    public void saveOrder(OrderVO orderVO) {
        OrderPO orderPO = new OrderPO();
        BeanUtils.copyProperties(orderVO,orderPO);
        OrderProjectPO orderProjectPO = new OrderProjectPO();
        BeanUtils.copyProperties(orderVO.getOrderProjectVO(),orderProjectPO);

        // 保存orderPO自动生成的主键是orderProjectPO需要用到的外键
        orderPOMapper.insert(orderPO);
        // 从orderPO中获取orderId
        Integer id = orderPO.getId();
        orderProjectPO.setOrderId(id);

        orderProjectPOMapper.insert(orderProjectPO);
    }
}
