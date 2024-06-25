package com.example.smartHospital.services;

import com.example.smartHospital.entities.Utente;
import com.example.smartHospital.enums.Role;
import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.exceptions.FuturoException;
import com.example.smartHospital.repositories.UtenteRepository;
import com.example.smartHospital.request.UtenteRequest;
import com.example.smartHospital.response.MedicoResponse;
import com.example.smartHospital.response.UtenteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    public UtenteResponse getUtenteById(Long id) throws EntityNotFoundException {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Utente"));
        return convertToResponse(utente);
    }

    public List<UtenteResponse> getAll() {
        List<Utente> utenti = utenteRepository.findAll();
        return utenti.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public UtenteResponse createUtente(UtenteRequest request) throws FuturoException {
        if (request.getDataNascita().isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
            throw new FuturoException();
        }
        Utente utente = Utente.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .email(request.getEmail())
                .comune(request.getComune())
                .indirizzo(request.getIndirizzo())
                .telefono(request.getTelefono())
                .dataNascita(request.getDataNascita())
                .password(request.getPassword())
                .build();
        utenteRepository.saveAndFlush(utente);
        return convertToResponse(utente);
    }

    public UtenteResponse updateUtente(Long id, UtenteRequest updatedUtenteRequest) throws FuturoException {
        Optional<Utente> utenteOptional = utenteRepository.findById(id);
        if (utenteOptional.isPresent()) {
            Utente utente = utenteOptional.get();
            if (updatedUtenteRequest.getDataNascita().isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
                throw new FuturoException();
            }
            Utente updatedUtente = Utente.builder()
                    .id(utente.getId())
                    .nome(updatedUtenteRequest.getNome())
                    .cognome(updatedUtenteRequest.getCognome())
                    .codiceFiscale(updatedUtenteRequest.getCodiceFiscale())
                    .email(updatedUtenteRequest.getEmail())
                    .comune(utente.getComune())
                    .indirizzo(updatedUtenteRequest.getIndirizzo())
                    .telefono(updatedUtenteRequest.getTelefono())
                    .dataNascita(updatedUtenteRequest.getDataNascita())
                    .password(updatedUtenteRequest.getPassword())
                    .build();
            Utente savedUtente = utenteRepository.saveAndFlush(updatedUtente);
            return convertToResponse(savedUtente);
        } else {
            return null;
        }
    }

    public void deleteUtenteById(Long id) {
        utenteRepository.deleteById(id);
    }

    private UtenteResponse convertToResponse(Utente utente) {
        return UtenteResponse.builder()
                .id(utente.getId())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .codiceFiscale(utente.getCodiceFiscale())
                .email(utente.getEmail())
                .indirizzo(utente.getIndirizzo())
                .telefono(utente.getTelefono())
                .dataNascita(utente.getDataNascita())
                .comune(utente.getComune())
                .build();
    }
}

