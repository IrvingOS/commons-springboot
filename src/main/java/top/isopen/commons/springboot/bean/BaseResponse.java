package top.isopen.commons.springboot.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/10 16:15
 */
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = -37040625864832676L;

}
