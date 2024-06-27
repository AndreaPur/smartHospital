package com.example.smartHospital.services;

import com.example.smartHospital.entities.*;
import com.example.smartHospital.exceptions.*;
import com.example.smartHospital.repositories.PrestazioneRepository;
import com.example.smartHospital.repositories.UtenteRepository;
import com.example.smartHospital.repositories.VisitaRepository;
import com.example.smartHospital.request.VisitaRequest;
import com.example.smartHospital.response.GenericResponse;
import com.example.smartHospital.response.VisitaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitaService {

    @Autowired
    private VisitaRepository visitaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PrestazioneRepository prestazioneRepository;

    public VisitaResponse getVisitaById(Long id) throws EntityNotFoundException {
        Visita visita = visitaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Visita"));
        return convertToResponse(visita);
    }

    public List<VisitaResponse> getAll() {
        List<Visita> visite = visitaRepository.findAll();
        return visite.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public VisitaResponse createVisita(VisitaRequest request) throws PassatoException, NonDisponibileException, SpecializzazioneException, EntityNotFoundException {
        if (request.getOrario().isBefore(LocalDateTime.now())) {
            throw new PassatoException();
        }
        Utente utente = utenteRepository.findById(request.getIdMedico())
                .orElseThrow(() -> new EntityNotFoundException(request.getIdMedico(), "Medico"));
        if (utente instanceof Medico) {
            Medico medico = (Medico) utente;
            if (!medico.getSpecializzazione().equals(request.getSpecializzazione())) {
                throw new SpecializzazioneException();
            }
            Visita visita = Visita.builder()
                    .medico(medico)
                    .specializzazione(request.getSpecializzazione())
                    .orario(request.getOrario())
                    .prenotata(false)
                    .build();
            visitaRepository.saveAndFlush(visita);
            return convertToResponse(visita);
        } else {
            throw new EntityNotFoundException(request.getIdMedico(), "Medico");
        }
    }

    public VisitaResponse updateVisita(Long id, VisitaRequest updatedRequest) throws PassatoException, NonDisponibileException, SpecializzazioneException, EntityNotFoundException, VisitaConclusaException {
        if (updatedRequest.getOrario().isBefore(LocalDateTime.now())) {
            throw new PassatoException();
        }
        Visita visita = visitaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Visita"));
        if (visita.isConclusa()) { throw new VisitaConclusaException(); }
        Medico medico = (Medico) utenteRepository.findById(updatedRequest.getIdMedico())
                .orElseThrow(() -> new EntityNotFoundException(updatedRequest.getIdMedico(), "Medico"));
        if (!medico.getSpecializzazione().equals(updatedRequest.getSpecializzazione())) {
            throw new SpecializzazioneException();
        }
        visita.setMedico(medico);
        visita.setSpecializzazione(updatedRequest.getSpecializzazione());
        visita.setOrario(updatedRequest.getOrario());
        visitaRepository.saveAndFlush(visita);
        return convertToResponse(visita);
    }

    public void deleteVisitaById(Long id) throws EntityNotFoundException {
        if (!visitaRepository.existsById(id)) {
            throw new EntityNotFoundException(id, "Visita");
        }
        visitaRepository.deleteById(id);
    }

    public void aggiungiPrestazione(Long visitaId, Long prestazioneId) throws EntityNotFoundException, VisitaConclusaException {
        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(() -> new EntityNotFoundException(visitaId, "Visita"));
        if (visita.isConclusa()) { throw new VisitaConclusaException(); }
        Prestazione prestazione = prestazioneRepository.findById(prestazioneId)
                .orElseThrow(() -> new EntityNotFoundException(prestazioneId, "Prestazione"));
        if (prestazione.getTariffario() == null) {
            throw new EntityNotFoundException(prestazioneId, "Prestazione non associata a nessun tariffario");
        }
        if (!prestazione.getTariffario().getPrestazioni().contains(prestazione)) {
            throw new EntityNotFoundException(prestazioneId, "Prestazione non trovata nel tariffario");
        }
        visita.getPrestazioni().add(prestazione);
        double onorarioProvvisorio = calcolaOnorario(visita);
        visita.setOnorario(onorarioProvvisorio);
        visitaRepository.save(visita);
    }

    private double calcolaOnorario(Visita visita) {
        return visita.getPrestazioni().stream()
                .map(Prestazione::getPrezzo)
                .filter(prezzo -> prezzo != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public GenericResponse concludeVisita(Long visitaId) throws EntityNotFoundException, VisitaConclusaException {
        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(() -> new EntityNotFoundException(visitaId, "Visita"));
        if (visita.isConclusa()) { throw new VisitaConclusaException(); }
        visita.setConclusa(true);
        visitaRepository.saveAndFlush(visita);
        String message = "Il totale della visita è " + visita.getOnorario() + " euro.";
        return new GenericResponse(message);
    }

    public List<VisitaResponse> getVisiteByUtenteId(Long utenteId) throws EntityNotFoundException {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new EntityNotFoundException(utenteId, "Utente"));
        List<Visita> visite = visitaRepository.findByUtenteId(utenteId);
        return visite.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public void uploadReferto(Long id, String path) throws EntityNotFoundException {
        Visita visita = visitaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Visita"));
        visita.setReferto(path);
        visita.setInsertTimeReferto(LocalDateTime.now());
        visitaRepository.saveAndFlush(visita);
    }

    public String getPath(Long id) throws EntityNotFoundException {
        if (!visitaRepository.existsById(id)) {
            throw new EntityNotFoundException(id, "Visita");
        }
        return visitaRepository.getFilePath(id);
    }

    private VisitaResponse convertToResponse(Visita visita) {
        return VisitaResponse.builder()
                .id(visita.getId())
                .idUtente(visita.getUtente() != null ? visita.getUtente().getId() : null)
                .nomeUtente(visita.getUtente() != null ? visita.getUtente().getNome() : null)
                .cognomeUtente(visita.getUtente() != null ? visita.getUtente().getCognome() : null)
                .codiceFiscale(visita.getUtente() != null ? visita.getUtente().getCodiceFiscale() : null)
                .idMedico(visita.getMedico().getId())
                .nomeMedico(visita.getMedico().getNome())
                .cognomeMedico(visita.getMedico().getCognome())
                .specializzazione(visita.getSpecializzazione())
                .orario(visita.getOrario())
                .referto(visita.getReferto())
                .insertTimeReferto(visita.getInsertTimeReferto())
                .prenotata(visita.isPrenotata())
                .nomePrestazioni(visita.getPrestazioni() != null ? visita.getPrestazioni().stream().map(Prestazione::getNome).collect(Collectors.toList()) : null)
                .costoPrestazioni(visita.getPrestazioni() != null ? visita.getPrestazioni().stream()
                        .map(Prestazione::getPrezzo)
                        .filter(prezzo -> prezzo != null)
                        .collect(Collectors.toList()) : null)
                .onorario(visita.getPrestazioni() != null && !visita.getPrestazioni().isEmpty() ? visita.getOnorario() : 0.0)
                .conclusa(visita.isConclusa())
                .build();
    }
}