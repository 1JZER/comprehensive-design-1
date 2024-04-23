package com.zuel.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.zuel.dto.biz.InferenceResp;
import com.zuel.service.IdentifyService;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class IdentifyServiceImpl implements IdentifyService {

    @Value("${inference-ip}")        // TODO，为什么@Value不适用于final字段
    private String inferenceIp;

    @Override
    public ResponseEntity<Resource> identify(MultipartFile file) {
        String inferenceURL = inferenceIp + ":5543/v2/models/yolo/infer/from_files";
        Path imagePath = Paths.get("/Users/bayern2jz/Desktop/WUST.jpg");

        HttpResponse resp = HttpRequest.post(inferenceURL)
                .form("request", imagePath.toFile())
                .execute();

        String body = resp.body();
        InferenceResp inferResp = JSONUtil.toBean(body, InferenceResp.class);


        return null;


//        Path imagePath = Paths.get("/Users/bayern2jz/Desktop/WUST.jpg");
//        Resource image = null;
//        try {
//            image = new UrlResource(imagePath.toUri());
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG) // 根据实际图片类型设置
//                .body(image);

    }

    private File drawBoundingBoxes(String inputImagePath, InferenceResp result) {
        Mat image = Imgcodecs.imread(inputImagePath);
        // TODO： 无法加载图像错误
        if (image.empty())
    }

}
