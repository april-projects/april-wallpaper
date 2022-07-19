package com.mobaijun.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: Images
 * class description： 图片实体
 *
 * @author MoBaiJun 2022/7/15 12:13
 */
@Getter
@Setter
@ToString
public class Images {
    /**
     * 排序
     */
    private String desc;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 图片日期
     */
    private LocalDateTime dateTime;
}
