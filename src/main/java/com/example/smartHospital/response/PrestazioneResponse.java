package com.example.smartHospital.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestazioneResponse {

    private Long id;
    private String nome;
    private Double prezzo;

}
