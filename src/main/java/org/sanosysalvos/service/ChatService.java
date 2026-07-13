package org.sanosysalvos.service;

import lombok.RequiredArgsConstructor;
import org.sanosysalvos.dto.*;
import org.sanosysalvos.model.ChatConversacion;
import org.sanosysalvos.model.ChatMensaje;
import org.sanosysalvos.repository.ChatConversacionRepository;
import org.sanosysalvos.repository.ChatMensajeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatConversacionRepository conversacionRepo;
    private final ChatMensajeRepository mensajeRepo;
    private final ScamDetector scamDetector;

    private static final List<String> FRASES_ESTAFA = List.of(
            "paga primero", "depositame", "transfiere primero", "adelanto",
            "costo de envío", "tarifa", "envíame dinero", "pago por adelantado",
            "dame tu clave", "código de verificación", "ingresa a este link",
            "actualiza tus datos aquí", "premio", "ganaste", "exclusivo para ti" , "bryan soto"
    );

    private boolean detectarEstafa(String contenido) {
        if (contenido == null || contenido.isEmpty()) {
            return false;
        }
        String contenidoLowerCase = contenido.toLowerCase();
        for (String frase : FRASES_ESTAFA) {
            if (contenidoLowerCase.contains(frase)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public ConversacionDTO crearOObtenerConversacion(String emailActual, String emailOtro) {
        ChatConversacion conv = conversacionRepo.findByUsuarios(emailActual, emailOtro)
                .orElseGet(() -> {
                    ChatConversacion nueva = ChatConversacion.builder()
                            .idUsuario1(emailActual)
                            .idUsuario2(emailOtro)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return conversacionRepo.save(nueva);
                });
        return toConversacionDTO(conv, emailActual);
    }

    public List<ConversacionDTO> listarConversaciones(String email) {
        return conversacionRepo.findByUsuario(email).stream()
                .map(c -> toConversacionDTO(c, email))
                .toList();
    }

    public List<MensajeDTO> listarMensajes(Long idConversacion, String emailActual) {
        return mensajeRepo.findByIdConversacionOrderByCreatedAtAsc(idConversacion).stream()
                .map(m -> toMensajeDTO(m, emailActual))
                .toList();
    }

    @Transactional
    public MensajeDTO enviarMensaje(Long idConversacion, String emailRemitente, String contenido) {
        conversacionRepo.findById(idConversacion)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        boolean esEstafa = scamDetector.esEstafa(contenido);

        ChatMensaje mensaje = ChatMensaje.builder()
                .idConversacion(idConversacion)
                .idRemitente(emailRemitente)
                .contenido(contenido)
                .leido(false)
                .potencialEstafa(esEstafa)
                .createdAt(LocalDateTime.now())
                .build();

        return toMensajeDTO(mensajeRepo.save(mensaje), emailRemitente);
    }

    @Transactional
    public void marcarLeidos(Long idConversacion, String emailActual) {
        mensajeRepo.findByIdConversacionOrderByCreatedAtAsc(idConversacion).stream()
                .filter(m -> !m.getIdRemitente().equals(emailActual) && !m.getLeido())
                .forEach(m -> {
                    m.setLeido(true);
                    mensajeRepo.save(m);
                });
    }

    private ConversacionDTO toConversacionDTO(ChatConversacion c, String emailActual) {
        String otroUsuario = c.getIdUsuario1().equals(emailActual) ? c.getIdUsuario2() : c.getIdUsuario1();
        String ultimoMensaje = mensajeRepo
                .findTopByIdConversacionOrderByCreatedAtDesc(c.getIdConversacion())
                .map(ChatMensaje::getContenido)
                .orElse(null);
        return new ConversacionDTO(c.getIdConversacion(), c.getIdUsuario1(), c.getIdUsuario2(), otroUsuario, ultimoMensaje, c.getCreatedAt());
    }

    private MensajeDTO toMensajeDTO(ChatMensaje m, String emailActual) {
        return new MensajeDTO(m.getIdMensaje(), m.getIdConversacion(), m.getIdRemitente(), m.getContenido(), m.getLeido(), m.getIdRemitente().equals(emailActual), m.getPotencialEstafa(), m.getCreatedAt());
    }
}