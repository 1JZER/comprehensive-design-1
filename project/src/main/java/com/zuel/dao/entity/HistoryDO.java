package com.zuel.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zuel.common.database.BaseDO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("t_history")
@Accessors(chain = true)
public class HistoryDO extends BaseDO {
    private Long id;
    private String username;
    private String topic;
    private String imgName;
    private Long deletionTime;  // 停用时间戳
}
