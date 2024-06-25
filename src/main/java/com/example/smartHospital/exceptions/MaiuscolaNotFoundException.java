package com.example.smartHospital.exceptions;

public class MaiuscolaNotFoundException extends Exception {

    @Override
    public String getMessage() {
        return "Inserisci almeno un carattere maiusolo";
    }
}
