package com.example.captcha.service;

import java.io.IOException;
import java.util.Map;

public interface CaptchaService {
  Map<String, String> createCaptcha() throws IOException;
}
