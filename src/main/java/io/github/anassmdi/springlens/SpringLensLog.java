package io.github.anassmdi.springlens;

public record SpringLensLog(String timestamp, String level, String logger, String message, String stackTrace) {
}
