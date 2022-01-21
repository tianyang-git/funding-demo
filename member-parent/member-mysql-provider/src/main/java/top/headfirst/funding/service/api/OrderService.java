package top.headfirst.funding.service.api;

import top.headfirst.funding.entity.vo.AddressVO;
import top.headfirst.funding.entity.vo.OrderProjectVO;
import top.headfirst.funding.entity.vo.OrderVO;

import java.util.List;

public interface OrderService {
    OrderProjectVO getOrderProjectVO(Integer projectId, Integer returnId);

    void saveAddress(AddressVO addressVO);

    List<AddressVO> getAddressVOList(Integer memberId);

    void saveOrder(OrderVO orderVO);
}
