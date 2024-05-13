package com.mobaijun.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.mobaijun.model.WallpaperData;
import java.util.List;

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
}
