package com.steven.crud.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.steven.crud.entities.Usuario;
import com.steven.crud.repositories.UsuarioRepository;

@RestController
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<?> getUsuarios() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            if (usuarios.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(usuarios);
        } catch (DataAccessException e) {
            String errorMessage = "Error al recuperar la lista de usuarios: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping
    public ResponseEntity<?> postUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuarioRepository.existsByName(usuario.getName())) {
                String errorMessage = "El Usuario ya existe";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            }

            Usuario newUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUsuario);

        } catch (Exception e) {
            String errorMessage = "Error al crear el usuario: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delUsuario(@PathVariable Long id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                String errorMessage = "El usuario con ID " + id + " no existe";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }
            usuarioRepository.deleteById(id);

            String successMessage = "Usuario con ID " + id + " eliminado correctamente";
            return ResponseEntity.ok(successMessage);

        } catch (EmptyResultDataAccessException ex) {
            String errorMessage = "No se encontró ningún usuario con ID " + id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error al eliminar el usuario con ID " + id + ": " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/{id}/uploadImg")
    public ResponseEntity<?> postUsuarioImg(@PathVariable Long id, @RequestParam("img") MultipartFile img) {
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                usuario.setPicture(img.getBytes());
                usuarioRepository.save(usuario);
                return ResponseEntity.ok("Imagen guardada correctamente para el usuario con ID: " + id);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/getImg")
    public ResponseEntity<?> getUsaurioImg(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                if (usuario.getPicture() != null) {
                    ByteArrayResource resource = new ByteArrayResource(usuario.getPicture());
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.IMAGE_JPEG); // Cambiar a MediaType.IMAGE_PNG si es PNG
                    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener la imagen del usuario: " + e.getMessage());
        }
    }
}
