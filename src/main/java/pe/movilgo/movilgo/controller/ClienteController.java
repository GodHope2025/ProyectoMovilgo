package pe.movilgo.movilgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.movilgo.movilgo.dto.ClienteForm;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.service.ClienteService;

/** Registro de nuevos clientes (personas y empresas). */
@Controller
public class ClienteController {

    private static final String VISTA = "cliente/registro";

    private final ClienteService clientes;

    public ClienteController(ClienteService clientes) {
        this.clientes = clientes;
    }

    @ModelAttribute("tipos")
    public TipoDocumento[] tipos() {
        return TipoDocumento.values();
    }

    @GetMapping("/registro")
    public String formulario(Model model) {
        model.addAttribute("clienteForm", new ClienteForm());
        return VISTA;
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("clienteForm") ClienteForm form, BindingResult errores,
            Model model, RedirectAttributes redireccion) {
        if (errores.hasErrors()) {
            return VISTA;
        }
        try {
            clientes.registrar(form);
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            return VISTA;
        }
        redireccion.addFlashAttribute("mensaje", "Cuenta creada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
