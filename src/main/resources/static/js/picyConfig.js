function changeTab(tabName) {
    document.getElementById("activeTab").innerText = tabName;

    // 隐藏所有内容
    document.querySelectorAll(".tab-content").forEach(tab => tab.classList.add("hidden"));
    document.getElementById(tabName).classList.remove("hidden");

    // 移除所有按钮的选中状态
    document.querySelectorAll(".tab-btn").forEach(btn => btn.classList.remove("bg-gray-300"));

    // 给当前选中的按钮添加背景色
    const activeBtn = document.querySelector(`.tab-btn[data-tab="${tabName}"]`);
    if (activeBtn) {
        activeBtn.classList.add("bg-gray-300");
    }
}


function toggleModal(show) {
    document.getElementById("notificationModal").classList.toggle("hidden", !show);
}

function toggleSettings(event) {
    event.stopPropagation();  // 阻止事件冒泡，避免被 document 点击监听器捕获
    const settingsDropdown = document.getElementById("settingsDropdown");
    settingsDropdown.classList.toggle("hidden");
}

function toggleSwitch(id) {
    const element = document.getElementById(id);
    element.parentElement.classList.toggle('bg-blue-500');
    element.classList.toggle('translate-x-4');
    saveSettings();
}

function saveSettings() {
    const settings = {
        watermark: document.getElementById("watermark").parentElement.classList.contains('bg-blue-500'),
        compress: document.getElementById("compress").parentElement.classList.contains('bg-blue-500'),
        markdown: document.getElementById("markdown").parentElement.classList.contains('bg-blue-500')
    };
    fetch('/api/save-settings', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(settings)
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const settingsIcon = document.querySelector(".fa-cog");
    const settingsDropdown = document.getElementById("settingsDropdown");

    settingsIcon.addEventListener("click", function (event) {
        toggleSettings(event);
    });

    document.addEventListener("click", function (event) {
        const notificationModal = document.getElementById("notificationModal");
        const bellIcon = document.querySelector(".fa-bell");

        // 关闭通知弹窗
        if (!notificationModal.contains(event.target) && !bellIcon.contains(event.target)) {
            notificationModal.classList.add("hidden");
        }

        // 关闭设置菜单
        if (!settingsDropdown.contains(event.target) && !settingsIcon.contains(event.target)) {
            settingsDropdown.classList.add("hidden");
        }
    });
});

// 处理头像
document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/user/info')  // 调用后端 API
        .then(response => response.json())
        .then(data => {
            const avatarElement = document.getElementById("userAvatar");
            if (data.avatar) {
                avatarElement.src = data.avatar;  // 更新头像
            }
        })
        .catch(error => console.error("Error fetching user info:", error));
});


// 图床配置
document.addEventListener("DOMContentLoaded", function () {
    // 监听目录选项变化
    document.querySelectorAll("input[name='dirType']").forEach(radio => {
        radio.addEventListener("change", function () {
            const customDirInput = document.getElementById("customDir");
            customDirInput.classList.toggle("hidden", this.value !== "custom");
        });
    });
});

// 配置（可以从后端获取默认数据）
function autoConfigure() {
    document.getElementById("username").innerText = "ChengHaoRan666";
    document.getElementById("repo").innerText = "picx-images-hosting";
    document.querySelector("input[name='dirType'][value='root']").checked = true;
}

// 提交数据到后端
function submitConfig() {
    const token = document.getElementById("token").value;
    const username = document.getElementById("username").innerText;
    const repo = document.getElementById("repo").innerText;
    const dirType = document.querySelector("input[name='dirType']:checked").value;
    const customDir = document.getElementById("customDir").value;

    const configData = {
        token: token,
        username: username,
        repo: repo,
        directory: dirType === "custom" ? customDir : dirType
    };

    fetch("/api/config", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(configData)
    })
        .then(response => response.json())
        .then(data => alert("配置成功：" + JSON.stringify(data)))
        .catch(error => console.error("提交失败:", error));
}