package com.zuel.dto.biz;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class History {
    private String topic;
}
