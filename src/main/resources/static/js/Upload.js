// 上传图片
document.addEventListener("DOMContentLoaded", function () {
    var dropzone = document.getElementById('dropzone');

    dropzone.ondragover = function () {
        this.className = 'dropzone dragover';
        return false;
    };

    dropzone.ondragleave = function () {
        this.className = 'dropzone';
        return false;
    };

    dropzone.ondrop = function (e) {
        e.preventDefault();
        this.className = 'dropzone';

        var files = e.dataTransfer.files;
        handleFiles(files);
    };

    dropzone.onclick = function () {
        document.getElementById('fileInput').click();
    };

    function handleFiles(files) {
        // Handle the uploaded files here
        console.log(files);
    }

    document.getElementById('fileInput').addEventListener('change', function (e) {
        var files = e.target.files;
        handleFiles(files);
    });
});

function openFileDialog() {
    document.getElementById('fileInput').click();
}


