package com.example.smartHospital.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
    @Column
    private String specializzazione;
    @Column
    private LocalDateTime orario;
    @Column(nullable = false)
    private boolean prenotata;
    @ManyToMany
    @JoinTable(
            name = "visita_prestazione",
            joinColumns = @JoinColumn(name = "visita_id"),
            inverseJoinColumns = @JoinColumn(name = "prestazione_id")
    )
    private List<Prestazione> prestazioni;
    @Column(nullable = false)
    private double onorario;
    @Column(nullable = false)
    private boolean conclusa = false;
    @Column
    private String referto;
    @Column
    private LocalDateTime insertTimeReferto;

}
