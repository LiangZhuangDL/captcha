package com.example.captcha.service;

import java.io.IOException;
import java.util.Map;

public interface CaptchaService {
  Map<String, String> createCaptcha(Integer width, Integer height, Integer number) throws IOException;

  Map<String, Object> checkCaptcha(String token, String captcha);
}
