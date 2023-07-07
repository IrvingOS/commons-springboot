package top.isopen.commons.springboot.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.isopen.commons.springboot.bean.BaseRequest;
import top.isopen.commons.springboot.repository.bean.OrderByRequest;
import top.isopen.commons.springboot.repository.bean.QueryRequest;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrmRequest extends BaseRequest {

    private List<QueryRequest> query;
    private List<OrderByRequest> orderBy;

}
