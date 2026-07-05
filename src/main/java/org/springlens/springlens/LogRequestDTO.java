package org.springlens.springlens;

import java.time.Instant;

public record LogRequestDTO(Instant startDateTime, Instant endDateTime, String levelFilter, String classFilter, String messageFilter, String sorting) {
}
