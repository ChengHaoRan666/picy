// 切换 Tab 选项卡，并改变样式
function changeTab(tabName) {
    // 更新当前激活的 Tab 名称
    document.getElementById("activeTab").innerText = tabName;

    // 隐藏所有 Tab 内容区域
    document.querySelectorAll(".tab-content").forEach(tab => tab.classList.add("hidden"));
    document.getElementById(tabName).classList.remove("hidden");

    // 移除所有按钮的选中状态
    document.querySelectorAll(".tab-btn").forEach(btn => btn.classList.remove("bg-gray-300"));

    // 给当前选中的 Tab 按钮添加背景色，表示选中
    const activeBtn = document.querySelector(`.tab-btn[data-tab="${tabName}"]`);
    if (activeBtn) {
        activeBtn.classList.add("bg-gray-300");
    }
}

// 控制公告模态框（弹窗）的显示和隐藏
function toggleModal(show) {
    document.getElementById("notificationModal").classList.toggle("hidden", !show);
}

// 控制设置菜单的显示/隐藏，并防止点击时事件冒泡
function toggleSettings(event) {
    event.stopPropagation(); // 阻止事件冒泡，避免点击其他地方时立即关闭
    const settingsDropdown = document.getElementById("settingsDropdown");
    settingsDropdown.classList.toggle("hidden");
}

// 保存用户的图床设置（例如：水印、压缩、Markdown）
function saveSettings() {
    const settings = {
        watermark: document.getElementById("watermark").parentElement.classList.contains('bg-blue-500'),
        compress: document.getElementById("compress").parentElement.classList.contains('bg-blue-500'),
        markdown: document.getElementById("markdown").parentElement.classList.contains('bg-blue-500')
    };

    // 发送数据到后端，进行保存
    fetch('/api/save-settings', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(settings)
    });
}

// 在页面加载完成后，绑定事件监听器
document.addEventListener("DOMContentLoaded", function () {
    const settingsIcon = document.querySelector(".fa-cog"); // 获取设置图标
    const settingsDropdown = document.getElementById("settingsDropdown");

    // 点击设置图标，显示/隐藏设置菜单
    settingsIcon.addEventListener("click", function (event) {
        toggleSettings(event);
    });

    // 点击页面其他地方时，关闭通知弹窗和设置菜单
    document.addEventListener("click", function (event) {
        const notificationModal = document.getElementById("notificationModal");
        const bellIcon = document.querySelector(".fa-bell");

        // 如果点击的不是通知图标或弹窗内部，则隐藏通知弹窗
        if (!notificationModal.contains(event.target) && !bellIcon.contains(event.target)) {
            notificationModal.classList.add("hidden");
        }

        // 如果点击的不是设置图标或菜单内部，则隐藏设置菜单
        if (!settingsDropdown.contains(event.target) && !settingsIcon.contains(event.target)) {
            settingsDropdown.classList.add("hidden");
        }
    });
});

// 页面加载时，从后端获取用户头像并更新 UI
document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/user/info')  // 发送请求获取用户信息
        .then(response => response.json())
        .then(data => {
            const avatarElement = document.getElementById("userAvatar");
            if (data.avatar) {
                avatarElement.src = data.avatar;  // 更新头像
            }
        })
        .catch(error => console.error("Error fetching user info:", error));
});

// 监听目录选择，切换“新建目录”输入框的显示状态
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("input[name='dirType']").forEach(radio => {
        radio.addEventListener("change", function () {
            const customDirInput = document.getElementById("customDir");
            customDirInput.classList.toggle("hidden", this.value !== "custom");
        });
    });
});

// 自动配置默认的图床信息（用户名、仓库）
function autoConfigure() {
    document.getElementById("username").innerText = "xxx";
    document.getElementById("repo").innerText = "xxx";
    document.querySelector("input[name='dirType'][value='root']").checked = true;
}

// 提交图床配置到后端
function submitConfig() {
    const token = document.getElementById("token").value;
    const username = document.getElementById("username").innerText;
    const repo = document.getElementById("repo").innerText;
    const dirType = document.querySelector("input[name='dirType']:checked").value;
    const customDir = document.getElementById("customDir").value;

    // 组装提交数据
    const configData = {
        token: token,
        username: username,
        repo: repo,
        directory: dirType === "custom" ? customDir : dirType
    };

    // 发送 POST 请求到后端
    fetch("/api/config", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(configData)
    })
        .then(response => response.json())
        .then(data => alert("配置成功：" + JSON.stringify(data)))
        .catch(error => console.error("提交失败:", error));
}