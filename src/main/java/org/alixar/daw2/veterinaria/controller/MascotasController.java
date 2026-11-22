package org.alixar.daw2.veterinaria.controller;

import jakarta.validation.Valid;
import org.alixar.daw2.veterinaria.entities.Mascota;
import org.alixar.daw2.veterinaria.repository.MascotaRepository;
import org.alixar.daw2.veterinaria.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador de las mascotas. Gestiona el listado, el alta y la edición (con el mismo formulario),
 * la subida de la foto y la eliminación de mascotas bajo la ruta `/pets`.
 * La foto se guarda en el servidor con `FileStorageService` y en la base de datos solo se almacena su nombre.
 */
@Controller
@RequestMapping("/pets")
public class MascotasController {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Muestra el listado completo de mascotas (con su foto si la tienen).
     */
    @GetMapping
    public String listMascotas(Model model, Locale locale) {
        List<Mascota> listMascotas = null;
        try {
            listMascotas = mascotaRepository.findAll();
        } catch (DataAccessException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.list.error", null, locale));
        }
        model.addAttribute("listMascotas", listMascotas); // Pasar la lista de mascotas al modelo
        return "pets/pets"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    /**
     * Muestra el formulario vacío para crear una mascota.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("mascota", new Mascota()); // Crear un nuevo objeto Mascota
        return "pets/pets-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    /**
     * Muestra el mismo formulario con los datos de una mascota existente para editarla.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<Mascota> mascota = mascotaRepository.findById(id);
            if (mascota.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.edit.notFound", null, locale));
                return "redirect:/pets";
            }
            model.addAttribute("mascota", mascota.get());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.edit.error", null, locale));
            return "redirect:/pets";
        }
        return "pets/pets-form";
    }

    /**
     * Inserta una mascota nueva y guarda su foto (si se ha subido). Si hay errores devuelve el formulario
     * con los datos escritos y los avisos correspondientes.
     */
    @PostMapping("/insert")
    public String insertMascota(@Valid @ModelAttribute("mascota") Mascota mascota, BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (mascota.getNombre() != null) {
                mascota.setNombre(mascota.getNombre().trim());
            }
            if (result.hasErrors()) {
                return "pets/pets-form"; // Devuelve el formulario para mostrar los errores sin perder los datos
            }
            // Guardar la imagen subida
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName == null) {
                    result.rejectValue("foto", "msg.pet.foto.invalid");
                    return "pets/pets-form";
                }
                mascota.setFoto(fileName);
            }
            mascotaRepository.save(mascota);
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.insert.error", null, locale));
        }
        return "redirect:/pets"; // Redirigir a la lista de mascotas
    }

    /**
     * Actualiza una mascota existente. Si se sube una foto nueva sustituye a la anterior (que se borra del servidor);
     * si no se sube ninguna, se conserva la que ya tenía.
     */
    @PostMapping("/update")
    public String updateMascota(@Valid @ModelAttribute("mascota") Mascota mascota, BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (mascota.getNombre() != null) {
                mascota.setNombre(mascota.getNombre().trim());
            }
            // La foto actual se lee de la base de datos (el formulario no la envía)
            Optional<Mascota> mascotaActual = mascotaRepository.findById(mascota.getId());
            if (mascotaActual.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.edit.notFound", null, locale));
                return "redirect:/pets";
            }
            String fotoAnterior = mascotaActual.get().getFoto();
            mascota.setFoto(fotoAnterior);
            if (result.hasErrors()) {
                return "pets/pets-form";
            }
            // Guardar la imagen subida
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName == null) {
                    result.rejectValue("foto", "msg.pet.foto.invalid");
                    return "pets/pets-form";
                }
                mascota.setFoto(fileName);
            }
            mascotaRepository.save(mascota);
            // Eliminar la foto anterior, si había y se ha sustituido
            if (!imageFile.isEmpty() && fotoAnterior != null && !fotoAnterior.isEmpty()) {
                fileStorageService.deleteFile(fotoAnterior);
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.update.error", null, locale));
        }
        return "redirect:/pets";
    }

    /**
     * Elimina una mascota (y su foto del servidor) y vuelve al listado. Si tiene ficha clínica o consultas
     * asociadas la base de datos lo impide y se muestra un aviso.
     */
    @PostMapping("/delete")
    public String deleteMascota(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Optional<Mascota> mascotaEliminada = mascotaRepository.findById(id);
            mascotaRepository.deleteById(id);
            // Eliminar la imagen asociada, si existe
            if (mascotaEliminada.isPresent() && mascotaEliminada.get().getFoto() != null && !mascotaEliminada.get().getFoto().isEmpty()) {
                fileStorageService.deleteFile(mascotaEliminada.get().getFoto());
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("msg.pet-controller.delete.error", null, locale));
        }
        return "redirect:/pets";
    }
}
