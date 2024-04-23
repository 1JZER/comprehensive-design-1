package com.zuel.controller;

import com.zuel.service.IdentifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
public class IdentifyController {

    private final IdentifyService identifyService;

    // 直接返回图片流
    @PostMapping("/api/comprehensive-design/admin/v1/doIdentify")
    public ResponseEntity<Resource> identify(@RequestParam("file") MultipartFile file) {
        return identifyService.identify(file);
    }
}
