package com.mobaijun.util;

import cn.hutool.http.Header;
import com.mobaijun.common.util.ObjectUtils;
import com.mobaijun.common.util.PrintUtils;
import com.mobaijun.model.Images;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: JsoupUtils
 * class description： jsoup 工具类
 *
 * @author MoBaiJun 2022/7/15 11:39
 */
public class JsoupUtils {

    /**
     * 报头
     */
    private static final String VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36";

    /**
     * 目标地址
     */
    private static final String PATTERN = "https://wallhaven.cc/w/.*";

    /**
     * 获取图片集合
     *
     * @param wallpaperApi 图片 API
     * @return 图片集合
     */
    public static List<Images> getImageList(String wallpaperApi) {
        AtomicReference<Document> document = new AtomicReference<>();
        List<Images> imagesList = new LinkedList<>();
        try {
            // 得到 Document 对象
            document.set(getConnection(wallpaperApi).get());
            // 查找所有 img 标签集合
            Elements imgList = document.get().getElementsByTag("a");
            imgList.stream()
                    // 获取每个img标签URL "abs:" 表示绝对路径|过滤无效url
                    .filter(element -> Pattern.matches(PATTERN, element.attr("abs:href")))
                    .forEach(element -> {
                        try {
                            document.set(getConnection(element.attr("abs:href")).timeout(10 * 1000).get());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Elements elements = document.get().getElementsByTag("img");
                        Images images = new Images();
                        elements.parallelStream()
                                // 图片链接不能null
                                .filter(e -> !ObjectUtils.isEmpty(e.getElementById("wallpaper")))
                                .forEach(e -> {
                                    // 存储图片地址
                                    images.setImageUrl((e.attr("abs:src")));
                                    // 写入集合
                                    imagesList.add(images);
                                });
                    });
        } catch (Exception e) {
            PrintUtils.print(e.getMessage(), "image url get error");
        }
        PrintUtils.println(imagesList);
        return imagesList;
    }

    /**
     * 获取报头
     *
     * @param wallpaperApi url api
     * @return 连接信息
     */
    private static Connection getConnection(String wallpaperApi) {
        // 伪装报头
        return Jsoup.connect(wallpaperApi)
                .header(Header.USER_AGENT.getValue(), VALUE);
    }
}
