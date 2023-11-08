package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "ESR_INBOUND_FILTER")
public class ESR_inbound_filter_model extends RepresentationModel<ESR_inbound_filter_model> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID esr_inbound_filter_pkey;
    private String inbound_event_type;
    private String esr_status;
    private String sent_to_system;
    private String message;
    private String created_dttm;
    private String updated_dttm;

}
