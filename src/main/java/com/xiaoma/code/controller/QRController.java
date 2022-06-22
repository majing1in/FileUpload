package com.xiaoma.code.controller;

import com.google.zxing.WriterException;
import com.xiaoma.code.utils.QRCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class QRController {

    /**
     * 二维码的信息，可以用微信、钉钉等任何能扫码的软件扫描
     * 1.如果是普通文本，则扫码显示文本信息
     * 2.如果是链接，则扫码后跳转链接的URL
     */
    private final String info;

    {
        info = new String("https://zhuanlan.zhihu.com/p/111099137".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    /**
     * 用ResponseEntity响应文件流（页面显示），Spring会从里面取出数据塞入响应流返回
     */
    @GetMapping("/qrImageByResponseEntity")
    public ResponseEntity<byte[]> getQRImageByResponseEntity() throws IOException, WriterException {
        byte[] qrCode = QRCodeGenerator.getQRCodeImage(info, 360, 360);
        // Header设置文件类型（对于ResponseEntity响应的方式，必须设置文件类型）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qrCode, headers, HttpStatus.CREATED);
    }

    /**
     * response原生响应文件流（页面显示）
     */
    @GetMapping("/qrImageByOutputStream")
    public void getQRImageByOutputStream(HttpServletResponse response) throws IOException, WriterException {
        byte[] qrCode = QRCodeGenerator.getQRCodeImage(info, 360, 360);
        // Header设置文件类型（ContentType不设置也没事）
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.getOutputStream().write(qrCode);
    }

    /**
     * response原生响应文件流（下载）
     */
    @PostMapping("/downloadQRCode")
    public void downloadQRCode(HttpServletResponse response) throws IOException, WriterException {
        byte[] qrCode = QRCodeGenerator.getQRCodeImage(info, 360, 360);
        String fileName = new String("激情二维码.png".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        // ContentType不设置也没事
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader("content-disposition", "attachment;filename=" + fileName);
        response.setHeader("filename", fileName);
        response.getOutputStream().write(qrCode);
    }

}
