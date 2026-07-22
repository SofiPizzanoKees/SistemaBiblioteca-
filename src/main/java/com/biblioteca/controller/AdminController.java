package com.biblioteca.controller;

import com.biblioteca.service.PrestamoService;
import com.biblioteca.service.SancionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PrestamoService prestamoService;
    private final SancionService sancionService;

    public AdminController(PrestamoService prestamoService, SancionService sancionService) {
        this.prestamoService = prestamoService;
        this.sancionService = sancionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("cantidadActivos", prestamoService.listarActivos().size());
        model.addAttribute("cantidadVencidos", prestamoService.listarVencidos().size());
        return "admin/dashboard";
    }

    // RF06
    @GetMapping("/prestamos")
    public String prestamosActivos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarActivos());
        return "admin/prestamos";
    }

    // RF08
    @GetMapping("/vencidos")
    public String vencidos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarVencidos());
        model.addAttribute("sanciones", sancionService.listarActivas());
        return "admin/vencidos";
    }

    // RF05: registrar devolucion (checkbox "ejemplarDaniado" en el formulario)
    @PostMapping("/prestamos/{id}/devolucion")
    public String registrarDevolucion(@PathVariable Long id,
                                       @org.springframework.web.bind.annotation.RequestParam(defaultValue = "false") boolean ejemplarDaniado) {
        prestamoService.registrarDevolucion(id, ejemplarDaniado);
        return "redirect:/admin/prestamos";
    }

    // RF10: estadisticas
    @GetMapping("/estadisticas")
    public String estadisticas(Model model) {
        model.addAttribute("librosMasPrestados", prestamoService.librosMasPrestados());
        model.addAttribute("usuariosConMasPrestamos", prestamoService.usuariosConMasPrestamos());
        return "admin/estadisticas";
    }

    // RF09: resolver una sancion (el bibliotecario confirma que el usuario repuso/reparo el libro)
    @PostMapping("/sanciones/{id}/resolver")
    public String resolverSancion(@PathVariable Long id) {
        sancionService.resolver(id);
        return "redirect:/admin/vencidos";
    }
}
