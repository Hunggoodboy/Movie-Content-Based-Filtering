const BASE_URL = "http://localhost:8082";

// 1. Lấy ID phim từ URL (Ví dụ: /detail.html?id=a1b2c3d4-...)
const urlParams = new URLSearchParams(window.location.search);
const movieId = urlParams.get('id');

// Biến lưu trữ thời gian
let watchSeconds = 0;
let timerInterval = null;

const playBtn = document.getElementById('play-btn');
const pauseBtn = document.getElementById('pause-btn');
const timeDisplay = document.getElementById('time-display');

// Chuyển đổi giây sang định dạng HH:mm:ss để Spring Boot map vào LocalTime
function formatTime(totalSeconds) {
    const h = Math.floor(totalSeconds / 3600).toString().padStart(2, '0');
    const m = Math.floor((totalSeconds % 3600) / 60).toString().padStart(2, '0');
    const s = (totalSeconds % 60).toString().padStart(2, '0');
    return `${h}:${m}:${s}`;
}

// 2. Logic Bấm Play (Đếm giờ)
playBtn.onclick = () => {
    playBtn.style.display = 'none';
    pauseBtn.style.display = 'inline-block';

    timerInterval = setInterval(() => {
        watchSeconds++;
        timeDisplay.innerText = formatTime(watchSeconds);
    }, 1000); // Tăng 1 giây mỗi 1000ms
};

// 3. Logic Bấm Pause (Dừng đếm)
pauseBtn.onclick = () => {
    pauseBtn.style.display = 'none';
    playBtn.style.display = 'inline-block';

    clearInterval(timerInterval);
};

// 4. BẮT SỰ KIỆN RỜI TRANG ĐỂ GỬI API
window.addEventListener('beforeunload', () => {
    // Chỉ gửi request nếu user có bấm xem (thời gian > 0)
    if (watchSeconds > 0) {
        sendBehaviorData();
    }
});

// Hàm gửi dữ liệu về Backend
// Hàm gửi dữ liệu về Backend (Đã cập nhật cho @RequestBody)
function sendBehaviorData() {
    const token = localStorage.getItem("token");
    if (!token || !movieId) return;

    // Lấy thông tin user đánh giá
    const ratingInput = document.getElementById('rating-input').value;
    const isLiked = document.getElementById('like-checkbox').checked ? 1.0 : 0.0;
    const durationWatch = formatTime(watchSeconds);

    // Đóng gói dữ liệu thành một Object chuẩn bị ép sang JSON
    const payload = {
        movieId: movieId,
        durationWatch: durationWatch,
        liked: isLiked
    };

    // Chỉ thêm rating nếu user có nhập số
    if (ratingInput) {
        payload.rating = parseFloat(ratingInput);
    }

    // Gửi request dạng JSON
    fetch(`${BASE_URL}/api/behavior/evaluate`, {
        method: 'POST',
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json" // QUAN TRỌNG: Khai báo gửi JSON
        },
        body: JSON.stringify(payload), // Ép Object thành chuỗi JSON
        keepalive: true // Vẫn giữ cờ này để trình duyệt gửi nốt khi tắt tab
    }).catch(err => console.error("Lỗi gửi tracking:", err));
}

// Hàm logout dùng chung
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
}