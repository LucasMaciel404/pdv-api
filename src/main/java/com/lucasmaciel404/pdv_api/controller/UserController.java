package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.response.UserSummaryResponse;
import com.lucasmaciel404.pdv_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/by-ids")
    public ResponseEntity<List<UserSummaryResponse>> findByIds(
            @RequestBody List<UUID> ids
    ) {
        return ResponseEntity.ok(userService.findByIds(ids));
    }
}