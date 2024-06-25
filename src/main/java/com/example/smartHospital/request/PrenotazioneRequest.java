package com.example.smartHospital.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrenotazioneRequest {

    private Long idUtente;
    private Long idVisita;

}
