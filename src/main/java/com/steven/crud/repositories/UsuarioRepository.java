package com.steven.crud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steven.crud.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{ 
    boolean existsByName(String name);
}
