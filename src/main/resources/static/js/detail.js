const BASE_URL = "http://localhost:8082";

const urlParams = new URLSearchParams(window.location.search);
const movieId = urlParams.get('id');

let watchSeconds = 0;
let timerInterval = null;

const playBtn    = document.getElementById('play-btn');
const pauseBtn   = document.getElementById('pause-btn');
const timeDisplay = document.getElementById('time-display');

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
// 3. GỬI HÀNH VI KHI RỜI TRANG
// ============================================================
window.addEventListener('beforeunload', () => {
    if (watchSeconds > 0) sendBehaviorData();
});

function sendBehaviorData() {
    const token = localStorage.getItem("token");
    if (!token || !movieId) return;

    const ratingInput   = document.getElementById('rating-input').value;
    const isLiked       = document.getElementById('like-checkbox').checked ? 1.0 : 0.0;
    const durationWatch = formatTime(watchSeconds);

    const payload = {
        movieId: movieId,
        durationWatch: durationWatch,
        liked: isLiked
    };
    if (ratingInput) payload.rating = parseFloat(ratingInput);

    fetch(`${BASE_URL}/api/behavior/evaluate`, {
        method: 'POST',
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload),
        keepalive: true
    }).catch(err => console.error("Lỗi gửi tracking:", err));
}

// ============================================================
// 4. LOGOUT
// ============================================================
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
}

// ============================================================
// KHỞI CHẠY
// ============================================================
loadMovieDetail();