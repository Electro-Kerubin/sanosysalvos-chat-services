package org.sanosysalvos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_conversacion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Builder.Default
private Boolean esEstafa = false;

public class ChatConversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversacion")
    private Long idConversacion;

    @Column(name = "id_usuario_1", nullable = false)
    private String idUsuario1;

    @Column(name = "id_usuario_2", nullable = false)
    private String idUsuario2;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}