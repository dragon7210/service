package com.example.springboot.repositories;

        import com.example.springboot.models.esr_inbound_filter_config_model;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.stereotype.Repository;

        import java.util.List;
        import java.util.Optional;
        import java.util.UUID;
@Repository
public interface esr_inbound_filter_config_model_repository extends JpaRepository<esr_inbound_filter_config_model, UUID> {
//    List<esr_inbound_filter_config_model_repository> findBySent(String sent);
//    ESR_inbound_filter_model findByUuid(String uuid);
}
