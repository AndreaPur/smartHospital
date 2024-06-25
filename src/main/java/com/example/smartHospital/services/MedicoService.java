package com.example.smartHospital.services;

import com.example.smartHospital.entities.Medico;
import com.example.smartHospital.entities.Visita;
import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.exceptions.FuturoException;
import com.example.smartHospital.repositories.MedicoRepository;
import com.example.smartHospital.request.MedicoRequest;
import com.example.smartHospital.response.MedicoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public MedicoResponse getMedicoById(Long id) throws EntityNotFoundException {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Medico"));
        return convertToResponse(medico);
    }

    public List<MedicoResponse> getAllMedici() {
        List<Medico> medici = medicoRepository.findAll();
        return medici.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public MedicoResponse createMedico(MedicoRequest request) throws FuturoException {
        if (request.getDataNascita().isAfter(LocalDate.now())) {
            throw new FuturoException();
        }
        LocalDateTime orarioInizio = LocalDateTime.of(LocalDate.now(), LocalTime.of(request.getOrarioInizio(), 0));
        LocalDateTime orarioFine = LocalDateTime.of(LocalDate.now(), LocalTime.of(request.getOrarioFine(), 0));
        Medico medico = Medico.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .email(request.getEmail())
                .comune(request.getComune())
                .indirizzo(request.getIndirizzo())
                .telefono(request.getTelefono())
                .dataNascita(request.getDataNascita())
                .password(request.getPassword())
                .specializzazione(request.getSpecializzazione())
                .orarioInizio(orarioInizio)
                .orarioFine(orarioFine)
                .build();
        medicoRepository.saveAndFlush(medico);
        return convertToResponse(medico);
    }

    public MedicoResponse updateMedico(Long id, MedicoRequest updatedMedicoRequest) throws FuturoException {
        Optional<Medico> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isPresent()) {
            Medico medico = medicoOptional.get();
            if (updatedMedicoRequest.getDataNascita().isAfter(LocalDate.now())) {
                throw new FuturoException();
            }
            LocalDateTime orarioInizio = LocalDateTime.of(LocalDate.now(), LocalTime.of(updatedMedicoRequest.getOrarioInizio(), 0));
            LocalDateTime orarioFine = LocalDateTime.of(LocalDate.now(), LocalTime.of(updatedMedicoRequest.getOrarioFine(), 0));
            medico.setNome(updatedMedicoRequest.getNome());
            medico.setCognome(updatedMedicoRequest.getCognome());
            medico.setCodiceFiscale(updatedMedicoRequest.getCodiceFiscale());
            medico.setEmail(updatedMedicoRequest.getEmail());
            medico.setIndirizzo(updatedMedicoRequest.getIndirizzo());
            medico.setTelefono(updatedMedicoRequest.getTelefono());
            medico.setDataNascita(updatedMedicoRequest.getDataNascita());
            medico.setPassword(updatedMedicoRequest.getPassword());
            medico.setSpecializzazione(updatedMedicoRequest.getSpecializzazione());
            medico.setOrarioInizio(orarioInizio);
            medico.setOrarioFine(orarioFine);
            Medico savedMedico = medicoRepository.saveAndFlush(medico);
            return convertToResponse(savedMedico);
        } else {
            return null;
        }
    }

    public void deleteMedicoById(Long id) {
        medicoRepository.deleteById(id);
    }

    public List<MedicoResponse> findByRoleAndSpecializzazione(String specializzazione) throws EntityNotFoundException {
        List<Medico> medici = medicoRepository.findByRoleAndSpecializzazione(specializzazione);
        if (medici.isEmpty()) {
            throw new EntityNotFoundException(-1L, "Medico");
        }
        return medici.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private MedicoResponse convertToResponse(Medico medico) {
        List<Long> idVisite = medico.getVisite().stream()
                .map(Visita::getId)
                .collect(Collectors.toList());
        if (idVisite.isEmpty()) {
            idVisite = null;
        }
        return MedicoResponse.builder()
                .id(medico.getId())
                .nome(medico.getNome())
                .cognome(medico.getCognome())
                .codiceFiscale(medico.getCodiceFiscale())
                .email(medico.getEmail())
                .indirizzo(medico.getIndirizzo())
                .telefono(medico.getTelefono())
                .dataNascita(medico.getDataNascita())
                .specializzazione(medico.getSpecializzazione())
                .orarioInizio(medico.getOrarioInizio())
                .orarioFine(medico.getOrarioFine())
                .idVisite(idVisite)
                .build();
    }
}