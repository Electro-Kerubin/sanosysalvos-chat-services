package org.sanosysalvos.repository;

import org.sanosysalvos.model.ChatMensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {
    List<ChatMensaje> findByIdConversacionOrderByCreatedAtAsc(Long idConversacion);
    Optional<ChatMensaje> findTopByIdConversacionOrderByCreatedAtDesc(Long idConversacion);
}