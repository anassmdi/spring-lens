package io.github.anassmdi.springlens;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import static io.github.anassmdi.springlens.Constants.*;

public class SpringLensAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SpringLensAppender.class);

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null) return;

        try {
            // retrieve log stackTrace in case of error
            String stackTrace = "";
            if (event.getThrowableProxy() != null) {
                stackTrace = ThrowableProxyUtil.asString(event.getThrowableProxy());
            }

            // format log timestamp
            String timestamp = FORMATTER.format(Instant.ofEpochMilli(event.getTimeStamp()));

            SpringLensLog log = new SpringLensLog(
                    timestamp,
                    event.getLevel().toString(),
                    event.getLoggerName(),
                    event.getFormattedMessage(),
                    stackTrace
            );

            // write log line
            String jsonLine = OBJECT_MAPPER.writeValueAsString(log);
            Files.writeString(LOG_FILE_PATH, jsonLine + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("[Spring Lens] Failed writing log line to file : {}", e.getMessage());
        }
    }
}
