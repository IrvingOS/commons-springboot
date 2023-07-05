package top.isopen.commons.springboot.test.repository;

import top.isopen.commons.springboot.test.model.OrderModel;
import top.isopen.commons.springboot.test.types.Order;
import top.isopen.commons.springboot.types.OrderByList;

import java.util.List;

public interface OrderRepository {

    List<Order> listOrder(Order query, OrderByList<OrderModel> orderByList);

}