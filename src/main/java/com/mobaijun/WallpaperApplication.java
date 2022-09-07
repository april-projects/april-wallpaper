package com.mobaijun;

import com.mobaijun.model.WallpaperData;
import com.mobaijun.util.FileUtils;
import com.mobaijun.util.JsonUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: WallpaperApplication
 * class description： 壁纸
 *
 * @author MoBaiJun 2022/7/15 11:36
 */
public class WallpaperApplication {

    /**
     * <a href="https://wallhaven.cc/help/api">WALLPAPER_API</a>
     */
    private static final String WALLPAPER_API = "https://wallhaven.cc/api/v1/search?sorting=toplist&atleast=1920x1080&ref=fp";
    /**
     * 当前日期作为分页参数
     */
    private static final String PAGE_API = WALLPAPER_API + "&page=" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static void main(String[] args) throws IOException {
        // Get a list of processed image collections
        List<WallpaperData> wallpaperData = FileUtils.writeWallpaper(JsonUtils.getWallpaperList(PAGE_API));
        FileUtils.writeReadme(wallpaperData);
        FileUtils.writeMonthInfo(wallpaperData);
    }
}
