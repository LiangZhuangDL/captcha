package com.example.captcha.service.impl;

import com.example.captcha.service.CaptchaService;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

/**
 * @program: captcha
 * @description:
 * @author: Simon Zhuang
 * @create: 2018-12-12 10:11
 **/
@Service
public class CaptchaServiceImpl implements CaptchaService {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @SuppressWarnings("ConstantConditions")
  @Override
  public Map<String, String> createCaptcha(Integer width, Integer height, Integer number) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics graphics = bufferedImage.getGraphics();
    //设置背景颜色
    Color color = new Color(255, 255, 255);
    graphics.setColor(color);
    graphics.fillRect(0, 0, width, height);
    //生成验证码文字
    List<String> words = new ArrayList<>();
    while (words.size() != number) {
      String code = RandomStringUtils.randomAlphanumeric(100);
      code = code.replaceAll("[\\W]", "").toUpperCase();
      words.add(StringUtils.substring(code, code.length() - 1, code.length()));
    }
    Random random = new Random();
    StringBuffer stringBuffer = new StringBuffer();
    words.forEach(word -> {
      graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
      graphics.setFont(new Font("Microsoft Yahei", Font.BOLD, 18));
      graphics.drawString(word, 20 * words.indexOf(word) + 5, 20);
      stringBuffer.append(word);
    });
    //添加干扰线
    for (int i = 0; i < number * 2; i++) {
      graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
      graphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
    }

    //把流转成Base64码发给前台
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
    byte[] bytes = byteArrayOutputStream.toByteArray();
    BASE64Encoder encoder = new BASE64Encoder();
    String code = encoder.encodeBuffer(bytes).trim();
    code = code.replaceAll("\n", "").replaceAll("\r", "");
    Map<String, String> map = new HashMap<>();
    String token = RandomStringUtils.randomAlphanumeric(50);
    while (checkToken(token)){
      token = RandomStringUtils.randomAlphanumeric(50);
    }
    redisTemplate.opsForValue().set(token, stringBuffer.toString(), 600L, TimeUnit.SECONDS);
    map.put("token", token);
    map.put("captcha", "data:image/jpg;base64," + code);

    return map;
  }


  @SuppressWarnings("ConstantConditions")
  @Override
  public Map<String, Object> checkCaptcha(String token, String captcha) {
    Map<String, Object> map = new HashMap<>();
    if (!redisTemplate.hasKey(token)) {
      map.put("result", false);
      map.put("message", "验证码过期，请重新生成验证码");
    } else {
      String code = redisTemplate.opsForValue().get(token);
      redisTemplate.delete(token);
      if(StringUtils.equals(code, captcha)){
        map.put("result", true);
      }else {
        map.put("result", false);
        map.put("message", "验证码错误");
      }
    }
    return map;
  }

  private Boolean checkToken(String token){
    try{
      redisTemplate.hasKey(token);
    }catch (NullPointerException e){
      return false;
    }
    return true;
  }
}
