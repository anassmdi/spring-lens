package io.github.anassmdi.springlens;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static io.github.anassmdi.springlens.Constants.*;

@Controller
@RequestMapping("${spring-lens.path:/spring-lens}")
public class SpringLensController {

    @Value("${spring-lens.password:admin}")
    private String password;

    /* return index.html */
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> index() {
        Resource htmlFile = new ClassPathResource("META-INF/resources/springlens/index.html");
        return ResponseEntity.ok().body(htmlFile);
    }

    /* Api to retrieve assets */
    @GetMapping("/assets/{asset}")
    public ResponseEntity<Resource> asset(@PathVariable("asset") String filename) {
        Resource assetResource = new ClassPathResource("META-INF/resources/springlens/assets/" + filename);
        if (assetResource.exists()) {
            return ResponseEntity.ok().body(assetResource);
        }
        return ResponseEntity.notFound().build();
    }

    /* Api to authenticate */
    @GetMapping("/api/login")
    public ResponseEntity<Boolean> authenticate(HttpServletRequest request) {
        return ResponseEntity.ok(isAuthorizationValid(request));
    }

    /* Api to filter & sort & retrieve logs */
    @ResponseBody
    @PostMapping("/api/logs")
    public ResponseEntity<List<SpringLensLog>> getLiveLogs(HttpServletRequest request, @RequestBody LogRequestDTO logRequestDTO) throws BadRequestException {
        validateAuthorization(request);

        List<SpringLensLog> logs = new ArrayList<>();

        if (!Files.exists(LOG_FILE_PATH)) {
            return ResponseEntity.ok(logs);
        }

        try {
            List<String> lines = Files.readAllLines(LOG_FILE_PATH);

            // retrieve logs by line
            for (String line : lines) {
                if (!line.isBlank()) {
                    logs.add(OBJECT_MAPPER.readValue(line.trim(), SpringLensLog.class));
                }
            }

            // if request dto is null throw exception
            if (logRequestDTO == null) {
                throw new BadRequestException("filter not provided");
            }

            // filter by start/end dateTime
            if (logRequestDTO.startDateTime() != null && logRequestDTO.endDateTime() != null) {
                logs.removeIf(log -> {
                    var logInstant = FORMATTER.parse(log.timestamp(), Instant::from);
                    return logInstant.isBefore(logRequestDTO.startDateTime()) || logInstant.isAfter(logRequestDTO.endDateTime());
                });
            }

            // filter by log level
            if (logRequestDTO.levelFilter() != null && !logRequestDTO.levelFilter().isBlank()) {
                logs.removeIf(log -> removeFilterChecker(logRequestDTO.levelFilter(), log.level()));
            }

            // filter by log class
            if (logRequestDTO.classFilter() != null && !logRequestDTO.classFilter().isBlank()) {
                logs.removeIf(log -> removeFilterChecker(logRequestDTO.classFilter(), log.logger()));
            }

            // filter by log message
            if (logRequestDTO.messageFilter() != null && !logRequestDTO.messageFilter().isBlank()) {
                logs.removeIf(log -> removeFilterChecker(logRequestDTO.messageFilter(), log.message()));
            }

            // sort by timestamp
            if (logRequestDTO.sorting() != null && !logRequestDTO.sorting().isBlank()) {
                if (logRequestDTO.sorting().equals("a")) {
                    logs.sort(Comparator.comparing((SpringLensLog log) -> FORMATTER.parse(log.timestamp(), Instant::from)));
                }
                if (logRequestDTO.sorting().equals("d")) {
                    logs.sort(Comparator.comparing((SpringLensLog log) -> FORMATTER.parse(log.timestamp(), Instant::from)).reversed());
                }
            }

            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            // return error if file read exception
            return ResponseEntity.ok(List.of(new SpringLensLog("", "ERROR", "SpringLens", "Failed to load logs from file.", "")));
        }
    }

    /* filters predicate checker */
    private boolean removeFilterChecker(String phrase, String element) {
        var strings = phrase.split("[^a-zA-Z0-9]+");
        return Arrays.stream(strings).noneMatch(str -> element.toLowerCase().contains(str.toLowerCase()));
    }

    /* validate api authorization */
    private void validateAuthorization(HttpServletRequest request) throws BadRequestException {
        if (!isAuthorizationValid(request)) {
            throw new BadRequestException("Authorization required");
        }
    }

    /* check if authorieation is valid */
    private Boolean isAuthorizationValid(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.equals(password);
    }
}
