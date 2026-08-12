package com.repairworkshop.appointment.repository;

import com.repairworkshop.appointment.entity.Appointment;
import com.repairworkshop.appointment.enums.AppointmentStatus;
import com.repairworkshop.appointment.enums.RepairCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByRepairCategory(RepairCategory category);
    List<Appointment> findByCustomerEmail(String email);
    List<Appointment> findByAssignedTechnicianId(Long technicianId);
}
