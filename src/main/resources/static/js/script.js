document.addEventListener("DOMContentLoaded", function () {
    let loading = false;
    let currentPage = 1;
    // 每页显示的缩略图数量
    const pageSize = 9;

    // 发起 XMLHttpRequest 请求获取数据
    function makeRequest(url, callback) {
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    callback(null, JSON.parse(xhr.responseText));
                } else {
                    callback(new Error("Failed to fetch data"));
                }
            }
        };
        xhr.open("GET", url, true);
        xhr.send();
    }

    // 加载缩略图函数
    function loadThumbnails(thumbnails) {
        const container = document.getElementById("thumbnail-container");
        // 遍历缩略图数组，创建并添加缩略图元素到页面
        thumbnails.forEach(function (thumbnailUrl) {
            const thumbnail = document.createElement("div");
            thumbnail.className = "thumbnail";
            const img = new Image();
            // 存储真实图片 URL
            img.dataset.src = thumbnailUrl;
            thumbnail.appendChild(img);
            container.appendChild(thumbnail);
            // 添加点击事件监听器，显示对应的大图
            thumbnail.addEventListener("click", function () {
                showFullImage(thumbnailUrl.replace("thumbnail", "full"));
            });
        });
    }

    // 显示大图函数
    function showFullImage(url) {
        const fullImageContainer = document.getElementById("full-image-container");
        const fullImage = document.createElement("img");
        fullImage.src = url;
        fullImageContainer.innerHTML = "";
        fullImageContainer.appendChild(fullImage);

        // 渐变显示大图容器,初始透明度为0
        fullImageContainer.style.opacity = "0";
        // 显示大图容器
        fullImageContainer.style.display = "flex";
        // 使用setTimeout在下一个渲染周期中执行，以触发渐变动画
        setTimeout(function () {
            // 最终透明度为1
            fullImageContainer.style.opacity = "1";
        }, 10);

        // 添加点击事件监听器，点击容器外部关闭大图
        fullImageContainer.addEventListener("click", function (event) {
            // 检查点击的目标是否为图片本身
            if (event.target !== fullImage) {
                // 渐变隐藏大图容器
                fullImageContainer.style.opacity = "0";
                // 使用setTimeout在渐变动画结束后隐藏大图容器
                setTimeout(function () {
                    fullImageContainer.style.display = "none";
                    // 此处的300毫秒与CSS中渐变动画的持续时间相同
                }, 300);
            }
        });
    }

    // 加载下一页图片函数
    function loadNextImages() {
        if (loading) return;
        loading = true;
        // 构造请求 URL
        const url = `http://localhost:3000?page=${currentPage}&pageSize=${pageSize}`;
        // 发起请求获取数据
        makeRequest(url, function (error, data) {
            if (error) {
                console.error(error);
            } else {
                // 加载缩略图
                loadThumbnails(data);
                loading = false;
                // 加载下一页
                currentPage++;
            }
        });
    }

    // 监听页面滚动事件，触发加载下一页图片
    window.addEventListener("scroll", function () {
        // 当滚动到页面底部时加载下一页图片
        if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - (window.innerHeight / 2))) {
            loadNextImages();
        }

        // 图片懒加载
        const lazyImages = document.querySelectorAll('.thumbnail img[data-src]');
        lazyImages.forEach(function (lazyImage) {
            if (lazyImage.offsetTop < window.innerHeight + window.scrollY) {
                lazyImage.src = lazyImage.dataset.src;
                lazyImage.removeAttribute('data-src');
            }
        });
    });

    // 初始化加载第一页图片
    loadNextImages();
});