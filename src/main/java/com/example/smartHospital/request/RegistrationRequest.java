package com.example.smartHospital.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {

    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String comune;
    private String indirizzo;
    private String telefono;
    private LocalDate dataNascita;
    private String password;

}
