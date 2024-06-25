package com.example.smartHospital.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoResponse {

    private Long id;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String comune;
    private String indirizzo;
    private String telefono;
    private LocalDate dataNascita;
    private String specializzazione;
    private LocalDateTime orarioInizio;
    private LocalDateTime orarioFine;
    private List<Long> idVisite;

}