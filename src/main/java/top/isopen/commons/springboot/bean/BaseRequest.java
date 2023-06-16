package top.isopen.commons.springboot.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 反序列化依赖构造函数
 * <p>
 * 所以 BaseRequest 的子类需使用 @NoArgsConstructor 和 @AllArgsConstructor
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/12 10:22
 */
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseRequest implements Serializable {

    private static final long serialVersionUID = 2006033976690108451L;

}
