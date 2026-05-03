package com.movie.Controller;

import com.movie.Service.RecommendationEvaluatorService;
import com.movie.Service.RecommendationEvaluatorService.EvaluationResult;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final RecommendationEvaluatorService evaluatorService;

    /**
     * GET /api/evaluation/metrics?k=10
     * Trả về Precision@K và Recall@K của user đang đăng nhập.
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "10") int k
    ) throws ParseException, JOSEException {

        EvaluationResult result = evaluatorService.evaluate(authHeader, k);

        return ResponseEntity.ok(Map.of(
                "k",             result.k(),
                "hits",          result.hits(),
                "likedTotal",    result.likedTotal(),
                "precisionAtK",  String.format("%.2f", result.precisionAtK() * 100) + "%",
                "recallAtK",     String.format("%.2f", result.recallAtK() * 100) + "%",
                "precisionRaw",  result.precisionAtK(),
                "recallRaw",     result.recallAtK()
        ));
    }
}