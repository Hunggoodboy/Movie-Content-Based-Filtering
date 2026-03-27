package com.movie.Service.vectorizer;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class OneHotEncoder {
    private static final Map<String, Integer> GENRE_INDEX = new HashMap<>();
    private static final Map<String, Integer> COUNTRY_INDEX = new HashMap<>();
    private static final Map<String, Integer> LANGUAGE_INDEX = new HashMap<>();
    private static final Map<String, Integer> AGE_INDEX = new HashMap<>();

    static {
        // Genres index 0 → 19
        GENRE_INDEX.put("Hành động", 0);
        GENRE_INDEX.put("Kinh dị",   1);
        GENRE_INDEX.put("Hài hước",  2);
        GENRE_INDEX.put("Tình cảm",  3);
        GENRE_INDEX.put("Phiêu lưu", 4);
        GENRE_INDEX.put("Khoa học viễn tưởng", 5);
        GENRE_INDEX.put("Hoạt hình", 6);
        GENRE_INDEX.put("Tội phạm",  7);
        GENRE_INDEX.put("Tâm lý",    8);
        GENRE_INDEX.put("Gia đình",  9);
        GENRE_INDEX.put("Lịch sử",   10);
        GENRE_INDEX.put("Chiến tranh", 11);
        GENRE_INDEX.put("Thể thao",  12);
        GENRE_INDEX.put("Âm nhạc",   13);
        GENRE_INDEX.put("Kinh điển", 14);
        GENRE_INDEX.put("Siêu anh hùng", 15);
        GENRE_INDEX.put("Sinh tồn",  16);
        GENRE_INDEX.put("Bí ẩn",     17);
        GENRE_INDEX.put("Thần thoại",18);
        GENRE_INDEX.put("Tài liệu",  19);

        // Country index 20 → 49
        COUNTRY_INDEX.put("Mỹ",        20);
        COUNTRY_INDEX.put("Hàn Quốc",  21);
        COUNTRY_INDEX.put("Nhật Bản",  22);
        COUNTRY_INDEX.put("Trung Quốc",23);
        COUNTRY_INDEX.put("Việt Nam",  24);
        COUNTRY_INDEX.put("Anh",       25);
        COUNTRY_INDEX.put("Pháp",      26);
        COUNTRY_INDEX.put("Ấn Độ",     27);
        COUNTRY_INDEX.put("Thái Lan",  28);
        COUNTRY_INDEX.put("Hồng Kông", 29);
        COUNTRY_INDEX.put("Đài Loan",  30);
        COUNTRY_INDEX.put("Đức",       31);
        COUNTRY_INDEX.put("Ý",         32);
        COUNTRY_INDEX.put("Tây Ban Nha",33);
        COUNTRY_INDEX.put("Úc",        34);
        COUNTRY_INDEX.put("Canada",    35);
        COUNTRY_INDEX.put("Nga",       36);
        COUNTRY_INDEX.put("Mexico",    37);
        COUNTRY_INDEX.put("Brazil",    38);
        COUNTRY_INDEX.put("Thụy Điển", 39);
        COUNTRY_INDEX.put("Đan Mạch",  40);
        COUNTRY_INDEX.put("Na Uy",     41);
        COUNTRY_INDEX.put("Phần Lan",  42);
        COUNTRY_INDEX.put("Hà Lan",    43);
        COUNTRY_INDEX.put("Bỉ",        44);
        COUNTRY_INDEX.put("Ba Lan",    45);
        COUNTRY_INDEX.put("Thổ Nhĩ Kỳ",46);
        COUNTRY_INDEX.put("Indonesia", 47);
        COUNTRY_INDEX.put("Philippines",48);
        COUNTRY_INDEX.put("Malaysia",  49);

        // Language index 50 → 59
        LANGUAGE_INDEX.put("vi", 50);
        LANGUAGE_INDEX.put("en", 51);
        LANGUAGE_INDEX.put("ko", 52);
        LANGUAGE_INDEX.put("ja", 53);
        LANGUAGE_INDEX.put("zh", 54);
        LANGUAGE_INDEX.put("fr", 55);
        LANGUAGE_INDEX.put("th", 56);
        LANGUAGE_INDEX.put("hi", 57);
        LANGUAGE_INDEX.put("es", 58);
        LANGUAGE_INDEX.put("de", 59);

        // Age rating index 60 → 63
        AGE_INDEX.put("P",   60);
        AGE_INDEX.put("C13", 61);
        AGE_INDEX.put("C16", 62);
        AGE_INDEX.put("C18", 63);
    }

    public float[] oneHotEncoder(Set<String> genres, Set<String> countries, Set<String> languages, String ageRating) {
        float[] result = new float[84];

        for(String genre : genres){
            Integer idx = GENRE_INDEX.get(genre);
            if(idx != null) result[idx] = 1;
        }
        for(String country : countries) {
            Integer idx = COUNTRY_INDEX.get(country);
            if(idx != null) result[idx] = 1;
        }
        for(String language : languages) {
            Integer idx = LANGUAGE_INDEX.get(language);
            if(idx != null) result[idx] = 1;
        }
        if(ageRating != null) {
            Integer idx = AGE_INDEX.get(ageRating);
            if(idx != null) result[idx] = 1;
        }
        return result;
    }
}
