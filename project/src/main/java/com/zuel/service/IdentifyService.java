package com.zuel.service;

import com.zuel.convention.result.Result;
import com.zuel.dto.resp.ListHistoryRespDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface IdentifyService{
    ResponseEntity<Resource> identify(MultipartFile file);

    ListHistoryRespDTO listHistory();

    ResponseEntity<Resource> queryHistory(String fileName);

}
