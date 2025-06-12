package net.alliancecraft.AllianceBotWebApi.userControllers;

import jakarta.servlet.http.HttpServletRequest;
import net.alliancecraft.AllianceBotWebApi.services.BotWebSocketService;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final String BASE_DIR = "api/users/";
    private static final String BASE_DIR_BADGES = "api/badges/";
    private static final long RATE_LIMIT_INTERVAL_MS = 5000; // 5 segundos
    private final Map<String, Long> ipAccessMap = new ConcurrentHashMap<>();

    // Tokens válidos
    private static final Set<String> validTokens = Set.of(
            "MQWOLuEDV3MUphSAdlkS6XvHv2qlJ7HCX7zKwvSJF4qAjVglULyk600X7PbDx9EM"
    );

    @GetMapping("/badges")
    public ResponseEntity<String> getUserJson(
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Token inválido ou ausente\"}");
        }

        Path filePath = Paths.get(BASE_DIR_BADGES + "badges.json");

        try {
            if (!Files.exists(filePath)) {
                Files.createDirectory(filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Arquivo não encontrado\"}");
            }

            String json = Files.readString(filePath);
            return ResponseEntity.ok(json);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Erro ao ler o JSON\"}");
        }
    }

    @GetMapping("/users/{id}.json")
    public ResponseEntity<String> getUserJson(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Token inválido ou ausente\"}");
        }

        Path filePath = Paths.get(BASE_DIR + id + ".json");

        try {
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Arquivo não encontrado\"}");
            }

            String json = Files.readString(filePath);
            return ResponseEntity.ok(json);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Erro ao ler o JSON\"}");
        }
    }

    @PutMapping("/users/{id}.json")
    public ResponseEntity<String> updateUserJson(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody String requestBody
    ) {
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Token inválido ou ausente\"}");
        }

        Path dirPath = Paths.get(BASE_DIR);
        Path filePath = dirPath.resolve(id + ".json");

        try {
            // Garante que o diretório BASE_DIR existe
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Verifica se existe um diretório no lugar do arquivo
            if (Files.exists(filePath) && Files.isDirectory(filePath)) {
                Files.walk(filePath) // Deleta tudo dentro se for diretório
                        .sorted(Comparator.reverseOrder()) // Deleta filhos antes dos pais
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

            // Se o arquivo não existir, cria ele com "{}"
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "{}", StandardCharsets.UTF_8);
            }

            // Lê JSON existente
            JSONObject existing = new JSONObject(Files.readString(filePath));

            // Atualiza com os dados recebidos
            JSONObject updates = new JSONObject(requestBody);
            for (String key : updates.keySet()) {
                existing.put(key, updates.get(key));
            }

            // Salva o JSON atualizado
            Files.writeString(filePath, existing.toString(4), StandardCharsets.UTF_8);

            return ResponseEntity.ok(existing.toString(4));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Erro ao salvar ou JSON inválido\"}");
        }
    }

    @GetMapping("/bot/status")
    public ResponseEntity<String> getBotStatus(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        Long lastAccess = ipAccessMap.get(ip);
        if (lastAccess != null && now - lastAccess < RATE_LIMIT_INTERVAL_MS) {
            return ResponseEntity.status(429).body("{\"error\": \"Você está enviando muitas requisições. Aguarde alguns segundos.\"}");
        }

        ipAccessMap.put(ip, now);

        try {
            BotWebSocketService webSocketService = new BotWebSocketService();
            return ResponseEntity.ok(new JSONObject(webSocketService.getResponse()).toString(4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTokenValid(String token) {
        return token != null && validTokens.contains(token.trim());
    }
}