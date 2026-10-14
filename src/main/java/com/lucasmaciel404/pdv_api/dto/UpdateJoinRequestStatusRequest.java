package com.lucasmaciel404.pdv_api.dto;


import com.lucasmaciel404.pdv_api.dto.enums.model.JoinRequestStatus;

public record UpdateJoinRequestStatusRequest(
        JoinRequestStatus status
) {}