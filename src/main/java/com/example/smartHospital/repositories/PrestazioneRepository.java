package com.example.smartHospital.repositories;

import com.example.smartHospital.entities.Prestazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestazioneRepository extends JpaRepository<Prestazione, Long> {
}