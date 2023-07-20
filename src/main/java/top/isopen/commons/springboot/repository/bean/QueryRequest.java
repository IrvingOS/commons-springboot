package top.isopen.commons.springboot.repository.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.isopen.commons.springboot.bean.BaseRequest;

import java.util.List;

/**
 * 复杂查询请求
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueryRequest extends BaseRequest {

    private static final long serialVersionUID = -7859444924246432002L;

    /**
     * 查询列名，需与 Model 层中的列明大小写保持一致
     *
     * @since 2023/7/7 16:51
     */
    private String column;
    /**
     * 查询类型，大小写无关
     * <p>
     * 见 {@link top.isopen.commons.springboot.repository.enums.QueryTypeEnum#value}
     *
     * @since 2023/7/7 16:51
     */
    private String type;
    /**
     * 查询值
     *
     * @since 2023/7/7 16:52
     */
    private Object value;
    /**
     * 子查询
     *
     * @since 2023/7/20 11:12
     */
    private List<QueryRequest> subQuery;

}
