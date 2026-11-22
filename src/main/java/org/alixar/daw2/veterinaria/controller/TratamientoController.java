package org.alixar.daw2.veterinaria.controller;

import jakarta.validation.Valid;
import org.alixar.daw2.veterinaria.entities.Tratamiento;
import org.alixar.daw2.veterinaria.repository.TratamientoRepository;
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
 * Controlador de los tratamientos. Gestiona el listado, el alta y la edición (con el mismo formulario)
 * y la eliminación de tratamientos bajo la ruta `/treatments`.
 */
@Controller
@RequestMapping("/treatments")
public class TratamientoController {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el listado completo de tratamientos.
     */
    @GetMapping
    public String listTratamientos(Model model, Locale locale) {
        List<Tratamiento> listTratamientos = null;
        try {
            listTratamientos = tratamientoRepository.findAll();
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("msg.treatment-controller.list.error", null, locale));
        }
        model.addAttribute("listTratamientos", listTratamientos); // Pasar la lista de tratamientos al modelo
        return "treatments/treatmens"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario vacío para crear un tratamiento.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("tratamiento", new Tratamiento()); // Crear un nuevo objeto Tratamiento
        return "treatments/treatmensts-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el mismo formulario con los datos de un tratamiento existente para editarlo.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<Tratamiento> tratamiento = tratamientoRepository.findById(id);
            if (tratamiento.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.treatment-controller.edit.notFound", null, locale));
                return "redirect:/treatments";
            }
            model.addAttribute("tratamiento", tratamiento.get());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.treatment-controller.edit.error", null, locale));
            return "redirect:/treatments";
        }
        return "treatments/treatmensts-form";
    }

    /**
     * Inserta un tratamiento nuevo. Si hay errores (validación o nombre duplicado) devuelve el formulario
     * con los datos escritos y los avisos correspondientes.
     */
    @PostMapping("/insert")
    public String insertTratamiento(@Valid @ModelAttribute("tratamiento") Tratamiento tratamiento, BindingResult result,
                                    RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (tratamiento.getNombre() != null) {
                tratamiento.setNombre(tratamiento.getNombre().trim());
                if (!result.hasFieldErrors("nombre") && tratamientoRepository.existsTratamientoByNombre(tratamiento.getNombre())) {
                    result.rejectValue("nombre", "msg.treatment.nombre.exists");
                }
            }
            if (result.hasErrors()) {
                return "treatments/treatmensts-form"; // Devuelve el formulario para mostrar los errores sin perder los datos
            }
            tratamientoRepository.save(tratamiento);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.treatment-controller.insert.error", null, locale));
        }
        return "redirect:/treatments"; // Redirigir a la lista de tratamientos
    }

    /**
     * Actualiza un tratamiento existente. Si hay errores devuelve el formulario con los datos escritos.
     */
    @PostMapping("/update")
    public String updateTratamiento(@Valid @ModelAttribute("tratamiento") Tratamiento tratamiento, BindingResult result,
                                    RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (tratamiento.getNombre() != null) {
                tratamiento.setNombre(tratamiento.getNombre().trim());
                if (!result.hasFieldErrors("nombre") && tratamientoRepository.existsTratamientoByNombreAndNotId(tratamiento.getNombre(), tratamiento.getId())) {
                    result.rejectValue("nombre", "msg.treatment.nombre.exists");
                }
            }
            if (result.hasErrors()) {
                return "treatments/treatmensts-form";
            }
            tratamientoRepository.save(tratamiento);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.treatment-controller.update.error", null, locale));
        }
        return "redirect:/treatments";
    }

    /**
     * Elimina un tratamiento y vuelve al listado. Si está asignado a alguna consulta la base de datos
     * lo impide y se muestra un aviso.
     */
    @PostMapping("/delete")
    public String deleteTratamiento(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            tratamientoRepository.deleteById(id);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.treatment-controller.delete.error", null, locale));
        }
        return "redirect:/treatments";
    }
}
