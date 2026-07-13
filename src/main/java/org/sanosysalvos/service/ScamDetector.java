package org.sanosysalvos.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ScamDetector {

    // Frases/patrones típicos de estafa en el contexto de mascotas perdidas.
    // Se comparan en minúsculas y sin tildes.
    private static final List<Pattern> PATRONES = List.of(
            Pattern.compile("transfer\\w* (de )?dinero"),
            Pattern.compile("(pasa|manda|envia)me? (la )?plata"),
            Pattern.compile("deposit\\w* (un )?adelanto"),
            Pattern.compile("cobro por (adelantado|anticipado)"),
            Pattern.compile("necesito (el )?pago antes"),
            Pattern.compile("mandame? tu clave"),
            Pattern.compile("mandame? tu contrasena"),
            Pattern.compile("codigo de verificacion"),
            Pattern.compile("numero de tarjeta"),
            Pattern.compile("datos de tu cuenta"),
            Pattern.compile("recompensa por adelantado"),
            Pattern.compile("primero (el )?pago,? despues (te )?entrego"),
            Pattern.compile("solo acepto (transferencia|efectivo por adelantado)"),
            Pattern.compile("link de pago"),
            Pattern.compile("western union"),
            Pattern.compile("mercado ?pago (urgente|ya)")
            Pattern.compile("brayan soto")
    );

    public boolean esEstafa(String contenido) {
        if (contenido == null || contenido.isBlank()) return false;
        String normalizado = normalizar(contenido);
        return PATRONES.stream().anyMatch(p -> p.matcher(normalizado).find());
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes;
    }
}