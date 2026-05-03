const BASE_URL = "http://localhost:8082";

const urlParams  = new URLSearchParams(window.location.search);
const movieId    = urlParams.get('id');

let watchSeconds  = 0;
let timerInterval = null;

const playBtn     = document.getElementById('play-btn');
const pauseBtn    = document.getElementById('pause-btn');
const timeDisplay = document.getElementById('time-display');
const likeCheckbox = document.getElementById('like-checkbox');

// ============================================================
// 1. TẢI & HIỂN THỊ THÔNG TIN PHIM
// ============================================================
async function loadMovieDetail() {
    if (!movieId) {
        document.getElementById('movie-title').innerText = 'Không tìm thấy phim.';
        return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    try {
        const res = await fetch(`${BASE_URL}/api/movie-detail?movieId=${movieId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) throw new Error("HTTP " + res.status);

        const movie = await res.json();
        renderMovieDetail(movie);

    } catch (err) {
        console.error("Lỗi loadMovieDetail:", err);
        document.getElementById('movie-title').innerText = 'Không thể tải thông tin phim.';
    }
}

function renderMovieDetail(movie) {
    document.title = (movie.title || 'Xem phim') + ' - MovieFlix';

    setText('movie-title',         movie.title);
    setText('movie-director',      movie.director);
    setText('movie-cast',          movie.cast);
    setText('movie-country',       movie.country);
    setText('movie-language',      movie.language);
    setText('movie-age-rating',    movie.ageRating);
    setText('movie-description',   movie.description);
    setText('movie-year',          movie.releaseYear);
    setText('movie-duration',      movie.durationMins);
    setText('movie-views',         movie.views?.toLocaleString('vi-VN'));
    setText('movie-total-ratings', movie.totalRatings?.toLocaleString('vi-VN'));

    const avg = movie.avgRating != null ? movie.avgRating.toFixed(1) : '--';
    setText('movie-avg-rating', avg);

    const posterEl = document.getElementById('movie-poster');
    if (movie.posterUrl) {
        posterEl.src = movie.posterUrl;
        posterEl.alt = movie.title;
    } else {
        posterEl.style.display = 'none';
    }

    const genresEl = document.getElementById('movie-genres');
    if (movie.genres && movie.genres.length > 0) {
        genresEl.innerHTML = movie.genres
            .map(g => `<span class="genre-badge">${g}</span>`)
            .join('');
    }

    // Khôi phục trạng thái like từ response nếu backend trả về
    if (movie.liked != null) {
        likeCheckbox.checked = movie.liked;
        updateLikeLabel(movie.liked);
    }
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.innerText = value || '--';
}

// ============================================================
// 2. ĐIỀU KHIỂN VIDEO
// ============================================================
function formatTime(totalSeconds) {
    const h = Math.floor(totalSeconds / 3600).toString().padStart(2, '0');
    const m = Math.floor((totalSeconds % 3600) / 60).toString().padStart(2, '0');
    const s = (totalSeconds % 60).toString().padStart(2, '0');
    return `${h}:${m}:${s}`;
}

playBtn.onclick = () => {
    playBtn.style.display  = 'none';
    pauseBtn.style.display = 'inline-block';
    timerInterval = setInterval(() => {
        watchSeconds++;
        timeDisplay.innerText = formatTime(watchSeconds);
    }, 1000);
};

pauseBtn.onclick = () => {
    pauseBtn.style.display = 'none';
    playBtn.style.display  = 'inline-block';
    clearInterval(timerInterval);
};

// ============================================================
// 3. YÊU THÍCH PHIM  →  POST /api/like-movie
// ============================================================

// Cập nhật nhãn checkbox theo trạng thái
function updateLikeLabel(isLiked) {
    const label = likeCheckbox.closest('label');
    if (!label) return;
    label.innerHTML = isLiked
        ? `<input type="checkbox" id="like-checkbox" checked> ❤️ Đã yêu thích`
        : `<input type="checkbox" id="like-checkbox"> 👍 Yêu thích phim này`;

    // Gắn lại event vì innerHTML thay thế node
    document.getElementById('like-checkbox').addEventListener('change', handleLikeToggle);
}

async function handleLikeToggle(e) {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    // Disable tránh double-click
    e.target.disabled = true;

    try {
        const res = await fetch(`${BASE_URL}/api/like-movie`, {
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ movieId: movieId })
        });

        if (!res.ok) throw new Error("HTTP " + res.status);

        const data = await res.json();

        // Backend trả về trạng thái liked mới (true/false) qua data.liked hoặc data.success
        const nowLiked = data.liked ?? e.target.checked;
        updateLikeLabel(nowLiked);

        showToast(nowLiked ? '❤️ Đã thêm vào yêu thích!' : '💔 Đã bỏ yêu thích');

    } catch (err) {
        console.error("Lỗi like phim:", err);
        // Revert lại trạng thái checkbox nếu lỗi
        e.target.checked = !e.target.checked;
        showToast('⚠️ Có lỗi xảy ra, vui lòng thử lại', true);
    } finally {
        e.target.disabled = false;
    }
}

likeCheckbox.addEventListener('change', handleLikeToggle);

// ============================================================
// 4. TOAST THÔNG BÁO NHẸ
// ============================================================
function showToast(msg, isError = false) {
    let toast = document.getElementById('_toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = '_toast';
        Object.assign(toast.style, {
            position:     'fixed',
            bottom:       '28px',
            right:        '24px',
            padding:      '10px 18px',
            borderRadius: '8px',
            fontSize:     '14px',
            fontWeight:   '500',
            color:        'white',
            zIndex:       '9999',
            opacity:      '0',
            transition:   'opacity .3s',
            pointerEvents:'none'
        });
        document.body.appendChild(toast);
    }

    toast.textContent  = msg;
    toast.style.background = isError ? '#c0392b' : '#e50914';
    toast.style.opacity    = '1';
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => { toast.style.opacity = '0'; }, 2800);
}

// ============================================================
// 5. GỬI HÀNH VI KHI RỜI TRANG
// ============================================================
window.addEventListener('beforeunload', () => {
    if (watchSeconds > 0) sendBehaviorData();
});

function sendBehaviorData() {
    const token = localStorage.getItem("token");
    if (!token || !movieId) return;

    const isLiked       = document.getElementById('like-checkbox')?.checked ? 1.0 : 0.0;
    const durationWatch = formatTime(watchSeconds);

    const payload = {
        movieId:       movieId,
        durationWatch: durationWatch,
        liked:         isLiked
    };

    fetch(`${BASE_URL}/api/behavior/evaluate`, {
        method:    'POST',
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body:      JSON.stringify(payload),
        keepalive: true
    }).catch(err => console.error("Lỗi gửi tracking:", err));
}

// ============================================================
// 6. LOGOUT
// ============================================================
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
}

// ============================================================
// KHỞI CHẠY
// ============================================================
loadMovieDetail();