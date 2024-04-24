package com.zuel.common.constant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Id2Label {
    private final Map<Integer, String> idToLabel;

    public Id2Label(@Value("classpath:id2label.json") Resource resource) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            idToLabel = mapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Map<String, Object>>>() {
                    }).entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> Integer.parseInt(entry.getKey()),
                            entry -> (String) entry.getValue().get("name")
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String convert(int id) {
        // 将String id转换为Integer，并查找对应的标签
        return idToLabel.getOrDefault(id, "Unknown");
    }

}
