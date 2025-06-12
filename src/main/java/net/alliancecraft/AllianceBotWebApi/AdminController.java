package net.alliancecraft.AllianceBotWebApi;


import net.alliancecraft.AllianceBotWebApi.services.BotWebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/")
    public String getIndex(Model model){
        //model.addAttribute("port", 11296);

        return "index"; // Nome do template a ser renderizado
    }
}
