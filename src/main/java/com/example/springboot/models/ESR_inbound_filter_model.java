package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
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
    private Date created_dttm;
    private Date updated_dttm;
    private String esr_inbound_filter_id;

    public ESR_inbound_filter_model(String inbound_event_type, String esr_inbound_filter_id, String esr_status, String sent_to_system, String message, Date created_dttm, Date updated_dttm){
        this.inbound_event_type = inbound_event_type;
        this.esr_status = esr_status;
        this.sent_to_system= sent_to_system;
        this.message = message;
        this.created_dttm = created_dttm;
        this.updated_dttm = updated_dttm;
        this.esr_inbound_filter_id = esr_inbound_filter_id;
    };
}
