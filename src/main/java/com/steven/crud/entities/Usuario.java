package com.steven.crud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    @NotBlank(message = "name cannot be null")
    private String name;
    
    @Column(name = "user_password")
    @NotBlank(message = "password cannot be null")
    private String password;

    @Lob
    @Column(name = "user_picture", columnDefinition="BLOB")
    private byte[] picture; 
}
