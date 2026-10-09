package com.lucasmaciel404.pdv_api.dto.response;

import java.util.List;

public record FindAllEstablishmentsResponse(
        List<CreateEstablishmentResponse> establishments
) {}