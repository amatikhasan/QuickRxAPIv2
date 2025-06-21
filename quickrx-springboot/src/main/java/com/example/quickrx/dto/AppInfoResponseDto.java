package com.example.quickrx.dto;

import com.example.quickrx.model.AppInfo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppInfoResponseDto {
    private Long id;
    private BigDecimal subscriptionFee;
    private String accountNumber;
    private String hotline;
    private String facebookLink;
    private String aboutUs;
    private String termsAndCondition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AppInfoResponseDto fromEntity(AppInfo appInfo) {
        if (appInfo == null) return null;
        AppInfoResponseDto dto = new AppInfoResponseDto();
        dto.setId(appInfo.getId());
        dto.setSubscriptionFee(appInfo.getSubscriptionFee());
        dto.setAccountNumber(appInfo.getAccountNumber());
        dto.setHotline(appInfo.getHotline());
        dto.setFacebookLink(appInfo.getFacebookLink());
        dto.setAboutUs(appInfo.getAboutUs());
        dto.setTermsAndCondition(appInfo.getTermsAndCondition());
        dto.setCreatedAt(appInfo.getCreatedAt()); // Might be null if not set
        dto.setUpdatedAt(appInfo.getUpdatedAt());
        return dto;
    }
}
