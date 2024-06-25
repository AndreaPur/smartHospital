package com.example.smartHospital.services;

import com.example.smartHospital.entities.Prestazione;
import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.repositories.PrestazioneRepository;
import com.example.smartHospital.request.PrestazioneRequest;
import com.example.smartHospital.response.PrestazioneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestazioneService {

    @Autowired
    private PrestazioneRepository prestazioneRepository;

    public PrestazioneResponse getPrestazioneById(Long id) throws EntityNotFoundException {
        Prestazione prestazione = prestazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Prestazione"));
        return convertToResponse(prestazione);
    }

    public List<PrestazioneResponse> getAllPrestazioni() {
        List<Prestazione> prestazioni = prestazioneRepository.findAll();
        return prestazioni.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public PrestazioneResponse createPrestazione(PrestazioneRequest request) {
        Prestazione prestazione = Prestazione.builder()
                .nome(request.getNome())
                .prezzo(request.getPrezzo())
                .build();
        prestazioneRepository.saveAndFlush(prestazione);
        return convertToResponse(prestazione);
    }

    public PrestazioneResponse updatePrestazione(Long id, PrestazioneRequest updatedPrestazioneRequest) throws EntityNotFoundException {
        Prestazione prestazione = prestazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Prestazione"));
        prestazione.setNome(updatedPrestazioneRequest.getNome());
        prestazione.setPrezzo(updatedPrestazioneRequest.getPrezzo());
        Prestazione savedPrestazione = prestazioneRepository.saveAndFlush(prestazione);
        return convertToResponse(savedPrestazione);
    }

    public void deletePrestazioneById(Long id) {
        prestazioneRepository.deleteById(id);
    }

    private PrestazioneResponse convertToResponse(Prestazione prestazione) {
        return PrestazioneResponse.builder()
                .id(prestazione.getId())
                .nome(prestazione.getNome())
                .prezzo(prestazione.getPrezzo())
                .build();
    }
}