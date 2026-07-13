package org.sanosysalvos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanosysalvos.dto.ConversacionDTO;
import org.sanosysalvos.dto.MensajeDTO;
import org.sanosysalvos.model.ChatConversacion;
import org.sanosysalvos.model.ChatMensaje;
import org.sanosysalvos.repository.ChatConversacionRepository;
import org.sanosysalvos.repository.ChatMensajeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatConversacionRepository conversacionRepo;
    @Mock private ChatMensajeRepository mensajeRepo;
    @Mock private ScamDetector scamDetector;

    @InjectMocks private ChatService chatService;

    private ChatConversacion conversacion;
    private final String emailUsuario1 = "user1@test.com";
    private final String emailUsuario2 = "user2@test.com";

    @BeforeEach
    void setUp() {
        conversacion = new ChatConversacion();
        conversacion.setIdConversacion(1L);
        conversacion.setIdUsuario1(emailUsuario1);
        conversacion.setIdUsuario2(emailUsuario2);
        conversacion.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void crearOObtenerConversacion_debeRetornarConversacionExistente() {
        when(conversacionRepo.findByUsuarios(emailUsuario1, emailUsuario2))
            .thenReturn(Optional.of(conversacion));
        when(mensajeRepo.findTopByIdConversacionOrderByCreatedAtDesc(1L))
            .thenReturn(Optional.empty());

        ConversacionDTO result = chatService.crearOObtenerConversacion(emailUsuario1, emailUsuario2);

        assertNotNull(result);
        assertEquals(1L, result.idConversacion());
        assertEquals(emailUsuario2, result.otroUsuario());
        verify(conversacionRepo, never()).save(any());
    }

    @Test
    void crearOObtenerConversacion_debeCrearNuevaConversacion_cuandoNoExiste() {
        when(conversacionRepo.findByUsuarios(emailUsuario1, emailUsuario2))
            .thenReturn(Optional.empty());
        when(conversacionRepo.save(any())).thenReturn(conversacion);
        when(mensajeRepo.findTopByIdConversacionOrderByCreatedAtDesc(1L))
            .thenReturn(Optional.empty());

        ConversacionDTO result = chatService.crearOObtenerConversacion(emailUsuario1, emailUsuario2);

        assertNotNull(result);
        verify(conversacionRepo, times(1)).save(any());
    }

    @Test
    void listarConversaciones_debeRetornarListaDeConversaciones() {
        when(conversacionRepo.findByUsuario(emailUsuario1)).thenReturn(List.of(conversacion));
        when(mensajeRepo.findTopByIdConversacionOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());

        List<ConversacionDTO> result = chatService.listarConversaciones(emailUsuario1);

        assertEquals(1, result.size());
        assertEquals(emailUsuario2, result.get(0).otroUsuario());
    }

    @Test
    void enviarMensaje_debeGuardarMensajeCorrectamente() {
        String contenido = "Hola, vi tu mascota!";

        when(conversacionRepo.findById(1L)).thenReturn(Optional.of(conversacion));

        ChatMensaje mensajeGuardado = new ChatMensaje();
        mensajeGuardado.setIdMensaje(1L);
        mensajeGuardado.setIdConversacion(1L);
        mensajeGuardado.setIdRemitente(emailUsuario1);
        mensajeGuardado.setContenido(contenido);
        mensajeGuardado.setLeido(false);
        mensajeGuardado.setCreatedAt(LocalDateTime.now());

        when(mensajeRepo.save(any())).thenReturn(mensajeGuardado);

        MensajeDTO result = chatService.enviarMensaje(1L, emailUsuario1, contenido);

        assertNotNull(result);
        assertEquals(contenido, result.contenido());
        assertTrue(result.esPropio());
        assertFalse(result.leido());
    }

    @Test
    void enviarMensaje_debeMarcarComoEstafa_cuandoContienePatronSospechoso() {
        String contenido = "necesito que me mandes tu clave para confirmar";

        when(conversacionRepo.findById(1L)).thenReturn(Optional.of(conversacion));
        when(scamDetector.esEstafa(contenido)).thenReturn(true);

        ChatMensaje mensajeGuardado = new ChatMensaje();
        mensajeGuardado.setIdMensaje(2L);
        mensajeGuardado.setIdConversacion(1L);
        mensajeGuardado.setIdRemitente(emailUsuario1);
        mensajeGuardado.setContenido(contenido);
        mensajeGuardado.setLeido(false);
        mensajeGuardado.setPotencialEstafa(true);
        mensajeGuardado.setCreatedAt(LocalDateTime.now());

        when(mensajeRepo.save(any())).thenReturn(mensajeGuardado);

        MensajeDTO result = chatService.enviarMensaje(1L, emailUsuario1, contenido);

        assertNotNull(result);
        assertTrue(result.potencialEstafa());
    }

    @Test
    void enviarMensaje_debeLanzarException_cuandoConversacionNoExiste() {
        when(conversacionRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> chatService.enviarMensaje(99L, emailUsuario1, "Hola"));
    }

    @Test
    void listarMensajes_debeMarcarMensajesComoLeidos() {
        ChatMensaje mensajeNoLeido = new ChatMensaje();
        mensajeNoLeido.setIdMensaje(1L);
        mensajeNoLeido.setIdConversacion(1L);
        mensajeNoLeido.setIdRemitente(emailUsuario2);
        mensajeNoLeido.setContenido("Hola");
        mensajeNoLeido.setLeido(false);
        mensajeNoLeido.setCreatedAt(LocalDateTime.now());

        when(mensajeRepo.findByIdConversacionOrderByCreatedAtAsc(1L))
            .thenReturn(List.of(mensajeNoLeido));

        chatService.marcarLeidos(1L, emailUsuario1);

        verify(mensajeRepo, times(1)).save(argThat(m -> m.getLeido()));
    }
}