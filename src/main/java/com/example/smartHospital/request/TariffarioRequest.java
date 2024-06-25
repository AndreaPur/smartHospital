package com.example.smartHospital.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffarioRequest {

    private String nome;
    private List<Long> idPrestazioni;
    private List<Long> idMedici;

}
