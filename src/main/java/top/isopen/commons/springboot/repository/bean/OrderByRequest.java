package top.isopen.commons.springboot.repository.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.isopen.commons.springboot.bean.BaseRequest;

/**
 * 排序请求
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/6/8 9:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderByRequest extends BaseRequest {

    private static final long serialVersionUID = -7859444924246432002L;

    private String column;
    private boolean asc;
    private int order = -1;

}
