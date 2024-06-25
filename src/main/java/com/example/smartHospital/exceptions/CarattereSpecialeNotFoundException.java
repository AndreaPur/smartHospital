package com.example.smartHospital.exceptions;

public class CarattereSpecialeNotFoundException extends Exception {

    @Override
    public String getMessage() {
        return "Inserisci almeno un carattere speciale nella nuova password";
    }
}
