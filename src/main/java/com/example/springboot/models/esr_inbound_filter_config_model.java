package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ESR_INBOUND_FILTER")
public class esr_inbound_filter_config_model extends RepresentationModel<esr_inbound_filter_config_model> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID esr_inbound_filter_config_id;
    private String config_type;
    private String config_message_json;
    private Date created_dttm;
    private Date updated_dttm;

    public esr_inbound_filter_config_model(String config_type, String config_message_json, Date created_dttm, Date updated_dttm){
        this.config_type = config_type;
        this.config_message_json = config_message_json;
        this.created_dttm = created_dttm;
        this.updated_dttm = updated_dttm;
    }
}
