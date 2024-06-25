package com.example.smartHospital.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("MEDICO")
public class Medico extends Utente {

    @Column
    private String specializzazione;
    @Column(name = "orario_inizio")
    private LocalDateTime orarioInizio;
    @Column(name = "orario_fine")
    private LocalDateTime orarioFine;
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private List<Visita> visite;
    @ManyToOne
    @JoinColumn(name = "tariffario_id")
    private Tariffario tariffario;

}
