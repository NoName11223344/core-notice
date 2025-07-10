package com.baokim.notification_service.features.email.controllers;

import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.features.email.dtos.requests.EmailRequest;
import com.baokim.notification_service.features.email.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Api gửi email")
public class EmailController {

    private final EmailService emailService;

    @Operation(
            summary = "API gửi email",
            description = "Gửi email với nội dung, tiêu đề, người nhận và file đính kèm. Hỗ trợ định dạng multipart/form-data"
    )
    @Parameter(
            name = "Signature",
            description = "Chữ ký điện tử dùng để xác thực request, được tạo bằng thuật toán SHA256withRSA. Dữ liệu ký là trường requestId.",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Vsc/GytjMMInoHQDOD2AwL6GUP4wQ2YZLCEOIIu/N1lhqTgHnusC...")
    )
    @Parameter(
            name = "requestId",
            required = true,
            description = "ID của request (unique), gồm chữ và số, max 100 ký tự",
            example = "REQ202505090001",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "requestTime",
            required = true,
            description = "Thời gian gửi request (yyyy-MM-dd HH:mm:ss)",
            example = "2025-05-09 12:00:00",
            schema = @Schema(type = "string", format = "date-time")
    )
    @Parameter(
            name = "merchantCode",
            required = true,
            description = "Mã định danh merchant",
            example = "BKPLUS",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "fromName",
            required = true,
            description = "Hiển thị trong email kiểu BKPLUS <no-reply@baokim.vn>",
            example = "BKPLUS",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "fromEmail",
            description = "Hiển thị đại diện email người gửi, nếu không gửi mặc định là no-reply@baokim.vn",
            example = "no-reply@baokim.vn",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "to",
            required = true,
            description = "Danh sách người nhận (ngăn cách bởi dấu phẩy)",
            example = "duongnt@baokim.vn,tungduongpd1996@gmail.com",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "cc",
            description = "Danh sách người CC (ngăn cách bởi dấu phẩy)",
            example = "cc1@baokim.vn,cc2@baokim.vn",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "subject",
            required = true,
            description = "Tiêu đề email",
            example = "Thông báo đơn hàng",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "content",
            required = true,
            description = "Nội dung email",
            example = "Cảm ơn bạn đã mua hàng tại Bảo Kim.",
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "contentType",
            required = true,
            description = "1 = text/plain, 2 = text/html",
            example = "2",
            schema = @Schema(type = "integer")
    )
    @Parameter(
            name = "files",
            description = "File đính kèm (có thể chọn nhiều file định dạng  .pdf, .xls, .xlsx, .jpg, .jpeg, .png, .zip, .doc, .docx)",
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "array")
            )
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
                    responseCode = "400",
                    description = "Mã merchant không hợp lệ",
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
    @PostMapping(value = "/sent")
    public ApiResponse sendEmail(@ModelAttribute @Valid EmailRequest request) {
        return emailService.sendEmail(request);
    }
}
