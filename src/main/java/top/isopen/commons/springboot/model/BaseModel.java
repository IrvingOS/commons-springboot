package top.isopen.commons.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;


/**
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/10 16:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = -37040625864832676L;

    private Date createTime;
    private Date updateTime;
    private Boolean deleted;

}
