package com.example.smartHospital.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prestazione")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double prezzo;

    @ManyToOne
    @JoinColumn(name = "tariffario_id")
    private Tariffario tariffario;
}