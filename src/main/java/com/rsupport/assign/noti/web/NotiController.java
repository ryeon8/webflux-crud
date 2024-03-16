package com.rsupport.assign.noti.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/noti")
public class NotiController {

  @GetMapping("/test")
  public String test() {
    return "hello, world";
  }

}
