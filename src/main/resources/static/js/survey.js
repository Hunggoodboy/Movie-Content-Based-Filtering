// Data chuẩn 100% lấy từ Backend (Map theo String)
const GENRES = ["Hành động", "Kinh dị", "Hài hước", "Tình cảm", "Phiêu lưu", "Khoa học viễn tưởng", "Hoạt hình", "Tội phạm", "Tâm lý", "Gia đình", "Lịch sử", "Chiến tranh", "Thể thao", "Âm nhạc", "Kinh điển", "Siêu anh hùng", "Sinh tồn", "Bí ẩn", "Thần thoại", "Tài liệu"];
const COUNTRIES = ["Mỹ", "Hàn Quốc", "Nhật Bản", "Trung Quốc", "Việt Nam", "Anh", "Pháp", "Ấn Độ", "Thái Lan", "Hồng Kông", "Đài Loan", "Đức", "Ý", "Tây Ban Nha", "Úc", "Canada", "Nga", "Mexico", "Brazil", "Thụy Điển", "Đan Mạch", "Na Uy", "Phần Lan", "Hà Lan", "Bỉ", "Ba Lan", "Thổ Nhĩ Kỳ", "Indonesia", "Philippines", "Malaysia"];
const LANGUAGES = ["vi", "en", "ko", "ja", "zh", "fr", "th", "hi", "es", "de"];
const AGES = ["P", "C13", "C16", "C18"];

// Biến lưu trữ sự lựa chọn của User
const userPreferences = {
    genres: [],
    countries: [],
    languages: [],
    age: "P", // Mặc định là P
    description: ""
};

// Hàm render Chips ra giao diện
function renderChips(containerId, dataArray, type, isSingleSelect = false) {
    const container = document.getElementById(containerId);

    dataArray.forEach(item => {
        const chip = document.createElement('div');
        chip.classList.add('chip');
        chip.innerText = item;

        // Setup trạng thái mặc định cho Age
        if (isSingleSelect && item === userPreferences.age) {
            chip.classList.add('active');
        }

        chip.addEventListener('click', () => {
            if (isSingleSelect) {
                // Xóa active của tất cả chip khác trong nhóm
                container.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
                chip.classList.add('active');
                userPreferences[type] = item;
            } else {
                // Bật/tắt trạng thái (Chọn nhiều)
                const isActive = chip.classList.toggle('active');
                if (isActive) {
                    userPreferences[type].push(item);
                } else {
                    userPreferences[type] = userPreferences[type].filter(i => i !== item);
                }
            }
        });

        container.appendChild(chip);
    });
}

// Render dữ liệu khi trang load
document.addEventListener("DOMContentLoaded", () => {
    renderChips('genres-group', GENRES, 'genres');
    renderChips('countries-group', COUNTRIES, 'countries');
    renderChips('languages-group', LANGUAGES, 'languages');
    renderChips('age-group', AGES, 'age', true); // True = Chỉ chọn 1
});

// Xử lý gửi API
document.getElementById('btn-submit').addEventListener('click', async () => {
    // 1. Lấy mô tả
    userPreferences.description = document.getElementById('survey-description').value;

    // 2. Validate sơ bộ
    if (userPreferences.genres.length === 0) {
        alert("Vui lòng chọn ít nhất 1 thể loại!");
        return;
    }

    // 3. Lấy token
    const token = localStorage.getItem('token');
    if (!token) {
        alert("Không tìm thấy thông tin đăng nhập! Vui lòng đăng nhập lại.");
        window.location.href = "/login"; // Đổi thành URL trang đăng nhập của bạn
        return;
    }

    // 4. Gửi Request
    try {
        const response = await fetch('/auth/my-favourite', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Gắn token vào header
            },
            body: JSON.stringify(userPreferences)
        });

        if (response.ok) {
            alert("Đã ghi nhận sở thích của bạn! Chúc bạn xem phim vui vẻ.");
            window.location.href = "/index"; // Chuyển về trang chủ
        } else {
            const errorText = await response.text();
            alert("Có lỗi xảy ra: " + errorText);
        }
    } catch (error) {
        alert("Lỗi kết nối đến máy chủ!");
        console.error(error);
    }
});