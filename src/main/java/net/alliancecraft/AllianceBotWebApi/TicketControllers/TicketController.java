package net.alliancecraft.AllianceBotWebApi.TicketControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class TicketController {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping("/tickets/{userid}/{ticket}")
    public ResponseEntity<String> getTicket(
            @PathVariable("userid") String userid,
            @PathVariable("ticket") String ticket, Model model) {
        // Caminho para o arquivo HTML
        Path path = Paths.get("transcripts", userid ,ticket + ".html");

        try {
            // Lê o conteúdo do arquivo HTML
            String htmlContent = Files.readString(path);

            // Retorna o conteúdo do HTML com o cabeçalho adequado
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlContent);
        } catch (IOException e) {
            // Em caso de erro ao ler o arquivo
            Context context = new Context();
            context.setVariable("customErroTitle", "404 - Transcrição não Encontrada");
            context.setVariable("customErroMessage", "A transcrição que você está procurando não existe ou ainda não foi gerada!");

            String renderedError = templateEngine.process("errorCustom", context);

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(renderedError);
        }
    }

    @GetMapping("/tickets/{userId}")
    public String getUserTickets(Model model, @PathVariable("userId") String userId) {
        // Aqui, o 'userId' será 'admin' se a URL for '/tickets/admin'
        File directory = new File("transcripts/" + userId);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".html"));
        List<FileInfo> fileInfoList = new ArrayList<>();

        for (File file : files) {
            String path = file.getAbsolutePath();
            long lastModified = file.lastModified();
            fileInfoList.add(new FileInfo(file.getName().replace(".html", ""), path, file.getParentFile().getName(), lastModified));
        }

        model.addAttribute("files", fileInfoList);
        return "transcripts"; // Nome do template a ser renderizado
    }

    @GetMapping("/tickets")
    public String getTickets(Model model) {
        File directory = new File("transcripts");

        // Verifica se o diretório existe
        if (!directory.exists() || !directory.isDirectory()) {
            model.addAttribute("error", "Diretório não encontrado");
            return "errorPage"; // Defina uma página de erro caso o diretório não seja encontrado
        }

        File[] files = directory.listFiles(File::isDirectory); // Apenas diretórios

        // Verifica se o diretório está vazio
        if (files == null || files.length == 0) {
            model.addAttribute("error", "Nenhuma transcrição encontrada");
            return "errorPage"; // Defina uma página de erro ou um template para quando não houver arquivos
        }

        List<String> fileInfoList = new ArrayList<>();

        // Adiciona os nomes dos diretórios à lista
        for (File name : files) {
            fileInfoList.add(name.getName());
        }

        model.addAttribute("files", fileInfoList);
        return "usersTranscripts"; // Nome do template a ser renderizado
    }

    @GetMapping("/")
    public String getTicketsIndex(Model model){
        return "index"; // Nome do template a ser renderizado
    }


}
