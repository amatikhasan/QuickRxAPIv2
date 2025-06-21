package com.example.quickrx.controller;

import com.example.quickrx.dto.AppInfoResponseDto;
import com.example.quickrx.dto.AppInfoUpdateRequestDto;
import com.example.quickrx.service.AppInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app-info")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    // Optional: Initialize AppInfo on startup if it doesn't exist.
    // This could be done via a CommandLineRunner bean.
    // @PostConstruct
    // public void init() {
    //     appInfoService.initializeAppInfoIfNotExist();
    // }


    @GetMapping
    public ResponseEntity<AppInfoResponseDto> getAppInfo() {
        // Consider initializing AppInfo if it doesn't exist, or ensure it's seeded.
        // The service method currently throws if not found, which is fine.
        // Admin should create/update it first.
        AppInfoResponseDto appInfo = appInfoService.getAppInfo();
        return ResponseEntity.ok(appInfo);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppInfoResponseDto> updateAppInfo(@Valid @RequestBody AppInfoUpdateRequestDto updateRequestDto) {
        AppInfoResponseDto updatedAppInfo = appInfoService.updateAppInfo(updateRequestDto);
        return ResponseEntity.ok(updatedAppInfo);
    }
}
