const BASE_URL = "http://localhost:8082";

let allRecommendMovies = []; // Lưu trữ toàn bộ phim nhận từ API
let currentPage = 1;
const pageSize = 20; // Số lượng phim hiển thị trên 1 trang

// 🎯 FETCH DỮ LIỆU
async function loadRecommendMovies() {
    try {
        const token = localStorage.getItem("token");
        const res = await fetch(BASE_URL + "/my-recommend-movie", {
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });

        if (!res.ok) {
            console.error("Lỗi xác thực hoặc server:", res.status);
            document.getElementById("recommend-list").innerHTML = "<p style='color:red;'>Lỗi xác thực. Vui lòng đăng nhập lại.</p>";
            return;
        }

        const data = await res.json();

        // Cập nhật mảng dữ liệu
        if (data && data.length > 0) {
            allRecommendMovies = data;
        } else {
            allRecommendMovies = [];
        }

        renderPage(1); // Mặc định render trang 1 khi load xong
    } catch (e) {
        console.error("RECOMMEND lỗi:", e);
        document.getElementById("recommend-list").innerHTML = "<p style='color:red;'>Không thể kết nối đến máy chủ.</p>";
    }
}

// 🎨 RENDER THEO TRANG
function renderPage(page) {
    const container = document.getElementById("recommend-list");
    container.innerHTML = ""; // Xóa dữ liệu trang cũ

    // Nếu không có phim
    if (allRecommendMovies.length === 0) {
        container.innerHTML = "<p>Hiện tại chưa có phim đề xuất nào cho bạn.</p>";
        document.getElementById("page-info").innerText = "Trang 1 / 1";
        document.getElementById("prev-btn").disabled = true;
        document.getElementById("next-btn").disabled = true;
        return;
    }

    // Tính toán cắt mảng phim
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const moviesToShow = allRecommendMovies.slice(start, end);

    // Sinh HTML cho từng phim
    moviesToShow.forEach(m => {
        const div = document.createElement("div");
        div.className = "movie-card";

        const img = m.posterUrl && m.posterUrl.startsWith("http")
            ? m.posterUrl
            : "https://via.placeholder.com/300x450?text=No+Image";

        div.style.backgroundImage = `url('${img}')`;
        div.setAttribute("data-title", m.title || "Phim chưa cập nhật tên");

        // Sự kiện click mở chi tiết phim
        div.onclick = () => {
            const movieId = m.id || m.movieId;
            if (movieId) {
                window.location.href = "/detail?id=" + movieId;
            } else {
                alert("Lỗi: Không tìm thấy ID phim!");
            }
        };

        container.appendChild(div);
    });

    // Cập nhật thanh điều hướng
    const totalPages = Math.ceil(allRecommendMovies.length / pageSize);
    document.getElementById("page-info").innerText = `Trang ${page} / ${totalPages}`;

    document.getElementById("prev-btn").disabled = (page === 1);
    document.getElementById("next-btn").disabled = (page >= totalPages);
}

// 🔄 HÀM XỬ LÝ NÚT CHUYỂN TRANG
function changePage(delta) {
    const totalPages = Math.ceil(allRecommendMovies.length / pageSize);
    const newPage = currentPage + delta;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        renderPage(currentPage);

        // Cuộn lên đầu màn hình khi sang trang mới
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}

// 🚀 KHỞI CHẠY KHI TRANG TẢI XONG
window.onload = () => {
    loadRecommendMovies();
};

// 🔓 ĐĂNG XUẤT
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login";
}