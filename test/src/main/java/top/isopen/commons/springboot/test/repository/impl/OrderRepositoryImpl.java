package top.isopen.commons.springboot.test.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import top.isopen.commons.springboot.repository.AbstractRepository;
import top.isopen.commons.springboot.repository.types.OrderByList;
import top.isopen.commons.springboot.repository.types.QueryList;
import top.isopen.commons.springboot.test.dao.OrderMapper;
import top.isopen.commons.springboot.test.model.OrderModel;
import top.isopen.commons.springboot.test.repository.OrderRepository;
import top.isopen.commons.springboot.test.types.Order;
import top.isopen.commons.springboot.util.TypeUtil;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OrderRepositoryImpl extends AbstractRepository<Order, OrderModel> implements OrderRepository {

    @Resource
    private OrderMapper orderDAO;

    @Override
    public List<Order> listOrder(Order query, OrderByList<Order> orderByList) {
        LambdaQueryWrapper<OrderModel> queryWrapper = queryWrapper(query, orderByList);
        List<OrderModel> orderModelList = orderDAO.selectList(queryWrapper);
        return TypeUtil.transform(orderModelList, OrderModel::toType);
    }

    @Override
    public List<Order> listOrder(QueryList<Order> queryList, OrderByList<Order> orderByList) {
        LambdaQueryWrapper<OrderModel> queryWrapper = queryWrapper(queryList, orderByList);
        List<OrderModel> orderModelList = orderDAO.selectList(queryWrapper);
        return TypeUtil.transform(orderModelList, OrderModel::toType);
    }

}
