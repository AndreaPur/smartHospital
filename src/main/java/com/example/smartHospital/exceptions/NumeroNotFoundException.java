package com.example.smartHospital.exceptions;

public class NumeroNotFoundException extends Exception{

    @Override
    public String getMessage() {
        return "Inserisci almeno un numero";
    }
}
