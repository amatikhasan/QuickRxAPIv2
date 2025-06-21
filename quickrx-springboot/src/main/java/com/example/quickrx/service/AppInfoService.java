package com.example.quickrx.service;

import com.example.quickrx.dto.AppInfoResponseDto;
import com.example.quickrx.dto.AppInfoUpdateRequestDto;
import com.example.quickrx.model.AppInfo;
import com.example.quickrx.repository.AppInfoRepository;
import com.example.quickrx.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AppInfoService {

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Transactional(readOnly = true)
    public AppInfoResponseDto getAppInfo() {
        // AppInfo typically has one row. If it can be empty, handle appropriately.
        AppInfo appInfo = appInfoRepository.findFirst()
                .orElseGet(() -> {
                    // If no AppInfo exists, return a default/empty one or throw exception
                    // For now, let's create a default one if it doesn't exist upon first request.
                    // This behavior might be better handled by a data initializer on startup.
                    // Or, simply throw ResourceNotFoundException if it must exist.
                    // For this conversion, the PHP code implies it expects one row.
                    // Let's assume it should ideally exist.
                    // If we allow creation on the fly:
                    // AppInfo newInfo = new AppInfo(); /* set defaults */ return appInfoRepository.save(newInfo);
                    // Throwing for now, admin should create it.
                    throw new ResourceNotFoundException("Application information has not been configured yet.");
                });
        return AppInfoResponseDto.fromEntity(appInfo);
    }

    @Transactional
    public AppInfoResponseDto updateAppInfo(AppInfoUpdateRequestDto updateRequestDto) {
        // Assuming there's one AppInfo record. If multiple, need an ID.
        // The PHP code implies a single record (SELECT ... LIMIT 1)
        AppInfo appInfo = appInfoRepository.findFirst()
                .orElseGet(() -> {
                    // If it doesn't exist, create it.
                    AppInfo newInfo = new AppInfo();
                    newInfo.setCreatedAt(LocalDateTime.now()); // Set creation time for new record
                    return newInfo;
                });

        appInfo.setSubscriptionFee(updateRequestDto.getSubscriptionFee());
        appInfo.setAccountNumber(updateRequestDto.getAccountNumber());
        appInfo.setHotline(updateRequestDto.getHotline());
        appInfo.setFacebookLink(updateRequestDto.getFacebookLink());
        appInfo.setAboutUs(updateRequestDto.getAboutUs());
        appInfo.setTermsAndCondition(updateRequestDto.getTermsAndCondition());
        // updatedAt is handled by @UpdateTimestamp

        AppInfo updatedAppInfo = appInfoRepository.save(appInfo);
        return AppInfoResponseDto.fromEntity(updatedAppInfo);
    }

    // Method to initialize AppInfo if it doesn't exist (e.g. called on startup)
    @Transactional
    public void initializeAppInfoIfNotExist() {
        if (appInfoRepository.findFirst().isEmpty()) {
            AppInfo newInfo = new AppInfo();
            // Set some default values if necessary
            newInfo.setSubscriptionFee(java.math.BigDecimal.ZERO);
            newInfo.setAccountNumber("Default Account Number");
            newInfo.setHotline("Default Hotline");
            newInfo.setFacebookLink("");
            newInfo.setAboutUs("Default About Us");
            newInfo.setTermsAndCondition("Default Terms");
            newInfo.setCreatedAt(LocalDateTime.now());
            appInfoRepository.save(newInfo);
        }
    }
}
