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
import javax.imageio.ImageIO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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

  private Integer width = 100;

  private Integer height = 35;

  @Override
  public Map<String, String> createCaptcha() throws IOException {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics graphics = bufferedImage.getGraphics();
    //设置背景颜色
    Color color = new Color(255, 255, 255);
    graphics.setColor(color);
    graphics.fillRect(0, 0, width, height);
    //生成验证码文字
    List<String> words = new ArrayList<>();
    while (words.size() != 5) {
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
    for(int i = 0;i<10;i++){
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
    map.put("code", stringBuffer.toString());
    map.put("captcha", "data:image/jpg;base64," + code);
    return map;
  }
}
