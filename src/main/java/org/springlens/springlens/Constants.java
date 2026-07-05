package org.springlens.springlens;

import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Constants {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final Path LOG_FILE_PATH = Paths.get("spring-lens-history.log");
}
