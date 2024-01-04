package com.mobaijun;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.mobaijun.model.WallpaperData;
import com.mobaijun.util.FileUtils;
import com.mobaijun.util.JsonUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
    private static final String WALLPAPER_API = "https://wallhaven.cc/api/v1/search?sorting=toplist&ref=fp&atleast=1920x1080&ratios=16x9";
    /**
     * 当前日期作为分页参数
     */
    private static final String PAGE_API = WALLPAPER_API + "&page=" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static void main(String[] args) throws IOException {
        // Get a list of processed image collections
        List<WallpaperData> wallpaperData = JsonUtils.getWallpaperList(PAGE_API);
        FileUtils.writeWallpaper(wallpaperData);
        FileUtils.writeReadme(wallpaperData);
        FileUtils.writeMonthInfo(wallpaperData);
        FileUtils.determineSizeFile();
        // 写入到 json
        writeJsonFile(wallpaperData);
    }

    /**
     * 配置 json api
     *
     * @param wallpaperData 地址列表
     */
    private static void writeJsonFile(List<WallpaperData> wallpaperData) {
        // 写入url到json
        List<String> collect = wallpaperData
                .stream()
                .map(WallpaperData::getPath)
                .toList();
        String entries = JSONUtil.toJsonStr(collect);
        FileUtil.writeString(entries, Path.of("api.json").toFile(), StandardCharsets.UTF_8);
    }
}
