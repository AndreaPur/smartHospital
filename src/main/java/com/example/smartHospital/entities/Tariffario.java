package com.example.smartHospital.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tariffario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariffario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "tariffario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestazione> prestazioni;

    @OneToMany(mappedBy = "tariffario", cascade = CascadeType.ALL)
    private List<Medico> medici;
}