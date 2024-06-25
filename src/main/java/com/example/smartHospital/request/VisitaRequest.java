package com.example.smartHospital.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaRequest {

    private Long idMedico;
    private String specializzazione;
    private LocalDateTime orario;

}
