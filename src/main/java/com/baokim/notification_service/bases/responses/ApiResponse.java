package com.baokim.notification_service.bases.responses;

import com.baokim.notification_service.commons.constants.ResponseConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
@JsonPropertyOrder({"code", "message", "data"}) // trả về thứ tự lần lượt như này
public class ApiResponse <T>{
    private String message = ResponseConstants.SUCCESS_MESSAGE;
    private int code = ResponseConstants.SUCCESS_CODE;
    private T data;
}