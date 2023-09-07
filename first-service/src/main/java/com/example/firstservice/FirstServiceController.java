package com.example.firstservice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/first-service")
public class FirstServiceController {
	
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First service.";
    }
    
    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Welcome to the first service message.";
    }
    
    @GetMapping("/check")
    public String check(HttpServletRequest request) {
//        log.info("Server port = {}", request.getServerPort());
//        return String.format("This is a message from First Service on PORT %s",
//                env.getProperty("local.server.port"));
    	return "Hi first service.";
    }

}
