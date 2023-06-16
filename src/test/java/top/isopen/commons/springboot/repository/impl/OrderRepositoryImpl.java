package top.isopen.commons.springboot.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Repository;
import top.isopen.commons.springboot.types.OrderByList;
import top.isopen.commons.springboot.dao.OrderMapper;
import top.isopen.commons.springboot.model.OrderModel;
import top.isopen.commons.springboot.repository.AbstractRepository;
import top.isopen.commons.springboot.repository.OrderRepository;
import top.isopen.commons.springboot.types.Query;
import top.isopen.commons.springboot.enums.QueryTypeEnum;
import top.isopen.commons.springboot.types.Order;
import top.isopen.commons.springboot.util.TypeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepositoryImpl extends AbstractRepository<Order, OrderModel> implements OrderRepository {

    @Resource
    private OrderMapper orderDAO;

    @Override
    protected List<Query<OrderModel>> buildQueryEntity(Order query) {
        List<Query<OrderModel>> result = new ArrayList<>();
        if (query.getTransactionId() != null) {
            result.add(Query.<OrderModel>builder().type(QueryTypeEnum.EQ).fieldFunc(OrderModel::getTransactionId).value(query.getTransactionId()).build());
        }
        if (query.getSubscribeId() != null) {
            result.add(Query.<OrderModel>builder().type(QueryTypeEnum.EQ).fieldFunc(OrderModel::getSubscribeId).value(query.getSubscribeId()).build());
        }
        if (query.getStatus() != null) {
            result.add(Query.<OrderModel>builder().type(QueryTypeEnum.EQ).fieldFunc(OrderModel::getStatus).value(query.getStatus()).build());
        }
        return result;
    }

    @Override
    public List<Order> listOrder(Order query, OrderByList orderByList) {
        QueryWrapper<OrderModel> queryWrapper = queryWrapper(query, orderByList);
        List<OrderModel> orderModelList = orderDAO.selectList(queryWrapper);
        System.out.println(orderModelList);
        return TypeUtil.transform(orderModelList, OrderModel::toEntity);
    }

}
