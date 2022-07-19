package com.mobaijun.model;

import lombok.Getter;
import lombok.Setter;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: WallpaperData
 * class description： json 对象
 *
 * @author MoBaiJun 2022/7/15 13:41
 */
@Getter
@Setter
public class WallpaperData {
    private String id;
    private String url;
    private String fileType;
    private String createdAt;
    private String path;
    private Thumbs thumbs;

    /**
     * 格式化 markdown 文本
     *
     * @return 字符串
     */
    public String formatMarkdown() {
        return String.format("%s | [%s](%s) ", createdAt, id, thumbs.getSmall());
    }

    /**
     * 生成 Readme 标题
     *
     * @return 字符串
     */
    public String toLarge() {
        return String.format("![%s](%s) Today: [%s](%s)", id, path, id, thumbs.getSmall());
    }

    /**
     * 生成空格内文本
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("![%s](%s)[%s download 4k](%s)", id, thumbs.getSmall(), id, url);
    }
}
