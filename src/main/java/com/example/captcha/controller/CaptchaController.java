package com.example.captcha.controller;

import com.example.captcha.service.CaptchaService;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: captcha
 * @description:
 * @author: Simon Zhuang
 * @create: 2018-12-12 10:08
 **/
@RestController
public class CaptchaController {

  @Autowired
  private CaptchaService captchaService;

  @GetMapping(value = "/createCaptcha")
  public Map<String, String> createCaptcha() throws IOException {
    return captchaService.createCaptcha();
  }
}
