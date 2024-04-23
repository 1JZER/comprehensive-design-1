package com.zuel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class IdentifyController {

    // 直接返回图片流
    @PostMapping("/api/comprehensive-design/admin/v1/doIdentify")
    public ResponseEntity<Resource> identify(@RequestParam("file") MultipartFile file) throws IOException {
        Path imagePath = Paths.get("/Users/bayern2jz/Desktop/WUST.jpg");
        Resource image = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 根据实际图片类型设置
                .body(image);
    }
}
