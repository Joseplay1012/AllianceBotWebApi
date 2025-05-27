package net.alliancecraft.AllianceBotWebApi;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IPBanFilter extends OncePerRequestFilter {

    // Lista de IPs banidos (thread-safe)
    private static final Set<String> bannedIPs = ConcurrentHashMap.newKeySet();

    // Adicionar IP na lista de banidos
    public static void banIP(String ip) {
        bannedIPs.add(ip);
        System.out.println("🔒 IP banido: " + ip);
    }

    // Verificar se IP está banido
    public static boolean isIPBanned(String ip) {
        return bannedIPs.contains(ip);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = getClientIP(request);

        if (isIPBanned(ip)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Acesso negado.");
            response.getWriter().write(" Seu ip está banido!");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Captura IP real, considerando proxy/reverso
    public static String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    public static void saveBanIps() {
        try {
            File bannedFile = new File("web/bannedips.json");

            if (!bannedFile.exists()) {
                bannedFile.getParentFile().mkdirs();
                bannedFile.createNewFile();

                FileWriter writer = new FileWriter(bannedFile);
                writer.write("{}");
                writer.close();
            }

            // Lê o JSON atual
            String content = new String(Files.readAllBytes(bannedFile.toPath()));
            JSONObject jsonObject = new JSONObject(content);

            // Adiciona IPs banidos
            for (String ip : bannedIPs) {
                if (!jsonObject.has(ip)) {
                    jsonObject.put(ip, true);
                }
            }

            // Salva de volta no arquivo
            FileWriter writer = new FileWriter(bannedFile);
            writer.write(jsonObject.toString(4));
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadBanIps() {
        try {
            File bannedFile = new File("web/bannedips.json");

            if (!bannedFile.exists()) {
                if (bannedFile.getParentFile() != null) {
                    bannedFile.getParentFile().mkdirs();
                }
                bannedFile.createNewFile();

                try (FileWriter writer = new FileWriter(bannedFile)) {
                    writer.write("{}");
                }
            }

            String content = new String(Files.readAllBytes(bannedFile.toPath()));
            if (content.isEmpty()) {
                content = "{}";
            }

            JSONObject jsonObject = new JSONObject(content);

            for (String s : jsonObject.keySet()) {
                bannedIPs.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
