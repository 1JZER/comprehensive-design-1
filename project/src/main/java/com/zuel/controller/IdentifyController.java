package com.zuel.controller;

import com.zuel.convention.result.Result;
import com.zuel.convention.result.Results;
import com.zuel.dto.resp.ListHistoryRespDTO;
import com.zuel.service.IdentifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/api/comprehensive-design/admin/v1/listHistory")
    public Result<ListHistoryRespDTO> listHistory() {
        return Results.success(identifyService.listHistory());
    }

    @GetMapping("/api/comprehensive-design/admin/v1/queryHistory")
    public ResponseEntity<Resource> queryHistory(String fileName) {
        return identifyService.queryHistory(fileName);
    }

}
