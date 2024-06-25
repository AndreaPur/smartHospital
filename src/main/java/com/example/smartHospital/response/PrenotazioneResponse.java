package com.example.smartHospital.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrenotazioneResponse {

    private Long id;
    private Long idUtente;
    private String nomeUtente;
    private String cognomeUtente;
    private String codiceFiscaleUtente;
    private Long idVisita;
    private Long idMedico;
    private String nomeMedico;
    private String cognomeMedico;
    private String specializzazione;
    private LocalDateTime orario;
    private LocalDateTime timestamp;

}
