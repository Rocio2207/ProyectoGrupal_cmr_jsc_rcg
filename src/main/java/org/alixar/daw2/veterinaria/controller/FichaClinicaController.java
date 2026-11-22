package org.alixar.daw2.veterinaria.controller;

import jakarta.validation.Valid;
import org.alixar.daw2.veterinaria.entities.FichaClinica;
import org.alixar.daw2.veterinaria.entities.Mascota;
import org.alixar.daw2.veterinaria.repository.FichaClinicaRepository;
import org.alixar.daw2.veterinaria.repository.MascotaRepository;
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
 * Controlador de las fichas clínicas. Gestiona el listado, el alta y la edición (con el mismo formulario)
 * y la eliminación de fichas clínicas bajo la ruta `/clinical-records`. La mascota se elige con un desplegable.
 */
@Controller
@RequestMapping("/clinical-records")
public class FichaClinicaController {

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el listado completo de fichas clínicas (con el nombre de la mascota).
     */
    @GetMapping
    public String listFichasClinicas(Model model, Locale locale) {
        List<FichaClinica> listFichasClinicas = null;
        try {
            listFichasClinicas = fichaClinicaRepository.findAll();
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("msg.clinical-record-controller.list.error", null, locale));
        }
        model.addAttribute("listFichasClinicas", listFichasClinicas); // Pasar la lista de fichas al modelo
        return "clinical-records/clinical-records"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario vacío para crear una ficha clínica.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        FichaClinica fichaClinica = new FichaClinica();
        fichaClinica.setMascota(new Mascota()); // Necesario para enlazar el <select> con mascota.id
        model.addAttribute("fichaClinica", fichaClinica);
        model.addAttribute("listMascotas", mascotaRepository.findAll()); // Mascotas para rellenar el <select>
        return "clinical-records/clinical-records-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el mismo formulario con los datos de una ficha clínica existente para editarla.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<FichaClinica> fichaClinica = fichaClinicaRepository.findById(id);
            if (fichaClinica.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.clinical-record-controller.edit.notFound", null, locale));
                return "redirect:/clinical-records";
            }
            model.addAttribute("fichaClinica", fichaClinica.get());
            model.addAttribute("listMascotas", mascotaRepository.findAll());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.clinical-record-controller.edit.error", null, locale));
            return "redirect:/clinical-records";
        }
        return "clinical-records/clinical-records-form";
    }

    /**
     * Inserta una ficha clínica nueva. Si hay errores (validación o mascota que ya tiene ficha)
     * devuelve el formulario con los datos escritos y los avisos correspondientes.
     */
    @PostMapping("/insert")
    public String insertFichaClinica(@Valid @ModelAttribute("fichaClinica") FichaClinica fichaClinica, BindingResult result,
                                     RedirectAttributes redirectAttributes, Model model, Locale locale) {
        try {
            if (fichaClinica.getMascota() == null || fichaClinica.getMascota().getId() == null) {
                result.rejectValue("mascota", "msg.clinical-record.mascota.notNull");
            } else {
                Optional<Mascota> mascota = mascotaRepository.findById(fichaClinica.getMascota().getId());
                if (mascota.isEmpty()) {
                    result.rejectValue("mascota", "msg.clinical-record.mascota.notNull");
                } else {
                    fichaClinica.setMascota(mascota.get());
                    if (fichaClinicaRepository.existsFichaClinicaByMascotaId(mascota.get().getId())) {
                        result.rejectValue("mascota", "msg.clinical-record.mascota.exists");
                    }
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("listMascotas", mascotaRepository.findAll()); // El <select> necesita las mascotas
                return "clinical-records/clinical-records-form"; // Devuelve el formulario sin perder los datos
            }
            fichaClinicaRepository.save(fichaClinica);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.clinical-record-controller.insert.error", null, locale));
        }
        return "redirect:/clinical-records"; // Redirigir a la lista de fichas clínicas
    }

    /**
     * Actualiza una ficha clínica existente. Si hay errores devuelve el formulario con los datos escritos.
     */
    @PostMapping("/update")
    public String updateFichaClinica(@Valid @ModelAttribute("fichaClinica") FichaClinica fichaClinica, BindingResult result,
                                     RedirectAttributes redirectAttributes, Model model, Locale locale) {
        try {
            if (fichaClinica.getMascota() == null || fichaClinica.getMascota().getId() == null) {
                result.rejectValue("mascota", "msg.clinical-record.mascota.notNull");
            } else {
                Optional<Mascota> mascota = mascotaRepository.findById(fichaClinica.getMascota().getId());
                if (mascota.isEmpty()) {
                    result.rejectValue("mascota", "msg.clinical-record.mascota.notNull");
                } else {
                    fichaClinica.setMascota(mascota.get());
                    if (fichaClinicaRepository.existsFichaClinicaByMascotaIdAndNotId(mascota.get().getId(), fichaClinica.getId())) {
                        result.rejectValue("mascota", "msg.clinical-record.mascota.exists");
                    }
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("listMascotas", mascotaRepository.findAll());
                return "clinical-records/clinical-records-form";
            }
            fichaClinicaRepository.save(fichaClinica);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.clinical-record-controller.update.error", null, locale));
        }
        return "redirect:/clinical-records";
    }

    /**
     * Elimina una ficha clínica y vuelve al listado.
     */
    @PostMapping("/delete")
    public String deleteFichaClinica(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            fichaClinicaRepository.deleteById(id);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.clinical-record-controller.delete.error", null, locale));
        }
        return "redirect:/clinical-records";
    }
}

