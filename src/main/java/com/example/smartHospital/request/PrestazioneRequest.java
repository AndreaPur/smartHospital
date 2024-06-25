package com.example.smartHospital.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestazioneRequest {

    private String nome;
    private Double prezzo;

}
