package com.zuel.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuel.common.biz.user.UserContext;
import com.zuel.common.exception.ClientException;
import com.zuel.convention.result.Result;
import com.zuel.convention.result.Results;
import com.zuel.dao.entity.HistoryDO;
import com.zuel.dao.mapper.HistoryMapper;
import com.zuel.dto.biz.History;
import com.zuel.dto.biz.InferenceResp;
import com.zuel.dto.resp.ListHistoryRespDTO;
import com.zuel.service.IdentifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentifyServiceImpl extends ServiceImpl<HistoryMapper, HistoryDO> implements IdentifyService {


    private final HistoryMapper historyMapper;
    @Value("${img-storage-path}")
    private String outputImagePath;
    @Value("${inference-ip}")        // TODO，为什么@Value不适用于final字段
    private String inferenceIp;

    @Override
    public ResponseEntity<Resource> identify(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ClientException("文件不能为空");
        }
        try {
            InferenceResp inferResp = sendPostRequest(file);
            BufferedImage image = null;
            image = ImageIO.read(file.getInputStream());
            BufferedImage imageWithBox = drawBoundingBoxes(image, inferResp);

            // 保存文件
            String fileName = UUID.randomUUID() + ".png";
            File outoutFile = new File(outputImagePath + "/" + fileName);
            // TODO 此处可异步执行？
            ImageIO.write(imageWithBox, "PNG", outoutFile);

            // 文件入库
            HistoryDO historyDO = new HistoryDO();
            historyDO.setUsername(UserContext.getUsername())
                    .setTopic("历史记录n")
                    .setImgName(fileName);     // TODO: 获取主题
            save(historyDO);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(outoutFile));

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<ListHistoryRespDTO> listHistory() {
        LambdaQueryWrapper<HistoryDO> lambdaQueryWrapper = new LambdaQueryWrapper<HistoryDO>()
                .eq(HistoryDO::getUsername, UserContext.getUsername())
                .orderByDesc(HistoryDO::getCreateTime);
        List<HistoryDO> histories = historyMapper.selectList(lambdaQueryWrapper);
        List<History> res = histories.stream().map((item) -> new History().setTopic(item.getTopic())).toList();
        ListHistoryRespDTO listHistoryRespDTO = new ListHistoryRespDTO();
        listHistoryRespDTO.setHistoryList(res);
        return Results.success(listHistoryRespDTO);
    }

    private InferenceResp sendPostRequest(MultipartFile file) {
        String inferenceURL = "http://" + inferenceIp + ":5543/v2/models/yolo/infer/from_files";
        if (file.isEmpty()) {
            throw new ClientException("文件不能为空");
        }
        try {
            // 创建 HTTP 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 创建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("request", new HttpEntity<>(file.getBytes(), createFileHeaders(file)));

            // 创建 HTTP 请求实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 发送请求并获取响应
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Resource> responseEntity = restTemplate.exchange(inferenceURL, HttpMethod.POST, requestEntity, Resource.class);
            String responseBody = StreamUtils.copyToString(responseEntity.getBody().getInputStream(), StandardCharsets.UTF_8);

            return JSONUtil.toBean(responseBody, InferenceResp.class);

        } catch (IOException e) {
            // 处理异常
            throw new ClientException("请求失败: " + e.getMessage());
        }
    }

    // 创建文件上传请求的 HTTP 头
    private HttpHeaders createFileHeaders(MultipartFile file) {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        fileHeaders.setContentDispositionFormData("request", file.getOriginalFilename());
        return fileHeaders;
    }


    private BufferedImage drawBoundingBoxes(BufferedImage image, InferenceResp result) {
        // 获取图像的图形上下文
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED); // 设置边界框颜色为红色
        graphics.setStroke(new BasicStroke(2)); // 设置边界框的线宽

        // 绘制边界框
        for (List<List<Double>> objectBoxes : result.getBoxes()) {
            for (List<Double> box : objectBoxes) {
                int x = box.get(0).intValue();
                int y = box.get(1).intValue();
                int width = (int) (box.get(2) - box.get(0));
                int height = (int) (box.get(3) - box.get(1));
                graphics.drawRect(x, y, width, height);
            }
        }
        graphics.dispose(); // 释放资源
        return image;
    }

}
