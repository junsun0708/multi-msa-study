package com.example.secondservice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/second-service")
public class SecondServiceController {
	Environment env;
	
	public SecondServiceController(Environment env) {
		this.env = env;
	}
	
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Second service.";
    }
    
    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header) {
        log.info(header);
        return "Welcome to the Second service message.";
    }
    
    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("Server port = {}", request.getServerPort());
        return String.format("This is a message from Second Service on PORT %s",
                env.getProperty("local.server.port"));
    	//return "Hi first service.";
    }
}
