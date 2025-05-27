package net.alliancecraft.AllianceBotWebApi.imagesControllers;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController {

    @GetMapping("/img/{ticketid}/{name}")
    public ResponseEntity<byte[]> getImagem(@PathVariable("ticketid") String ticketId, @PathVariable("name") String nome) throws IOException {
        Path dirRoot = Paths.get("web/tickets");

        if (!Files.exists(dirRoot)){
            Files.createDirectories(dirRoot);
            return ResponseEntity.status(500).build();
        }

        Path path = dirRoot.resolve(ticketId).resolve(nome);
        if (!Files.exists(path)) {
            return ResponseEntity.status(500).build();
        }

        // Detecta tipo da imagem
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream"; // fallback
        }

        byte[] content = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }


}
