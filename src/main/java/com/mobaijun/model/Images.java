package com.mobaijun.model;

import java.time.LocalDateTime;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: Images
 * class description： 图片实体
 *
 * @author MoBaiJun 2022/7/15 12:13
 */
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Images{" +
                "desc='" + desc + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
