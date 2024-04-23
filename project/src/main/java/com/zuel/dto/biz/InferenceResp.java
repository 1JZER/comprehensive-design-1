package com.zuel.dto.biz; /**
 * Copyright 2024 bejson.com
 */
import lombok.Data;

import java.util.List;

@Data
public class InferenceResp{

    private List<List<List<Double>>> boxes;
    private List<List<Double>> scores;
    private List<List<String>> labels;
    private String intermediate_outputs;

}