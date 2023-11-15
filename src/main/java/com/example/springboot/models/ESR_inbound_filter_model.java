package com.example.springboot.models;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ESR_INBOUND_FILTER")
public class ESR_inbound_filter_model extends RepresentationModel<ESR_inbound_filter_model> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID esr_inbound_filter_pkey;
    public String inbound_event_type;
    public String esr_status;
    @Column(name="sent_to_system")
    public String sent;
    public String message;
    public Date created_dttm;
    public Date updated_dttm;
    @Getter
    public String esr_inbound_filter_id;

    public ESR_inbound_filter_model(String inbound_event_type, String esr_inbound_filter_id, String esr_status, String sent_to_system, String message, Date created_dttm, Date updated_dttm){
        this.inbound_event_type = inbound_event_type;
        this.esr_status = esr_status;
        this.sent= sent_to_system;
        this.message = message;
        this.created_dttm = created_dttm;
        this.updated_dttm = updated_dttm;
        this.esr_inbound_filter_id = esr_inbound_filter_id;
    }

    public ESR_inbound_filter_model() {

    }
}
