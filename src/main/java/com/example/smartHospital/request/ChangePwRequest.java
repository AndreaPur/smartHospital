package com.example.smartHospital.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePwRequest {

    private Long id_utente;
    private String oldPassword;
    private String newPassword;

}
