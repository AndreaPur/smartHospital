package com.example.smartHospital.repositories;

import com.example.smartHospital.entities.Tariffario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffarioRepository extends JpaRepository<Tariffario, Long> {
}
