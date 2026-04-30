# 🎬 Movie Recommendation — Content-Based Filtering

Hệ thống gợi ý phim dựa trên **lọc theo nội dung (Content-Based Filtering)** sử dụng thuật toán **TF-IDF** để vector hoá đặc trưng phim và tính độ tương đồng cosine.

---

## 📌 Tổng quan

| Thành phần | Công nghệ |
|---|---|
| Backend | Spring Boot 3.x |
| Database | PostgreSQL |
| Thuật toán | TF-IDF + Cosine Similarity |
| Build tool | Maven |

### Luồng hoạt động

```
Thêm phim mới
     ↓
Tiền xử lý văn bản (title, genre, description)
     ↓
Tính TF-IDF → Vector đặc trưng phim
     ↓
Lưu vector vào PostgreSQL
     ↓
Người dùng xem/thích phim X
     ↓
Tính Cosine Similarity(X, toàn bộ phim)
     ↓
Trả về Top-N phim tương tự
```

---

## ⚙️ Thuật toán TF-IDF

**TF (Term Frequency)** — tần suất từ xuất hiện trong mô tả phim:
```
TF(t, d) = số lần t xuất hiện trong d / tổng số từ trong d
```

**IDF (Inverse Document Frequency)** — mức độ hiếm của từ trong toàn bộ kho phim:
```
IDF(t) = log(N / df(t))
```

**Cosine Similarity** — đo độ tương đồng giữa 2 vector phim:
```
sim(A, B) = (A · B) / (||A|| × ||B||)
```

---

## 🗄️ Cấu trúc Database

```sql
-- Bảng phim
CREATE TABLE movies (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    genre       VARCHAR(255),
    description TEXT,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- Bảng lưu vector TF-IDF
CREATE TABLE movie_vectors (
    id          SERIAL PRIMARY KEY,
    movie_id    INT REFERENCES movies(id),
    term        VARCHAR(100),
    tfidf_score DOUBLE PRECISION
);

-- Bảng hành vi người dùng
CREATE TABLE user_interactions (
    id         SERIAL PRIMARY KEY,
    user_id    INT,
    movie_id   INT REFERENCES movies(id),
    action     VARCHAR(50),   -- 'view', 'like', 'rate'
    score      DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## 📁 Cấu trúc Project

```
src/
├── main/
│   ├── java/com/movie/recommendation/
│   │   ├── controller/
│   │   │   └── RecommendationController.java
│   │   ├── service/
│   │   │   ├── TfIdfService.java        ← Tính vector TF-IDF
│   │   │   ├── SimilarityService.java   ← Tính Cosine Similarity
│   │   │   └── RecommendationService.java
│   │   ├── repository/
│   │   │   ├── MovieRepository.java
│   │   │   └── MovieVectorRepository.java
│   │   └── entity/
│   │       ├── Movie.java
│   │       └── MovieVector.java
│   └── resources/
│       └── application.properties
```

---

## 🚀 Cài đặt & Chạy

### Yêu cầu
- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### 1. Clone project
```bash
git clone https://github.com/your-org/movie-recommendation.git
cd movie-recommendation
```

### 2. Cấu hình database
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/movie_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Tạo database
```bash
psql -U postgres -c "CREATE DATABASE movie_db;"
```

### 4. Chạy ứng dụng
```bash
mvn spring-boot:run
```

---

## 📡 API Endpoints

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/api/movies` | Thêm phim mới + tự động tính TF-IDF |
| `GET` | `/api/movies/{id}/recommend?top=10` | Gợi ý phim tương tự |
| `GET` | `/api/users/{id}/recommend` | Gợi ý dựa trên lịch sử xem |
| `POST` | `/api/interactions` | Ghi nhận hành vi người dùng |

### Ví dụ request
```bash
# Gợi ý 10 phim tương tự phim ID=5
GET /api/movies/5/recommend?top=10

# Response
[
  { "id": 12, "title": "Avengers: Infinity War", "similarity": 0.91 },
  { "id": 7,  "title": "Thor: Ragnarok",         "similarity": 0.85 },
  ...
]
```

---

## 🔑 Các lớp xử lý chính

### TfIdfService.java
```java
// Tính vector TF-IDF cho 1 bộ phim
public Map<String, Double> computeTfIdf(Movie movie) {
    String text = movie.getTitle() + " " + movie.getGenre()
                + " " + movie.getDescription();
    // 1. Tokenize + loại stopwords
    // 2. Tính TF từng từ
    // 3. Nhân với IDF toàn kho
    // 4. Lưu vào bảng movie_vectors
}
```

### SimilarityService.java
```java
// Tính cosine similarity giữa 2 phim
public double cosineSimilarity(Map<String, Double> vecA,
                               Map<String, Double> vecB) {
    // dot product / (norm_A * norm_B)
}
```

---

## 📊 Đánh giá

| Metric | Kết quả |
|---|---|
| Precision@10 | ~0.72 |
| Recall@10 | ~0.65 |
| Dataset | MovieLens 100K |

---

## 👥 Nhóm phát triển

| Thành viên | Vai trò |
|---|---|
| Dũng | Thuật toán, kiến trúc hệ thống |
| Kiên | Backend, API, Database |

---

## 📚 Tài liệu tham khảo

- Salton, G. & Buckley, C. (1988). *Term-weighting approaches in automatic text retrieval*
- Spring Boot Documentation — https://spring.io/projects/spring-boot
- PostgreSQL Documentation — https://www.postgresql.org/docs/