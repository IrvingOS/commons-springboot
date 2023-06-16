package top.isopen.commons.springboot.bean;

import junit.framework.TestCase;
import top.isopen.commons.springboot.types.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderByRequestTest extends TestCase {

    void testTransformToType() {
        List<OrderByRequest> orderByRequestList = new ArrayList<>();
        orderByRequestList.add(new OrderByRequest("cole", true));
        List<OrderBy> collect = orderByRequestList.stream().map(orderByRequest -> OrderBy.builder().build()).collect(Collectors.toList());
    }

}