package org.sanosysalvos.dto;

import java.time.LocalDateTime;

public record ConversacionDTO(
        Long idConversacion,
        String idUsuario1,
        String idUsuario2,
        String otroUsuario,
        String ultimoMensaje,
        LocalDateTime createdAt
) {}