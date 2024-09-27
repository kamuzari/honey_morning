package com.sf.honeymorning.domain.auth.controller;

import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.user.dto.response.UserDetailDto;
import com.sf.honeymorning.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증")
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        // 회원 데이터 생성
        authService.saveUser(user);

        return ResponseEntity.ok("회원가입을 성공하였습니다.");
    }


    @Operation(
            summary = "리프레시 토큰 발급"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "리프레시 토큰 발급 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request,
                                                 HttpServletResponse response) {
        return ResponseEntity.ok("refresh 토큰을 성공적으로 발급하였습니다.");
    }

    @Operation(
            summary = "로그인 유저 정보 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 유저 정보 조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = UserDetailDto.class))
            )
    })
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo() {
        UserDetailDto userDetailDto = authService.getUserInfo();
        return ResponseEntity.ok(userDetailDto);
    }
}
