package org.alixar.daw2.veterinaria.controller;

import jakarta.validation.Valid;
import org.alixar.daw2.veterinaria.entities.Consultorio;
import org.alixar.daw2.veterinaria.repository.ConsultorioRepository;
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
 * Controlador de los consultorios. Gestiona el listado, el alta y la edición (con el mismo formulario)
 * y la eliminación de consultorios bajo la ruta `/offices`.
 */
@Controller
@RequestMapping("/offices")
public class ConsultorioController {

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el listado completo de consultorios.
     */
    @GetMapping
    public String listConsultorios(Model model, Locale locale) {
        List<Consultorio> listConsultorios = null;
        try {
            listConsultorios = consultorioRepository.findAll();
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("msg.office-controller.list.error", null, locale));
        }
        model.addAttribute("listConsultorios", listConsultorios); // Pasar la lista de consultorios al modelo
        return "offices/offices"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario vacío para crear un consultorio.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("consultorio", new Consultorio()); // Crear un nuevo objeto Consultorio
        return "offices/offices-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el mismo formulario con los datos de un consultorio existente para editarlo.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<Consultorio> consultorio = consultorioRepository.findById(id);
            if (consultorio.isEmpty()) {
                // Si no existe, se vuelve al listado con un aviso (la vista no puede trabajar con un consultorio null)
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.office-controller.edit.notFound", null, locale));
                return "redirect:/offices";
            }
            model.addAttribute("consultorio", consultorio.get());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.office-controller.edit.error", null, locale));
            return "redirect:/offices";
        }
        return "offices/offices-form";
    }

    /**
     * Inserta un consultorio nuevo. Si hay errores (validación o número duplicado) devuelve el formulario
     * con los datos escritos y los avisos correspondientes.
     */
    @PostMapping("/insert")
    public String insertConsultorio(@Valid @ModelAttribute("consultorio") Consultorio consultorio, BindingResult result,
                                    RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (!result.hasFieldErrors("numero") && consultorioRepository.existsConsultorioByNumero(consultorio.getNumero())) {
                result.rejectValue("numero", "msg.office.numero.exists");
            }
            if (result.hasErrors()) {
                return "offices/offices-form"; // Devuelve el formulario para mostrar los errores sin perder los datos
            }
            consultorioRepository.save(consultorio);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.office-controller.insert.error", null, locale));
        }
        return "redirect:/offices"; // Redirigir a la lista de consultorios
    }

    /**
     * Actualiza un consultorio existente. Si hay errores devuelve el formulario con los datos escritos.
     */
    @PostMapping("/update")
    public String updateConsultorio(@Valid @ModelAttribute("consultorio") Consultorio consultorio, BindingResult result,
                                    RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (!result.hasFieldErrors("numero") && consultorioRepository.existsConsultorioByNumeroAndNotId(consultorio.getNumero(), consultorio.getId())) {
                result.rejectValue("numero", "msg.office.numero.exists");
            }
            if (result.hasErrors()) {
                return "offices/offices-form";
            }
            consultorioRepository.save(consultorio);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.office-controller.update.error", null, locale));
        }
        return "redirect:/offices";
    }

    /**
     * Elimina un consultorio y vuelve al listado. Si tiene un veterinario asignado la base de datos
     * lo impide y se muestra un aviso.
     */
    @PostMapping("/delete")
    public String deleteConsultorio(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            consultorioRepository.deleteById(id);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.office-controller.delete.error", null, locale));
        }
        return "redirect:/offices";
    }
}
