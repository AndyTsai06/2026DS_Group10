
// 取得 HTML 元素
const searchInput = document.getElementById("searchInput");
const searchButton = document.getElementById("searchButton");
const statusText = document.getElementById("status");
const resultsContainer = document.getElementById("results");


// 執行搜尋
async function searchWeb() {

    // 取得使用者輸入的關鍵字
    const query = searchInput.value.trim();

    // 檢查是否有輸入內容
    if (query === "") {
        statusText.textContent = "請輸入搜尋關鍵字！";
        resultsContainer.replaceChildren();
        return;
    }

    // 顯示搜尋中
    statusText.textContent = "搜尋中，請稍候...";
    resultsContainer.replaceChildren();

    // 暫時停用搜尋按鈕
    searchButton.disabled = true;

    try {

        // 發送請求到 Spring Boot Controller
        const response = await fetch(
            "/api/search?query=" + encodeURIComponent(query)
        );

        // 將回傳資料轉換成 JSON
        const data = await response.json();

        // 判斷搜尋是否成功
        if (!response.ok || !data.success) {
            throw new Error(data.message || "搜尋失敗！");
        }

        // 取得搜尋結果
        const results = data.results;

        // 沒有搜尋結果
        if (!results || results.length === 0) {
            statusText.textContent = "找不到相關搜尋結果。";
            return;
        }

        // 顯示搜尋結果數量
        statusText.textContent =
            "找到 " + results.length + " 筆搜尋結果";

        // 顯示每一筆搜尋結果
        results.forEach(result => {

            // 建立結果卡片
            const card = document.createElement("div");
            card.className = "result-card";

            // 建立標題
            const title = document.createElement("h2");
            const link = document.createElement("a");

            link.textContent = result.title;
            link.href = result.url;
            link.target = "_blank";
            link.rel = "noopener noreferrer";

            title.appendChild(link);

            // 建立網址
            const url = document.createElement("p");
            url.className = "result-url";
            url.textContent = result.url;

            // 建立搜尋摘要
            const content = document.createElement("p");
            content.className = "result-content";
            content.textContent = result.content;

            // 將元素加入卡片
            card.appendChild(title);
            card.appendChild(url);
            card.appendChild(content);

            // 將卡片加入搜尋結果區域
            resultsContainer.appendChild(card);

        });

    } catch (error) {

        // 顯示錯誤訊息
        statusText.textContent =
            "搜尋發生錯誤：" + error.message;

    } finally {

        // 恢復搜尋按鈕
        searchButton.disabled = false;

    }
}


// 點擊搜尋按鈕時執行搜尋
searchButton.addEventListener("click", searchWeb);


// 按下 Enter 也可以搜尋
searchInput.addEventListener("keydown", function(event) {

    if (event.key === "Enter") {
        searchWeb();
    }

});
