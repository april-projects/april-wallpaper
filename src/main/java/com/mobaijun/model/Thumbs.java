package com.mobaijun.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: WallpaperThumbs
 * class description：略缩图
 *
 * @author MoBaiJun 2022/7/15 14:55
 */
@Getter
@Setter
@ToString
public class Thumbs {

    /**
     * 432 * 243
     */
    private String large;

    /**
     * 300 * 294
     */
    private String original;

    /**
     * 300 * 200
     */
    private String small;
}
