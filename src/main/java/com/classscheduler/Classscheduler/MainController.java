package com.classscheduler.Classscheduler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/index")
    public String index() {
        return "desktop-1440px";
    }
}
