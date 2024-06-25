package com.example.smartHospital.repositories;

import com.example.smartHospital.entities.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Query(value = "SELECT * FROM utente u WHERE u.role = 'MEDICO' AND u.specializzazione = :specializzazione", nativeQuery = true)
    List<Medico> findByRoleAndSpecializzazione(@Param("specializzazione") String specializzazione);
}
