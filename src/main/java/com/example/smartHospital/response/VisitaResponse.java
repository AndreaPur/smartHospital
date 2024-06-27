package com.example.smartHospital.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaResponse {

    private Long id;
    private Long idUtente;
    private String nomeUtente;
    private String cognomeUtente;
    private String codiceFiscale;
    private Long idMedico;
    private String nomeMedico;
    private String cognomeMedico;
    private String specializzazione;
    private LocalDateTime orario;
    private String referto;
    private LocalDateTime insertTimeReferto;
    private boolean prenotata;
    private List<String> nomePrestazioni;
    private List<Double> costoPrestazioni;
    private double onorario;
    private boolean conclusa;

}
