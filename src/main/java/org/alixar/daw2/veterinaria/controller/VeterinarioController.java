package org.alixar.daw2.veterinaria.controller;

import jakarta.validation.Valid;
import org.alixar.daw2.veterinaria.entities.Consultorio;
import org.alixar.daw2.veterinaria.entities.Veterinario;
import org.alixar.daw2.veterinaria.repository.ConsultorioRepository;
import org.alixar.daw2.veterinaria.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador de los veterinarios. Gestiona el listado, el alta y la edición (con el mismo formulario)
 * y la eliminación de veterinarios bajo la ruta `/vets`. El consultorio se elige con un desplegable.
 */
@Controller
@RequestMapping("/vets")
public class VeterinarioController {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el listado completo de veterinarios (con el número de su consultorio).
     */
    @GetMapping
    public String listVeterinarios(Model model, Locale locale) {
        List<Veterinario> listVeterinarios = null;
        try {
            listVeterinarios = veterinarioRepository.findAll();
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("msg.vet-controller.list.error", null, locale));
        }
        model.addAttribute("listVeterinarios", listVeterinarios); // Pasar la lista de veterinarios al modelo
        return "vets/vets"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario vacío para crear un veterinario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Veterinario veterinario = new Veterinario();
        veterinario.setConsultorio(new Consultorio()); // Necesario para enlazar el <select> con consultorio.id
        model.addAttribute("veterinario", veterinario);
        model.addAttribute("listConsultorios", consultorioRepository.findAll()); // Consultorios para rellenar el <select>
        return "vets/vets-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el mismo formulario con los datos de un veterinario existente para editarlo.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<Veterinario> veterinario = veterinarioRepository.findById(id);
            if (veterinario.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.vet-controller.edit.notFound", null, locale));
                return "redirect:/vets";
            }
            model.addAttribute("veterinario", veterinario.get());
            model.addAttribute("listConsultorios", consultorioRepository.findAll());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.vet-controller.edit.error", null, locale));
            return "redirect:/vets";
        }
        return "vets/vets-form";
    }

    /**
     * Inserta un veterinario nuevo. Si hay errores (validación, nombre duplicado o consultorio ya asignado)
     * devuelve el formulario con los datos escritos y los avisos correspondientes.
     */
    @PostMapping("/insert")
    public String insertVeterinario(@Valid @ModelAttribute("veterinario") Veterinario veterinario, BindingResult result,
                                    RedirectAttributes redirectAttributes, Model model, Locale locale) {
        try {
            if (veterinario.getNombre() != null) {
                veterinario.setNombre(veterinario.getNombre().trim());
                if (!result.hasFieldErrors("nombre") && veterinarioRepository.existsVeterinarioByNombre(veterinario.getNombre())) {
                    result.rejectValue("nombre", "msg.vet.nombre.exists");
                }
            }
            if (veterinario.getConsultorio() == null || veterinario.getConsultorio().getId() == null) {
                result.rejectValue("consultorio", "msg.vet.consultorio.notNull");
            } else {
                Optional<Consultorio> consultorio = consultorioRepository.findById(veterinario.getConsultorio().getId());
                if (consultorio.isEmpty()) {
                    result.rejectValue("consultorio", "msg.vet.consultorio.notNull");
                } else {
                    veterinario.setConsultorio(consultorio.get());
                    if (veterinarioRepository.existsVeterinarioByConsultorioId(consultorio.get().getId())) {
                        result.rejectValue("consultorio", "msg.vet.consultorio.exists");
                    }
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("listConsultorios", consultorioRepository.findAll()); // El <select> necesita los consultorios
                return "vets/vets-form"; // Devuelve el formulario para mostrar los errores sin perder los datos
            }
            veterinarioRepository.save(veterinario);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.vet-controller.insert.error", null, locale));
        }
        return "redirect:/vets"; // Redirigir a la lista de veterinarios
    }

    /**
     * Actualiza un veterinario existente. Si hay errores devuelve el formulario con los datos escritos.
     */
    @PostMapping("/update")
    public String updateVeterinario(@Valid @ModelAttribute("veterinario") Veterinario veterinario, BindingResult result,
                                    RedirectAttributes redirectAttributes, Model model, Locale locale) {
        try {
            if (veterinario.getNombre() != null) {
                veterinario.setNombre(veterinario.getNombre().trim());
                if (!result.hasFieldErrors("nombre") && veterinarioRepository.existsVeterinarioByNombreAndNotId(veterinario.getNombre(), veterinario.getId())) {
                    result.rejectValue("nombre", "msg.vet.nombre.exists");
                }
            }
            if (veterinario.getConsultorio() == null || veterinario.getConsultorio().getId() == null) {
                result.rejectValue("consultorio", "msg.vet.consultorio.notNull");
            } else {
                Optional<Consultorio> consultorio = consultorioRepository.findById(veterinario.getConsultorio().getId());
                if (consultorio.isEmpty()) {
                    result.rejectValue("consultorio", "msg.vet.consultorio.notNull");
                } else {
                    veterinario.setConsultorio(consultorio.get());
                    if (veterinarioRepository.existsVeterinarioByConsultorioIdAndNotId(consultorio.get().getId(), veterinario.getId())) {
                        result.rejectValue("consultorio", "msg.vet.consultorio.exists");
                    }
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("listConsultorios", consultorioRepository.findAll());
                return "vets/vets-form";
            }
            veterinarioRepository.save(veterinario);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.vet-controller.update.error", null, locale));
        }
        return "redirect:/vets";
    }

    /**
     * Elimina un veterinario y vuelve al listado. Si tiene consultas asociadas la base de datos
     * lo impide y se muestra un aviso.
     */
    @PostMapping("/delete")
    public String deleteVeterinario(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            veterinarioRepository.deleteById(id);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.vet-controller.delete.error", null, locale));
        }
        return "redirect:/vets";
    }
}
