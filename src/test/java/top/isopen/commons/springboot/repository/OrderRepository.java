package top.isopen.commons.springboot.repository;

import top.isopen.commons.springboot.types.OrderByList;
import top.isopen.commons.springboot.types.Order;

import java.util.List;

public interface OrderRepository {

    List<Order> listOrder(Order query, OrderByList orderByList);

}