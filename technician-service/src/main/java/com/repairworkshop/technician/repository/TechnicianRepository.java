package com.repairworkshop.technician.repository;

import com.repairworkshop.technician.entity.Technician;
import com.repairworkshop.technician.enums.Specialisation;
import com.repairworkshop.technician.enums.TechnicianStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    List<Technician> findByStatus(TechnicianStatus status);

    List<Technician> findBySpecialisation(Specialisation specialisation);

    // Used by Appointment Service via Feign call
    Optional<Technician> findFirstBySpecialisationAndStatus(
            Specialisation specialisation, TechnicianStatus status);

    boolean existsByEmail(String email);
}
