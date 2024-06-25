package com.example.smartHospital.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteRequest {

    private String nome;
    private String cognome;
    private String comune;
    private LocalDate dataNascita;
    private String codiceFiscale;
    private String indirizzo;
    private String telefono;
    private String email;
    private String password;

}
