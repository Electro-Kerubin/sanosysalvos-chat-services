package org.sanosysalvos.dto;

import java.time.LocalDateTime;

public record MensajeDTO(
        Long idMensaje,
        Long idConversacion,
        String idRemitente,
        String contenido,
        Boolean leido,
        Boolean esPropio,
        LocalDateTime createdAt
) {}