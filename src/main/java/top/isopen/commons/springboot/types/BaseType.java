package top.isopen.commons.springboot.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 基础类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseType {

    private Date createTime;
    private Date updateTime;
    private Boolean deleted;

}
