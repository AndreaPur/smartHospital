package com.example.smartHospital.services;

import com.example.smartHospital.entities.Medico;
import com.example.smartHospital.entities.Prestazione;
import com.example.smartHospital.entities.Tariffario;
import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.repositories.MedicoRepository;
import com.example.smartHospital.repositories.PrestazioneRepository;
import com.example.smartHospital.repositories.TariffarioRepository;
import com.example.smartHospital.request.TariffarioRequest;
import com.example.smartHospital.response.TariffarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TariffarioService {

    @Autowired
    private TariffarioRepository tariffarioRepository;

    @Autowired
    private PrestazioneRepository prestazioneRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public TariffarioResponse getTariffarioById(Long id) throws EntityNotFoundException {
        Tariffario tariffario = tariffarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Tariffario"));
        return convertToResponse(tariffario);
    }

    public List<TariffarioResponse> getAllTariffari() {
        List<Tariffario> tariffari = tariffarioRepository.findAll();
        return tariffari.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public TariffarioResponse createTariffario(TariffarioRequest request) throws EntityNotFoundException {
        List<Prestazione> prestazioni = prestazioneRepository.findAllById(request.getIdPrestazioni());
        List<Medico> medici = medicoRepository.findAllById(request.getIdMedici());

        Tariffario tariffario = Tariffario.builder()
                .nome(request.getNome())
                .prestazioni(prestazioni)
                .medici(medici)
                .build();
        prestazioni.forEach(prestazione -> prestazione.setTariffario(tariffario));
        medici.forEach(medico -> medico.setTariffario(tariffario));

        tariffarioRepository.saveAndFlush(tariffario);
        return convertToResponse(tariffario);
    }

    public TariffarioResponse updateTariffario(Long id, TariffarioRequest updatedTariffarioRequest) throws EntityNotFoundException {
        Tariffario tariffario = tariffarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Tariffario"));
        List<Prestazione> prestazioni = prestazioneRepository.findAllById(updatedTariffarioRequest.getIdPrestazioni());
        List<Medico> medici = medicoRepository.findAllById(updatedTariffarioRequest.getIdMedici());
        tariffario.setNome(updatedTariffarioRequest.getNome());
        tariffario.getPrestazioni().clear();
        tariffario.getPrestazioni().addAll(prestazioni);
        tariffario.getMedici().clear();
        tariffario.getMedici().addAll(medici);
        prestazioni.forEach(prestazione -> prestazione.setTariffario(tariffario));
        medici.forEach(medico -> medico.setTariffario(tariffario));

        Tariffario savedTariffario = tariffarioRepository.saveAndFlush(tariffario);
        return convertToResponse(savedTariffario);
    }

    public void deleteTariffarioById(Long id) {
        tariffarioRepository.deleteById(id);
    }

    private TariffarioResponse convertToResponse(Tariffario tariffario) {
        List<String> nomiPrestazioni = tariffario.getPrestazioni().stream()
                .map(Prestazione::getNome)
                .collect(Collectors.toList());
        if (nomiPrestazioni.isEmpty()) {
            nomiPrestazioni = null;
        }

        List<Double> prezziPrestazioni = tariffario.getPrestazioni().stream()
                .map(Prestazione::getPrezzo)
                .collect(Collectors.toList());
        if (prezziPrestazioni.isEmpty()) {
            prezziPrestazioni = null;
        }

        List<Long> idMedici = tariffario.getMedici().stream()
                .map(Medico::getId)
                .collect(Collectors.toList());
        if (idMedici.isEmpty()) {
            idMedici = null;
        }

        return TariffarioResponse.builder()
                .id(tariffario.getId())
                .nome(tariffario.getNome())
                .nomiPrestazioni(nomiPrestazioni)
                .prezziPrestazioni(prezziPrestazioni)
                .idMedici(idMedici)
                .build();
    }
}