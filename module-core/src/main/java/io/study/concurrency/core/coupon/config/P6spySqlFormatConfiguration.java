package io.study.concurrency.core.coupon.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;

public class P6spySqlFormatConfiguration implements MessageFormattingStrategy {
    public static final String CREATE = "create";
    public static final String ALTER = "alter";
    public static final String COMMENT = "comment";
    public static final String EXECUTE_QUERY_LOG_FORMAT = "\n[Execute info] : %s | connection id : %s | duration : %d ms\n[Hibernate format] : %s";

    @Override
    public String formatMessage(
        int connectionId,
        String now,
        long duration,
        String category,
        String prepared,
        String sql,
        String url
    ) {
        return String.format(
            EXECUTE_QUERY_LOG_FORMAT,
            epochMilliToLocalDateTime(now),
            connectionId,
            duration,
            formatSql(category, sql)
        );
    }

    private LocalDateTime epochMilliToLocalDateTime(String now) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(now)), TimeZone.getDefault().toZoneId());
    }

    private String formatSql(String category, String sql) {
        if (isStatementCategory(category)) {
            String statement = sql.trim().toLowerCase();
            if (isDataDefinitionLanguage(statement)) {
                return toFormat(FormatStyle.DDL, sql);
            }
            return toFormat(FormatStyle.BASIC, sql);
        }
        return sql;
    }

    private boolean isStatementCategory(String category) {
        return Category.STATEMENT.getName().equals(category);
    }

    private boolean isDataDefinitionLanguage(String sql) {
        return sql.startsWith(CREATE) || sql.startsWith(ALTER) || sql.startsWith(COMMENT);
    }

    private String toFormat(FormatStyle formatStyle, String sql) {
        return getFormatter(formatStyle).format(sql);
    }

    private Formatter getFormatter(FormatStyle formatStyle) {
        return formatStyle.getFormatter();
    }
}
