package org.sanosysalvos.controller;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.*;
import org.sanosysalvos.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/conversaciones")
    public ResponseEntity<ConversacionDTO> crearOObtener(
            Authentication auth,
            @RequestBody CrearConversacionRequest request) {
        String emailActual = auth.getName();
        return ResponseEntity.ok(chatService.crearOObtenerConversacion(emailActual, request.emailOtroUsuario()));
    }

    @GetMapping("/conversaciones")
    public ResponseEntity<List<ConversacionDTO>> listar(Authentication auth) {
        return ResponseEntity.ok(chatService.listarConversaciones(auth.getName()));
    }

    @GetMapping("/conversaciones/{id}/mensajes")
    public ResponseEntity<List<MensajeDTO>> mensajes(
            @PathVariable Long id,
            Authentication auth) {
        chatService.marcarLeidos(id, auth.getName());
        return ResponseEntity.ok(chatService.listarMensajes(id, auth.getName()));
    }

    @PostMapping("/conversaciones/{id}/mensajes")
    public ResponseEntity<MensajeDTO> enviar(
            @PathVariable Long id,
            Authentication auth,
            @RequestBody EnviarMensajeRequest request) {
        return ResponseEntity.ok(chatService.enviarMensaje(id, auth.getName(), request.contenido()));
    }
}