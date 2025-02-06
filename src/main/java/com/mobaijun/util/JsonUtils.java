package com.mobaijun.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.mobaijun.model.WallpaperData;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * software：IntelliJ IDEA 2022.1
 * class name: JsonUtils
 * class description： json 工具类
 *
 * @author MoBaiJun 2022/7/15 14:00
 */
public class JsonUtils {

    /**
     * tools log
     */
    private static final Log log = Log.get(JsonUtils.class);

    /**
     * 请求 api 返回数据集
     *
     * @param api 请求 api
     * @return 数据集
     */
    public static List<WallpaperData> getWallpaperList(String api, Boolean isProxy) {
        log.info("当前请求 api 地址：{}", api);
        HttpRequest request = HttpUtil.createGet(api);
        // 检查是否开启代理
        if (isProxy) {
            request.setHttpProxy("127.0.0.1", 10809);
        }
        // 返回 json 字符串
        String jsonStr = request.execute().body();
        JSONArray jsonArray = JSONUtil.parseObj(jsonStr).getJSONArray("data");
        return JSONUtil.toList(jsonArray, WallpaperData.class);
    }

    /**
     * 写入 api 文件
     *
     * @param wallpaperData 数据
     */
    public static void writerApiFiles(List<WallpaperData> wallpaperData) {
        // 按月份分组
        Map<String, Set<String>> groupedData = new HashMap<>();

        for (WallpaperData data : wallpaperData) {
            String monthKey = DateUtil.format(DateUtil.parse(data.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"), "yyyy-M");
            groupedData.computeIfAbsent(monthKey, k -> new HashSet<>()).add(data.getPath());
        }

        // 处理每个月的数据
        groupedData.forEach((month, newData) -> {
            String fileName = "api/" + month + "-api.json";
            File file = new File(fileName);
            Set<String> existingData = new HashSet<>();
            // 读取已有的数据
            if (file.exists()) {
                try {
                    String content = FileUtil.readUtf8String(file);
                    JSONArray jsonArray = JSONUtil.parseArray(content);
                    existingData.addAll(jsonArray.toList(String.class));
                } catch (Exception e) {
                    log.error("读取文件失败：" + e.getMessage());
                }
            }
            // 合并去重后写回文件
            existingData.addAll(newData);
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(existingData), file);
        });
    }
}
