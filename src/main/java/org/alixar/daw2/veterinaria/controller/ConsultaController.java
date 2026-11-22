package org.alixar.daw2.veterinaria.controller;

import jakarta.validation.Valid;
import org.alixar.daw2.veterinaria.entities.Consulta;
import org.alixar.daw2.veterinaria.entities.Mascota;
import org.alixar.daw2.veterinaria.entities.Veterinario;
import org.alixar.daw2.veterinaria.repository.ConsultaRepository;
import org.alixar.daw2.veterinaria.repository.MascotaRepository;
import org.alixar.daw2.veterinaria.repository.TratamientoRepository;
import org.alixar.daw2.veterinaria.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador de las consultas. Gestiona el listado, el alta y la edición (con el mismo formulario)
 * y la eliminación de consultas bajo la ruta `/consultations`. La mascota y el veterinario se eligen con
 * desplegables y los tratamientos con casillas de verificación (`tratamientosIds`).
 */
@Controller
@RequestMapping("/consultations")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el listado completo de consultas (con el nombre de la mascota, del veterinario y los tratamientos).
     */
    @GetMapping
    public String listConsultas(Model model, Locale locale) {
        List<Consulta> listConsultas = null;
        try {
            listConsultas = consultaRepository.findAll();
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("msg.consultation-controller.list.error", null, locale));
        }
        model.addAttribute("listConsultas", listConsultas); // Pasar la lista de consultas al modelo
        return "consultations/consultations"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario vacío para crear una consulta.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Consulta consulta = new Consulta();
        consulta.setMascota(new Mascota()); // Necesario para enlazar el <select> con mascota.id
        consulta.setVeterinario(new Veterinario()); // Necesario para enlazar el <select> con veterinario.id
        model.addAttribute("consulta", consulta);
        model.addAttribute("listMascotas", mascotaRepository.findAll());
        model.addAttribute("listVeterinarios", veterinarioRepository.findAll());
        model.addAttribute("listTratamientos", tratamientoRepository.findAll()); // Para las casillas de verificación
        return "consultations/consultations-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el mismo formulario con los datos de una consulta existente para editarla.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<Consulta> consulta = consultaRepository.findById(id);
            if (consulta.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.consultation-controller.edit.notFound", null, locale));
                return "redirect:/consultations";
            }
            model.addAttribute("consulta", consulta.get());
            model.addAttribute("listMascotas", mascotaRepository.findAll());
            model.addAttribute("listVeterinarios", veterinarioRepository.findAll());
            model.addAttribute("listTratamientos", tratamientoRepository.findAll());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.consultation-controller.edit.error", null, locale));
            return "redirect:/consultations";
        }
        return "consultations/consultations-form";
    }

    /**
     * Inserta una consulta nueva. Si hay errores devuelve el formulario con los datos escritos
     * (incluidos los tratamientos marcados) y los avisos correspondientes.
     */
    @PostMapping("/insert")
    public String insertConsulta(@Valid @ModelAttribute("consulta") Consulta consulta, BindingResult result,
                                 @RequestParam(value = "tratamientosIds", required = false) List<Long> tratamientosIds,
                                 RedirectAttributes redirectAttributes, Model model, Locale locale) {
        try {
            // Tratamientos marcados en el formulario (si no se marca ninguno, la lista queda vacía)
            consulta.setTratamientos(tratamientosIds == null ? new ArrayList<>() : tratamientoRepository.findAllById(tratamientosIds));
            if (consulta.getMascota() == null || consulta.getMascota().getId() == null) {
                result.rejectValue("mascota", "msg.consultation.mascota.notNull");
            } else {
                Optional<Mascota> mascota = mascotaRepository.findById(consulta.getMascota().getId());
                if (mascota.isEmpty()) {
                    result.rejectValue("mascota", "msg.consultation.mascota.notNull");
                } else {
                    consulta.setMascota(mascota.get());
                }
            }
            if (consulta.getVeterinario() == null || consulta.getVeterinario().getId() == null) {
                result.rejectValue("veterinario", "msg.consultation.veterinario.notNull");
            } else {
                Optional<Veterinario> veterinario = veterinarioRepository.findById(consulta.getVeterinario().getId());
                if (veterinario.isEmpty()) {
                    result.rejectValue("veterinario", "msg.consultation.veterinario.notNull");
                } else {
                    consulta.setVeterinario(veterinario.get());
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("listMascotas", mascotaRepository.findAll());
                model.addAttribute("listVeterinarios", veterinarioRepository.findAll());
                model.addAttribute("listTratamientos", tratamientoRepository.findAll());
                return "consultations/consultations-form"; // Devuelve el formulario sin perder los datos
            }
            consultaRepository.save(consulta);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.consultation-controller.insert.error", null, locale));
        }
        return "redirect:/consultations"; // Redirigir a la lista de consultas
    }

    /**
     * Actualiza una consulta existente. Si hay errores devuelve el formulario con los datos escritos.
     */
    @PostMapping("/update")
    public String updateConsulta(@Valid @ModelAttribute("consulta") Consulta consulta, BindingResult result,
                                 @RequestParam(value = "tratamientosIds", required = false) List<Long> tratamientosIds,
                                 RedirectAttributes redirectAttributes, Model model, Locale locale) {
        try {
            consulta.setTratamientos(tratamientosIds == null ? new ArrayList<>() : tratamientoRepository.findAllById(tratamientosIds));
            if (consulta.getMascota() == null || consulta.getMascota().getId() == null) {
                result.rejectValue("mascota", "msg.consultation.mascota.notNull");
            } else {
                Optional<Mascota> mascota = mascotaRepository.findById(consulta.getMascota().getId());
                if (mascota.isEmpty()) {
                    result.rejectValue("mascota", "msg.consultation.mascota.notNull");
                } else {
                    consulta.setMascota(mascota.get());
                }
            }
            if (consulta.getVeterinario() == null || consulta.getVeterinario().getId() == null) {
                result.rejectValue("veterinario", "msg.consultation.veterinario.notNull");
            } else {
                Optional<Veterinario> veterinario = veterinarioRepository.findById(consulta.getVeterinario().getId());
                if (veterinario.isEmpty()) {
                    result.rejectValue("veterinario", "msg.consultation.veterinario.notNull");
                } else {
                    consulta.setVeterinario(veterinario.get());
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("listMascotas", mascotaRepository.findAll());
                model.addAttribute("listVeterinarios", veterinarioRepository.findAll());
                model.addAttribute("listTratamientos", tratamientoRepository.findAll());
                return "consultations/consultations-form";
            }
            consultaRepository.save(consulta);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.consultation-controller.update.error", null, locale));
        }
        return "redirect:/consultations";
    }

    /**
     * Elimina una consulta (y sus filas de la tabla `consulta_tratamiento`) y vuelve al listado.
     */
    @PostMapping("/delete")
    public String deleteConsulta(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            consultaRepository.deleteById(id);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.consultation-controller.delete.error", null, locale));
        }
        return "redirect:/consultations";
    }
}
