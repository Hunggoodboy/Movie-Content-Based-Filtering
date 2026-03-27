package com.movie.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/index")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/survey")
    public String showSurveyPage() {
        return "survey";
    }

}