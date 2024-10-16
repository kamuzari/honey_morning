package com.sf.honeymorning.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원")
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

}
