package com.movie.Controller;

import com.movie.Service.MatrixFactorization.MatrixFactorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@AllArgsConstructor
public class TrainAIController {
    private final MatrixFactorizationService matrixFactorizationService;

    @GetMapping("/train")
    public ResponseEntity extract(){
        matrixFactorizationService.extract();
        return ResponseEntity.ok("Đã hoàn thành việc huấn luyện mô hình");
    }
}
