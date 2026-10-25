
package com.lucasmaciel404.pdv_api.controller;

import com.lucasmaciel404.pdv_api.dto.response.DashboardResponse;
import com.lucasmaciel404.pdv_api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/establishment/{establishmentId}")
    public ResponseEntity<DashboardResponse> getDashboard(
            @PathVariable UUID establishmentId,
            @RequestParam(defaultValue = "7d") String period,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                dashboardService.getDashboard(
                        establishmentId,
                        period,
                        authentication.getName()
                )
        );
    }
}
