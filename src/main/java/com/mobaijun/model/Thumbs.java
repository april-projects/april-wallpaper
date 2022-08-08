package com.mobaijun.model;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: WallpaperThumbs
 * class description：略缩图
 *
 * @author MoBaiJun 2022/7/15 14:55
 */
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

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    @Override
    public String toString() {
        return "Thumbs{" +
                "large='" + large + '\'' +
                ", original='" + original + '\'' +
                ", small='" + small + '\'' +
                '}';
    }
}
