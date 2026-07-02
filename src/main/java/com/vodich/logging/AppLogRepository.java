package com.vodich.logging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppLogRepository extends JpaRepository<AppLog, Long> {
    List<AppLog> findTop200ByOrderByCreatedAtDesc();
    List<AppLog> findTop200ByLevelOrderByCreatedAtDesc(LogLevel level);
}
