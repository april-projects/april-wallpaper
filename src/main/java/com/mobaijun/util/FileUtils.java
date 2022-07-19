package com.mobaijun.util;

import com.mobaijun.common.util.PrintUtils;
import com.mobaijun.common.util.constant.NumberConstant;
import com.mobaijun.model.WallpaperData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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

    private final static Path README_PATH = Paths.get("README.md");
    private final static Path BING_PATH = Paths.get("wallpaper.md");
    private final static Path MONTH_PATH = Paths.get("picture/");
    private final static String REPO_URL = "[%s](https://github.com/april-projects/april-wallpaper/tree/main/picture/%s/) | ";

    /**
     * 写入 wallpaper.md
     *
     * @param wallpaperDataList 图片集合
     * @throws IOException IOException
     */
    public static void writeWallpaper(List<WallpaperData> wallpaperDataList) throws IOException {
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        Files.write(BING_PATH, "## Wallpaper".getBytes());
        Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        wallpaperDataList.forEach(wallpaperData -> {
            try {
                Files.write(BING_PATH, wallpaperData.formatMarkdown().getBytes(), StandardOpenOption.APPEND);
                Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
                Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                PrintUtils.print(e.getMessage(), "Failed to write wallpaper.md file");
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
        List<WallpaperData> imagesList = wallpaperDataList.subList(0, wallpaperDataList.size() - 1);
        writeFile(README_PATH, imagesList, null);

        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        // 归档
        Files.write(README_PATH, "### 历史归档：".getBytes(), StandardOpenOption.APPEND);
        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        List<String> dateList = wallpaperDataList
                .stream()
                .map(WallpaperData::getCreatedAt)
                .map(date -> date.substring(0, 7))
                .distinct()
                .collect(Collectors.toList());
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
            Path path = MONTH_PATH.resolve(s);
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                    path = path.resolve("README.md");
                    writeFile(path, monthMap.get(s), s);
                } catch (IOException e) {
                    PrintUtils.print(e.getMessage(), "Failed to write README.MD file");
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
        Files.write(path, wallpaperDataList.get(NumberConstant.ZERO).toLarge().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "|      |      |      |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "| :----: | :----: | :----: |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        final int[] i = {NumberConstant.ONE};
        wallpaperDataList.forEach(wallpaperData -> {
            try {
                Files.write(path, ("|" + wallpaperData.toString()).getBytes(), StandardOpenOption.APPEND);
                if (i[0] % NumberConstant.THREE == 0) {
                    Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
                    Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
                }
                i[0]++;
            } catch (IOException e) {
                PrintUtils.print(e.getMessage(), "file write failed error");
            }
        });
        if (i[0] % NumberConstant.THREE != 1) {
            Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
        }
    }
}
