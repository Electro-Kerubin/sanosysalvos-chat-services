package org.sanosysalvos.repository;

import org.sanosysalvos.model.ChatConversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ChatConversacionRepository extends JpaRepository<ChatConversacion, Long> {

    @Query("SELECT c FROM ChatConversacion c WHERE c.idUsuario1 = :email OR c.idUsuario2 = :email")
    List<ChatConversacion> findByUsuario(String email);

    @Query("SELECT c FROM ChatConversacion c WHERE (c.idUsuario1 = :u1 AND c.idUsuario2 = :u2) OR (c.idUsuario1 = :u2 AND c.idUsuario2 = :u1)")
    Optional<ChatConversacion> findByUsuarios(String u1, String u2);
}