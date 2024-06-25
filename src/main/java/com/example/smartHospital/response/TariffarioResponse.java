package com.example.smartHospital.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffarioResponse {

    private Long id;
    private String nome;
    private List<String> nomiPrestazioni;
    private List<Double> prezziPrestazioni;
    private List<Long> idMedici;
}
