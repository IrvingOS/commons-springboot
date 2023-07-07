package top.isopen.commons.springboot.test.repository;

import top.isopen.commons.springboot.repository.types.OrderByList;
import top.isopen.commons.springboot.repository.types.QueryList;
import top.isopen.commons.springboot.test.types.Order;

import java.util.List;

public interface OrderRepository {

    List<Order> listOrder(Order query, OrderByList<Order> orderByList);

    List<Order> listOrder(QueryList<Order> queryList, OrderByList<Order> orderByList);

}