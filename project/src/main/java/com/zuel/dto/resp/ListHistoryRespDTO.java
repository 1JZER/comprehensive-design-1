package com.zuel.dto.resp;

import com.zuel.dto.biz.History;
import lombok.Data;

import java.util.List;

@Data
public class ListHistoryRespDTO {
    List<History> historyList;
}
