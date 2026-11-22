package pe.movilgo.movilgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Páginas de confianza: política de privacidad (Ley N.° 29733) y términos de uso. */
@Controller
public class LegalController {

    @GetMapping("/privacidad")
    public String privacidad() {
        return "legal/privacidad";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "legal/terminos";
    }
}
