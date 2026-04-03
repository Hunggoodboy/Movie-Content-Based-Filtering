const BASE_URL = "http://localhost:8082";

// 🎯 RECOMMEND
async function loadRecommendMovies() {
    try {
        const token = localStorage.getItem("token");

        const res = await fetch(BASE_URL + "/my-recommend-movie", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        const data = await res.json();
        renderRow(data, "recommend-list");
    } catch (e) {
        console.error("RECOMMEND lỗi:", e);
    }
}

// 🎨 RENDER
function renderRow(movies, id) {
    const container = document.getElementById(id);
    container.innerHTML = "";

    movies.forEach(m => {
        const div = document.createElement("div");
        div.className = "movie-card";

        const img = m.posterUrl && m.posterUrl.startsWith("http")
            ? m.posterUrl
            : "https://via.placeholder.com/300x450?text=No+Image";

        div.style.backgroundImage = `url(${img})`;
        div.setAttribute("data-title", m.title);

        // 👇 CLICK ĐỂ MỞ YOUTUBE
        div.onclick = () => {
            if (m.externalUrl) {
                window.open(m.externalUrl, "_blank");
            } else {
                alert("Phim này chưa có link!");
            }
        };

        container.appendChild(div);
    });
}
// 🚀 LOAD
window.onload = () => {
    loadRecommendMovies();
};

// 🔓 LOGOUT
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login";
}