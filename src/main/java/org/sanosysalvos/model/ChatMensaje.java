package org.sanosysalvos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_mensaje")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Builder.Default
private Boolean esEstafa = false;

public class ChatMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @Column(name = "id_conversacion", nullable = false)
    private Long idConversacion;

    @Column(name = "id_remitente", nullable = false)
    private String idRemitente;

    @Column(name = "contenido", nullable = false)
    private String contenido;

    @Column(name = "leido")
    private Boolean leido;

    @Column(name = "potencial_estafa")
    private Boolean potencialEstafa;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}