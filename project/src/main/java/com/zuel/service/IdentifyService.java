package com.zuel.service;

import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface IdentifyService{
    ResponseEntity<Resource> identify(MultipartFile file);
}
