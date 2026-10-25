package com.lucasmaciel404.pdv_api.dto.response;

import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record TableResponse(
        UUID id,
        Integer number,
        TableStatusEnum status,
        LocalDateTime createdAt,
        UUID establishmentId,
        UUID cardId,
        Boolean active
) {}
