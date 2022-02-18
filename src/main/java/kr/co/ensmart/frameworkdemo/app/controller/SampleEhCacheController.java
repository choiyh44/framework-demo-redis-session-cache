package kr.co.ensmart.frameworkdemo.app.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.ensmart.frameworkdemo.app.dto.User;
import kr.co.ensmart.frameworkdemo.app.service.SampleEhCacheService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/sample/ehcache")
@Slf4j
public class SampleEhCacheController {
    @Autowired
    private SampleEhCacheService sampleService;
    
	@Value("${app.name}")
	private String appName;
	
	@GetMapping("/main")
	String viewMain() {
		log.info("test: {}", appName);
		
		return "sample/main";
	}

	@GetMapping("/session")
	@ResponseBody
	public String index(HttpSession session) {
	    session.setAttribute("name", "최영환");
	    return session.getId() + " Hello " + session.getAttribute("name");
	}

    @GetMapping("/user")
    @ResponseBody
    public User getUser(@RequestParam String userName) {
        return sampleService.findByUserName(userName);
    }

    @GetMapping("/userById")
    @ResponseBody
    public User getUserById(User user) {
        return sampleService.findByUserId(user);
    }

}
