package com.zuel.convention.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局返回对象
 */
@Data
// 如果设置为 true，setter 方法将返回 this（当前对象的引用），而不是 void。
// 这允许链式调用setter方法，即 obj.setOne(val1).setTwo(val2).setThree(val3);
@Accessors(chain = true)
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 5679018624309023727L;      // serialVersionUID 提供了一个明确的版本标识，以便在序列化和反序列化过程中确保类的版本一致性。

    /**
     * 正确返回码
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
