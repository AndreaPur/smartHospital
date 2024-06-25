package com.example.smartHospital.services;

import com.example.smartHospital.entities.Prenotazione;
import com.example.smartHospital.entities.Utente;
import com.example.smartHospital.entities.Visita;
import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.exceptions.FuturoException;
import com.example.smartHospital.repositories.PrenotazioneRepository;
import com.example.smartHospital.repositories.UtenteRepository;
import com.example.smartHospital.repositories.VisitaRepository;
import com.example.smartHospital.request.PrenotazioneRequest;
import com.example.smartHospital.response.PrenotazioneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private VisitaRepository visitaRepository;

    public PrenotazioneResponse getPrenotazioneById(Long id) throws EntityNotFoundException {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Prenotazione"));
        return convertToResponse(prenotazione);
    }

    public List<PrenotazioneResponse> getAllPrenotazioni() {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        return prenotazioni.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public PrenotazioneResponse createPrenotazione(PrenotazioneRequest request) throws EntityNotFoundException, FuturoException {
        Utente utente = utenteRepository.findById(request.getIdUtente())
                .orElseThrow(() -> new EntityNotFoundException(request.getIdUtente(), "Utente"));

        Visita visita = visitaRepository.findById(request.getIdVisita())
                .orElseThrow(() -> new EntityNotFoundException(request.getIdVisita(), "Visita"));

        if (visita.getOrario().isBefore(LocalDateTime.now())) {
            throw new FuturoException();
        }

        visita.setUtente(utente);
        visita.setPrenotata(true);
        visitaRepository.saveAndFlush(visita);

        Prenotazione prenotazione = Prenotazione.builder()
                .utente(utente)
                .visita(visita)
                .timestamp(LocalDateTime.now())
                .build();

        prenotazioneRepository.saveAndFlush(prenotazione);
        return convertToResponse(prenotazione);
    }

    public PrenotazioneResponse updatePrenotazione(Long id, PrenotazioneRequest updatedRequest) throws EntityNotFoundException, FuturoException {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Prenotazione"));

        Utente utente = utenteRepository.findById(updatedRequest.getIdUtente())
                .orElseThrow(() -> new EntityNotFoundException(updatedRequest.getIdUtente(), "Utente"));

        Visita visita = visitaRepository.findById(updatedRequest.getIdVisita())
                .orElseThrow(() -> new EntityNotFoundException(updatedRequest.getIdVisita(), "Visita"));

        if (visita.getOrario().isBefore(LocalDateTime.now())) {
            throw new FuturoException();
        }

        visita.setUtente(utente);
        visita.setPrenotata(true);
        visitaRepository.saveAndFlush(visita);

        prenotazione.setUtente(utente);
        prenotazione.setVisita(visita);
        prenotazione.setTimestamp(LocalDateTime.now());

        prenotazioneRepository.saveAndFlush(prenotazione);
        return convertToResponse(prenotazione);
    }

    public void deletePrenotazioneById(Long id) throws EntityNotFoundException {
        if (!prenotazioneRepository.existsById(id)) {
            throw new EntityNotFoundException(id, "Prenotazione");
        }
        prenotazioneRepository.deleteById(id);
    }

    private PrenotazioneResponse convertToResponse(Prenotazione prenotazione) {
        Visita visita = prenotazione.getVisita();
        Utente utente = prenotazione.getUtente();
        return PrenotazioneResponse.builder()
                .id(prenotazione.getId())
                .idUtente(utente.getId())
                .nomeUtente(utente.getNome())
                .cognomeUtente(utente.getCognome())
                .codiceFiscaleUtente(utente.getCodiceFiscale())
                .idVisita(visita.getId())
                .idMedico(visita.getMedico().getId())
                .nomeMedico(visita.getMedico().getNome())
                .cognomeMedico(visita.getMedico().getCognome())
                .specializzazione(visita.getSpecializzazione())
                .orario(visita.getOrario())
                .timestamp(prenotazione.getTimestamp())
                .build();
    }
}