package net.alliancecraft.AllianceBotWebApi.Honeypots;

import jakarta.servlet.http.HttpServletRequest;
import net.alliancecraft.AllianceBotWebApi.IPBanFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GitConfigHoneypotController {

    private static final Logger logger = LoggerFactory.getLogger(GitConfigHoneypotController.class);

    @GetMapping(value = "/.git/config", produces = MediaType.TEXT_PLAIN_VALUE)
    public String fakeGitConfig(HttpServletRequest request) {
        // Loga IP, user agent, e qualquer outra info que quiser
        String ip = IPBanFilter.getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String requestedUrl = request.getRequestURL().toString();

        logger.warn("Tentativa de acesso ao arquivo .git/config detectada!");
        logger.warn("IP: " + ip);
        logger.warn("User-Agent: " + userAgent);
        logger.warn("Referer: " + referer);
        logger.warn("URL solicitada: " + requestedUrl);

        if (!ip.equals("127.0.0.1")) {
            IPBanFilter.banIP(ip);

            logger.warn("Ip banido!");
        }

        // Retorna um arquivo falso .git/config
        return "[core]\n" +
                "repositoryformatversion = 0\n" +
                "filemode = true\n" +
                "bare = false\n" +
                "logallrefupdates = true\n" +
                "[remote \"origin\"]\n" +
                "url = https://cdn.alliancecraft.com.br/allianceutils.git\n" +
                "fetch = +refs/heads/*:refs/remotes/origin/*";
    }
}
