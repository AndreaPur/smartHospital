package com.example.smartHospital.repositories;

import com.example.smartHospital.entities.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitaRepository extends JpaRepository <Visita, Long> {

    @Query(value = "SELECT referto FROM visita WHERE id =:id_visita", nativeQuery = true)
    String getFilePath(@Param("id_visita") Long id_visita);

}