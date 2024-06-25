package com.example.smartHospital.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteResponse {

    private Long id;
    private String nome;
    private String cognome;
    private String comune;
    private LocalDate dataNascita;
    private String codiceFiscale;
    private String indirizzo;
    private String telefono;
    private String email;

}
