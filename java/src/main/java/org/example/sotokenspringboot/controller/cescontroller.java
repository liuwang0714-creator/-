package org.example.sotokenspringboot.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class cescontroller {

    @GetMapping
    public String ces(){
        return "ces";
    }


}
