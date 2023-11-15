package com.example.springboot.repositories;

import com.example.springboot.models.ESR_inbound_filter_model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ESR_inbound_filter_model_repository extends JpaRepository<ESR_inbound_filter_model, UUID> {
    List<ESR_inbound_filter_model> findBysent_to_system(String sent_to_system);
}
