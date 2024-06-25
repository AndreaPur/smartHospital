package com.example.smartHospital.repositories;

import com.example.smartHospital.entities.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {

    @Query(value = "SELECT * FROM token_black_list WHERE utente_id = :id_utente", nativeQuery = true)
    List<TokenBlackList> getTokenBlackListFromUtenteId(@Param("id_utente") Long id_utente);
}
