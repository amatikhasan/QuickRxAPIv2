package com.example.quickrx.repository;

import com.example.quickrx.model.AppInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Moved import to the top

@Repository
public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {
    // Typically, there's only one row in AppInfo.
    // You might add a method to fetch the first/only record.
    default Optional<AppInfo> findFirst() {
        return findAll().stream().findFirst();
    }
}
