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

// 将按钮变色
function toggleSwitch(id) {
    const element = document.getElementById(id);
    element.parentElement.classList.toggle('bg-blue-500');
    element.classList.toggle('translate-x-4');
    saveSettings();
}


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
async function autoConfigure() {
    const accessKeyId = document.getElementById("accessKeyId").value;
    const accessKeySecret = document.getElementById("accessKeySecret").value;
    const bucketName = document.getElementById("bucketName").value;

    // 发送请求到后端
    try {
        const response = await fetch("/configure", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                accessKeyId: accessKeyId,
                accessKeySecret: accessKeySecret,
                bucketName: bucketName
            })
        });

        const data = await response.json();

        if (response.ok) {
            // 配置成功，更新用户名和仓库
            document.getElementById("location").innerText = data.location;
            document.getElementById("repo").innerText = data.repo;

            // 发送请求获取 OSS 配置文件
            fetch("/get-settings")
                .then(response => {
                    if (!response.ok) {
                        throw new Error("获取 OSS 配置失败");
                    } else {
                        alert("获取配置文件配置成功")
                    }
                })
        } else {
            alert("配置失败：" + data.message);
        }
    } catch (error) {
        alert("请求失败：" + error.message);
    }
}


// 保存用户的图床设置（例如：压缩、Markdown、哈希化）
function saveSettings() {
    const settings = {
        compress: document.getElementById("compress").parentElement.classList.contains('bg-blue-500'),
        markdown: document.getElementById("markdown").parentElement.classList.contains('bg-blue-500'),
        Hashization: document.getElementById("Hashization").parentElement.classList.contains('bg-blue-500')
    };

    fetch('/save-settings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(settings)
    })
        .then(response => response.json())
        .then(data => {
            if (data.message === "Settings saved successfully") {
                alert("配置保存成功！");
            } else {
                alert("保存失败：" + data.message);
            }
        })
        .catch(error => {
            console.error("请求失败:", error);
            alert("保存失败，发生错误");
        });

}


// 提交图床配置到后端
function submitConfig() {
    const token = document.getElementById("token").value;
    const location = document.getElementById("location").innerText;
    const repo = document.getElementById("repo").innerText;
    const dirType = document.querySelector("input[name='dirType']:checked").value;
    const customDir = document.getElementById("customDir").value;

    // 组装提交数据
    const configData = {
        token: token,
        location: location,
        repo: repo,
        directory: dirType === "custom" ? customDir : dirType
    };

    // 发送 POST 请求到后端
    fetch("/save-settings", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(configData)
    })
        .then(response => response.json())
        .then(data => alert("配置成功：" + JSON.stringify(data)))
        .catch(error => console.error("提交失败:", error));
}