package top.isopen.commons.springboot.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author TimeChaser
 * @version 1.0
 * @since 2023/5/10 16:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 439154896956826690L;

    private Long current;
    private Long size;
    private Long total;
    private List<T> data;

    public static <T> PageResponse<T> of(Long current, Long size, Long total, List<T> data) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(current);
        pageResponse.setSize(size);
        pageResponse.setTotal(total);
        pageResponse.setData(data);
        return pageResponse;
    }

}
