const https = require('https');
const fs = require('fs');

// JSON 数据的 URL
const JSON_URL = 'https://cdn.jsdelivr.net/gh/april-projects/april-wallpaper/api.json';
// 缓存文件路径
const CACHE_FILE_PATH = 'cache.json';
// 缓存过期时间（单位：毫秒）  24 小时
const CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000;

// 发起 HTTPS 请求获取 JSON 数据
function fetchData(url, callback) {
    https.get(url, (res) => {
        let data = '';
        res.on('data', (chunk) => {
            data += chunk;
        });
        res.on('end', () => {
            callback(null, JSON.parse(data));
        });
    }).on('error', (err) => {
        callback(err, null);
    });
}

// 从缓存中读取数据
function readCache(callback) {
    fs.readFile(CACHE_FILE_PATH, 'utf8', (err, data) => {
        if (err) {
            callback(err, null);
            return;
        }
        try {
            const cache = JSON.parse(data);
            callback(null, cache);
        } catch (parseErr) {
            callback(parseErr, null);
        }
    });
}

// 将数据写入缓存
function writeCache(data, callback) {
    fs.writeFile(CACHE_FILE_PATH, JSON.stringify(data), 'utf8', callback);
}

// 判断缓存是否过期
function isCacheExpired(cache) {
    return Date.now() - cache.timestamp > CACHE_EXPIRY_TIME;
}

// 分页函数，根据页码和每页大小来返回对应的数据
function paginate(data, page, pageSize) {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return data.slice(startIndex, endIndex);
}

const http = require('http');

const server = http.createServer((req, res) => {
    // 设置响应头
    res.setHeader('Content-Type', 'application/json');
    res.setHeader('Access-Control-Allow-Origin', '*');

    // 解析 URL 中的分页参数
    const url = new URL(req.url, `http://${req.headers.host}`);
    // 默认页码为第一页
    const page = parseInt(url.searchParams.get('page')) || 1;
    // 默认每页大小为10
    const pageSize = parseInt(url.searchParams.get('pageSize')) || 10;

    // 获取客户端 IP 地址
    const clientIP = req.connection.remoteAddress;

    // 从缓存中读取数据
    readCache((err, cache) => {
        if (err || !cache || isCacheExpired(cache) || !cache[clientIP]) {
            // 如果缓存不存在、过期或者客户端 IP 未在缓存中，则重新请求数据
            fetchData(JSON_URL, (fetchErr, data) => {
                if (fetchErr) {
                    res.writeHead(500);
                    res.end(JSON.stringify({error: 'Internal Server Error'}));
                    return;
                }

                // 分页数据
                const paginatedData = paginate(data, page, pageSize);

                // 更新缓存
                const newCache = {
                    timestamp: Date.now(),
                    [clientIP]: data
                };
                writeCache(newCache, (writeErr) => {
                    if (writeErr) {
                        console.error('Error writing cache:', writeErr);
                    }
                });

                // 返回分页后的数据
                res.writeHead(200);
                res.end(JSON.stringify(paginatedData));
            });
        } else {
            // 如果缓存存在且未过期，并且客户端 IP 在缓存中，则直接返回缓存数据
            const data = cache[clientIP];
            // 分页数据
            const paginatedData = paginate(data, page, pageSize);
            res.writeHead(200);
            res.end(JSON.stringify(paginatedData));
        }
    });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});