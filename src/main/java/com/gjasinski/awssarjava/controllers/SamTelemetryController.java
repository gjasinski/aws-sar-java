package com.gjasinski.awssarjava.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class SamTelemetryController {
    @GetMapping("/")
    @PostMapping("/")
    public String main(){
        return "HelloWorld";
    }

    @GetMapping("/metrics")
    @PostMapping("/metrics")
    public String metrics(@RequestBody String input){
        System.out.println(input);
        return "ok";
    }

}
