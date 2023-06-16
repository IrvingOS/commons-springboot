package top.isopen.commons.springboot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 分页请求
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/6/8 9:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class PageRequest extends BaseRequest {

    private static final long serialVersionUID = -7859444924246432002L;

    private Integer current;
    private Integer size;

}
