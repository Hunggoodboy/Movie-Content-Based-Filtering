package com.movie.Controller;

import com.movie.DTO.Request.BehaviorRequest;
import com.movie.Service.MatrixFactorization.UserActivity;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/behavior")
@AllArgsConstructor
public class BehaviorController {
    private final UserActivity userFavourite;
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateBehavior(@RequestHeader("Authorization") String authHeader,@RequestBody BehaviorRequest behaviorRequest) throws JOSEException {
        try {
            System.out.println("Hệ thống bắt đầu đánh giá hành vi người dùng");
            return ResponseEntity.ok(userFavourite.EvaluateUserFavourite(behaviorRequest, authHeader));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
