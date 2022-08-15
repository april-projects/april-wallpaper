package com.mobaijun.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.log.Log;
import com.mobaijun.enums.NumberEnums;
import com.mobaijun.model.Thumbs;
import com.mobaijun.model.WallpaperData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: FileUtils
 * class description： 文件操作工具类
 *
 * @author MoBaiJun 2022/7/15 13:28
 */
public class FileUtils {

    /**
     * tools log
     */
    private static final Log log = Log.get(FileUtils.class);
    private final static Path README_PATH = Paths.get("README.md");
    private final static Path WALLPAPER_PATH = Paths.get("wallpaper.md");
    private final static Path MONTH_PATH = Paths.get("picture/");
    private final static String REPO_URL = "[%s](https://github.com/april-projects/april-wallpaper/tree/main/picture/%s/) | ";

    /**
     * 读取 wallpaper.md
     *
     * @return WallpaperData 集合
     * @throws IOException IOException
     */
    public static List<WallpaperData> readWallpaperData() throws IOException {
        if (!Files.exists(WALLPAPER_PATH)) {
            Files.createFile(WALLPAPER_PATH);
        }
        List<String> allLines = Files.readAllLines(WALLPAPER_PATH);
        allLines = allLines.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<WallpaperData> wallpaperDataList = new ArrayList<>();
        for (int i = 1; i < allLines.size(); i++) {
            String s = allLines.get(i).trim();
            String date = s.substring(0, 19);
            // 略缩图地址
            String thumbs = getThumbs(s);
            // 4K地址
            String download4kUrl = getDownload4kUrl(s);
            // Image Name
            String wallId = getWallpaperId(s);
            wallpaperDataList.add(setWallpaper(wallId, date, download4kUrl, thumbs));
        }
        return wallpaperDataList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 写入 wallpaper.md
     *
     * @param wallpaperDataList 图片集合
     * @throws IOException IOException
     */
    public static void writeWallpaper(List<WallpaperData> wallpaperDataList) throws IOException {
        if (!Files.exists(WALLPAPER_PATH)) {
            Files.createFile(WALLPAPER_PATH);
        }
        // 扫描本地文件
        List<WallpaperData> data = readWallpaperData();
        if (CollUtil.isNotEmpty(data)) {
            wallpaperDataList.addAll(data);
        }
        // 排序
        List<WallpaperData> collect = wallpaperDataList.stream()
                .sorted(Comparator.comparing(WallpaperData::getCreatedAt))
                .collect(Collectors.toList());
        Files.write(WALLPAPER_PATH, "## Wallpaper".getBytes());
        Files.write(WALLPAPER_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        collect.forEach(wallpaperData -> {
            try {
                Files.write(WALLPAPER_PATH, wallpaperData.formatMarkdown().getBytes(), StandardOpenOption.APPEND);
                Files.write(WALLPAPER_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
                Files.write(WALLPAPER_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error(e.getMessage(), "Failed to write wallpaper.md file");
            }
        });
    }

    /**
     * 写入 README.md
     *
     * @param wallpaperDataList 图片集合
     * @throws IOException IOException
     */
    public static void writeReadme(List<WallpaperData> wallpaperDataList) throws IOException {
        if (!Files.exists(README_PATH)) {
            Files.createFile(README_PATH);
        }
        // 写入 readme
        writeFile(README_PATH, wallpaperDataList.subList(0, 24), null);

        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        // 归档
        Files.write(README_PATH, "### 历史归档：".getBytes(), StandardOpenOption.APPEND);
        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        List<String> dateList = wallpaperDataList.stream().map(WallpaperData::getCreatedAt).map(date -> date.substring(0, 7)).distinct().collect(Collectors.toList());
        int i = 0;
        for (String date : dateList) {
            String link = String.format(REPO_URL, date, date);
            Files.write(README_PATH, link.getBytes(), StandardOpenOption.APPEND);
            i++;
            if (i % 8 == 0) {
                Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
        }
    }

    /**
     * 按月份写入图片信息
     *
     * @param wallpaperDataList 图片集合
     */
    public static void writeMonthInfo(List<WallpaperData> wallpaperDataList) {
        Map<String, List<WallpaperData>> monthMap = new LinkedHashMap<>();
        wallpaperDataList.forEach(wallpaperData -> {
            String key = wallpaperData.getCreatedAt().substring(0, 7);
            if (monthMap.containsKey(key)) {
                monthMap.get(key).add(wallpaperData);
            } else {
                ArrayList<WallpaperData> list = new ArrayList<>();
                list.add(wallpaperData);
                monthMap.put(key, list);
            }
        });
        monthMap.keySet().forEach(s -> {
            // 创建文件夹
            Path path = MONTH_PATH.resolve(s);
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                    path = path.resolve("README.md");
                    writeFile(path, monthMap.get(s), s);
                } catch (IOException e) {
                    log.error(e.getMessage(), "Failed to write README.MD file");
                }
            } else {
                try {
                    path = path.resolve("README.md");
                    writeFile(path, monthMap.get(s), s);
                } catch (IOException e) {
                    log.error(e.getMessage(), "Failed to write README.MD file");
                }
            }
        });
    }

    /**
     * 写入文件处理
     *
     * @param path              路径
     * @param wallpaperDataList 图片集合
     * @param name              标题
     * @throws IOException IOException
     */
    private static void writeFile(Path path, List<WallpaperData> wallpaperDataList, String name) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        String title = "## Wallpaper";
        if (name != null) {
            title = "## Wallpaper (" + name + ")";
        }
        Files.write(path, title.getBytes());
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, wallpaperDataList.get(NumberEnums.ZERO.getCode()).toLarge().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "|      |      |      |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "| :----: | :----: | :----: |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        final int[] i = {NumberEnums.ONE.getCode()};
        wallpaperDataList.forEach(wallpaperData -> {
            try {
                Files.write(path, ("|" + wallpaperData.toString()).getBytes(), StandardOpenOption.APPEND);
                if (i[0] % NumberEnums.THREE.getCode() == 0) {
                    Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
                    Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
                }
                i[0]++;
            } catch (IOException e) {
                log.error(e.getMessage(), "file write failed error");
            }
        });
        if (i[0] % NumberEnums.THREE.getCode() != 1) {
            Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 写入 wallpaper 数据
     */
    private static WallpaperData setWallpaper(String id, String date, String url, String path) {
        Thumbs thumbs = new Thumbs();
        thumbs.setSmall(path);
        WallpaperData wallpaperData = new WallpaperData();
        wallpaperData.setUrl(url);
        wallpaperData.setCreatedAt(date);
        wallpaperData.setThumbs(thumbs);
        wallpaperData.setPath(path);
        wallpaperData.setId(id);
        return wallpaperData;
    }

    /**
     * 截取字符获取指定链接
     *
     * @param url 字符串
     * @return url
     */
    private static String getDownload4kUrl(String url) {
        String index = url.substring(url.indexOf("]") - 2);
        return index.substring(4, index.lastIndexOf("|") - 2);
    }

    /**
     * 获取 wallpaper id
     *
     * @param url 字符串
     * @return url
     */
    private static String getWallpaperId(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length() - 5);
    }

    /**
     * 获取略缩图
     *
     * @param url 字符串
     * @return url
     */
    private static String getThumbs(String url) {
        return url.substring(url.lastIndexOf("]") + 2, url.length() - 1);
    }
}
