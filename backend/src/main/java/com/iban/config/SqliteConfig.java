package com.iban.config;

import javax.sql.DataSource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SqliteConfig {

    private final DataSource dataSource;

    public SqliteConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void enableWalMode() throws Exception {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA cache_size=-65536;");
            stmt.execute("PRAGMA mmap_size=268435456;");
            stmt.execute("PRAGMA temp_store=MEMORY;");
        }
    }
}
