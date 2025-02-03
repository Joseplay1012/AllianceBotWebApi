package net.alliancecraft.AllianceBotWebApi.TicketControllers;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura erros do tipo NoHandlerFoundException (por exemplo, 404 - página não encontrada)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundError(Model model) {
        model.addAttribute("errorMessage", "Página não encontrada!");
        return "error";  // Nome do template de erro
    }

    // Captura todos os outros tipos de exceções
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalError(Model model, Exception ex) {
        model.addAttribute("errorMessage", "Ocorreu um erro inesperado.");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error";  // Nome do template de erro
    }
}
