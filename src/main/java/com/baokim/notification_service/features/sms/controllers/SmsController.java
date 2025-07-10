package com.baokim.notification_service.features.sms.controllers;

import com.baokim.notification_service.features.sms.dtos.requests.sms.BlockSmsSendRequest;
import com.baokim.notification_service.features.sms.dtos.requests.sms.CskhSmsSendRequest;
import com.baokim.notification_service.features.sms.dtos.requests.sms.OtpSmsSendRequest;
import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.features.sms.services.sms.internal.SmsService;
import com.baokim.notification_service.commons.utils.DebugUtil;
import com.baokim.notification_service.commons.utils.SignUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;


@RestController
@RequestMapping("/api/sms/brand-name")
@RequiredArgsConstructor
@Tag(name = "SMS", description = "API gửi tin nhắn SMS")
public class SmsController {
    private final SmsService smsService;

//    @PostMapping("/sent-message")
//    public ApiResponse sendSms(@RequestBody @Valid BaseSmsSendRequest request) {
//        return smsService.sendSms(request);
//    }

    @Operation(
            summary = "API gửi tin OTP",
            description = "API phục vụ cho việc gửi tin OTP ưu tiên tới khách hàng."
    )
    @Parameter(
            name = "Signature",
            description = "Chữ ký điện tử dùng để xác thực request, được tạo bằng thuật toán SHA256withRSA. Dữ liệu ký là chuỗi JSON (json_encode(request->all(), JSON_UNESCAPED_UNICODE)).",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Vsc/GytjMMInoHQDOD2AwL6GUP4wQ2YZLCEOIIu/N1lhqTgHnusC...")
    )
    @Parameter(
            name = "requestId",
            required = true,
            description = "Request id ứng với từng request (định dạng yyyy-MM-dd HH:mm:ss)",
            example = "BKPLUSagva14aad",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "requestTime",
            required = true,
            description = "Request id ứng với từng request",
            example = "2025-01-01 12:00:00",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "merchantCode",
            required = true,
            description = "Mã merchant ứng với từng dịch vụ",
            example = "BKPLUS",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "referenceIdPartner",
            required = true,
            description = "Mã giao dịch bên đối tác",
            example = "W2DH3EvBMULCI",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "to",
            required = true,
            description = "Số điện thoaại gửi tin nhắn",
            example = "0339524455",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "type",
            required = true,
            description = "Loại tin cần gửi (1 là chăm sóc khách hàng, 2 là quảng cáo)",
            example = "1",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "from",
            required = true,
            description = "Mã merchant",
            example = "BKPLUS",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "message",
            required = true,
            description = "Nội dung gửi tin nhắn",
            example = "Ma dang nhap Portal cua ban la: 123123",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "scheduled",
            description = "Lên lịch thời gian gửi tin nhắn (định dạng yyyy-MM-dd HH:mm:ss)",
            example = "2025-02-01 14:00:00",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "useUnicode",
            description = "Tin nhắn có chưa unicode hay không 0 là có, 1 là không, 2 là tự chuyển Unicode sang notUnicode",
            example = "0",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "templateId",
            description = "Id template ứng với template message gửi lên, nếu gửi lên sẽ validate nội dung có đúng với template khai báo hay không",
            example = "10",
            schema = @Schema(type = "int")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 200, \"message\": \"Thành công\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "300",
                    description = "Tin nhắn có chứa từ khoá block",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 300, \"message\": \"Nội dung tin nhắn có chứa từ khoá bị block\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Lỗi yêu cầu",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 400, \"message\": \"Mã merchant không hợp lệ\"}")
                    )
             ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Lỗi server",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 500, \"message\": \"Lỗi server\"}")
                    )
            )
    })
    @PostMapping("/sent-otp")
    public ApiResponse sendSms(@RequestBody @Valid OtpSmsSendRequest request) {
        return smsService.sendSms(request);
    }


    @Operation(
            summary = "API gửi tin tin nhắn block",
            description = "Api gửi tin nhắn đến nhiều số điện thoại cùng một lúc"
    )
    @Parameter(
            name = "Signature",
            description = "Chữ ký điện tử dùng để xác thực request, được tạo bằng thuật toán SHA256withRSA. Dữ liệu ký là chuỗi JSON (json_encode(request->all(), JSON_UNESCAPED_UNICODE)).",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Vsc/GytjMMInoHQDOD2AwL6GUP4wQ2YZLCEOIIu/N1lhqTgHnusC...")
    )
    @Parameter(
            name = "requestId",
            required = true,
            description = "Request id ứng với từng request (định dạng yyyy-MM-dd HH:mm:ss)",
            example = "BKPLUSagva14aad",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "requestTime",
            required = true,
            description = "Request id ứng với từng request",
            example = "2025-01-01 12:00:00",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "merchantCode",
            required = true,
            description = "Mã merchant ứng với từng dịch vụ",
            example = "BKPLUS",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "referenceIdPartner",
            required = true,
            description = "Mã giao dịch bên đối tác",
            example = "W2DH3EvBMULCI",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "to",
            required = true,
            description = "Số điện thoaại gửi tin nhắn, mỗi số cách nhau bởi kí tự ','",
            example = "0339524455,0988801212",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "type",
            required = true,
            description = "Loại tin cần gửi (1 là chăm sóc khách hàng, 2 là quảng cáo)",
            example = "1",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "from",
            required = true,
            description = "Mã merchant",
            example = "BKPLUS",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "message",
            required = true,
            description = "Nội dung gửi tin nhắn",
            example = "Ma dang nhap Portal cua ban la: 123123",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "scheduled",
            description = "Lên lịch thời gian gửi tin nhắn (định dạng yyyy-MM-dd HH:mm:ss)",
            example = "2025-02-01 14:00:00",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "useUnicode",
            description = "Tin nhắn có chưa unicode hay không 0 là có, 1 là không, 2 là tự chuyển Unicode sang notUnicode",
            example = "0",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "templateId",
            description = "Id template ứng với template message gửi lên, nếu gửi lên sẽ validate nội dung có đúng với template khai báo hay không",
            example = "10",
            schema = @Schema(type = "int")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 200, \"message\": \"Thành công\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "300",
                    description = "Tin nhắn có chứa từ khoá block",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 300, \"message\": \"Nội dung tin nhắn có chứa từ khoá bị block\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Lỗi yêu cầu",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 400, \"message\": \"Mã merchant không hợp lệ\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Lỗi server",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 500, \"message\": \"Lỗi server\"}")
                    )
            )
    })
    @PostMapping("/sent-block")
    public ApiResponse sentBlock(@RequestBody @Valid BlockSmsSendRequest request) {
        return smsService.sendSms(request);
    }

    @Operation(
            summary = "API gửi tin tin nhắn cskh",
            description = "API gửi tin tin nhắn cskh"
    )
    @Parameter(
            name = "Signature",
            description = "Chữ ký điện tử dùng để xác thực request, được tạo bằng thuật toán SHA256withRSA. Dữ liệu ký là chuỗi JSON (json_encode(request->all(), JSON_UNESCAPED_UNICODE)).",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Vsc/GytjMMInoHQDOD2AwL6GUP4wQ2YZLCEOIIu/N1lhqTgHnusC...")
    )
    @Parameter(
            name = "requestId",
            required = true,
            description = "Request id ứng với từng request (định dạng yyyy-MM-dd HH:mm:ss)",
            example = "BKPLUSagva14aad",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "requestTime",
            required = true,
            description = "Request id ứng với từng request",
            example = "2025-01-01 12:00:00",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "merchantCode",
            required = true,
            description = "Mã merchant ứng với từng dịch vụ",
            example = "BKPLUS",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "referenceIdPartner",
            required = true,
            description = "Mã giao dịch bên đối tác",
            example = "W2DH3EvBMULCI",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "to",
            required = true,
            description = "Số điện thoaại gửi tin nhắn, mỗi số cách nhau bởi kí tự ','",
            example = "0339524455,0988801212",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "type",
            required = true,
            description = "Loại tin cần gửi (1 là chăm sóc khách hàng, 2 là quảng cáo)",
            example = "1",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "from",
            required = true,
            description = "Mã merchant",
            example = "BKPLUS",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "message",
            required = true,
            description = "Nội dung gửi tin nhắn",
            example = "Ma dang nhap Portal cua ban la: 123123",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "scheduled",
            description = "Lên lịch thời gian gửi tin nhắn (định dạng yyyy-MM-dd HH:mm:ss)",
            example = "2025-02-01 14:00:00",
            schema = @Schema(type = "String")
    )
    @Parameter(
            name = "useUnicode",
            description = "Tin nhắn có chưa unicode hay không 0 là có, 1 là không, 2 là tự chuyển Unicode sang notUnicode",
            example = "0",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "templateId",
            description = "Id template ứng với template message gửi lên, nếu gửi lên sẽ validate nội dung có đúng với template khai báo hay không",
            example = "10",
            schema = @Schema(type = "int")
    )
    @Parameter(
            name = "telco",
            description = "Mã ứng với nhà mạng muốn gửi (01: MobiFone, 02: VinaPhone, 04: Viettel, 05: Vietnamobile, 07: Gtel Mobile, 08: Indochina Telecom)",
            example = "04",
            schema = @Schema(type = "String")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 200, \"message\": \"Thành công\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "300",
                    description = "Tin nhắn có chứa từ khoá block",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 300, \"message\": \"Nội dung tin nhắn có chứa từ khoá bị block\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Lỗi yêu cầu",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 400, \"message\": \"Mã merchant không hợp lệ\"}")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Lỗi server",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"code\": 500, \"message\": \"Lỗi server\"}")
                    )
            )
    })
    @PostMapping("/sent-cskh")
    public ApiResponse sendCskh(@RequestBody @Valid CskhSmsSendRequest request) {
        return smsService.sendSms(request);
    }
}
