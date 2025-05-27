package net.alliancecraft.AllianceBotWebApi.Honeypots;

import jakarta.servlet.http.HttpServletRequest;
import net.alliancecraft.AllianceBotWebApi.IPBanFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AdminHoneyPot {

    private static final Logger logger = LoggerFactory.getLogger(GitConfigHoneypotController.class);

    @GetMapping("/admin")
    public String getTicketsIndex(Model model, HttpServletRequest request){
        // Loga IP, user agent, e qualquer outra info que quiser
        String ip = IPBanFilter.getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String requestedUrl = request.getRequestURL().toString();

        logger.warn("Tentativa de acesso ao arquivo /admin detectada!");
        logger.warn("IP: " + ip);
        logger.warn("User-Agent: " + userAgent);
        logger.warn("Referer: " + referer);
        logger.warn("URL solicitada: " + requestedUrl);

        if (!ip.equals("127.0.0.1")) {
            IPBanFilter.banIP(ip);

            logger.warn("Ip banido!");
        }

        return "admin";
    }

}
