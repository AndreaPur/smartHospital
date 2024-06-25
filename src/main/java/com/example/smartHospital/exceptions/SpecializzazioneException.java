package com.example.smartHospital.exceptions;

public class SpecializzazioneException extends Exception{

    @Override
    public String getMessage() {
        return "La specializzazione richiesta non è di competenza del dottore richiesto!";
    }
}
