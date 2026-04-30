package com.medicore.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medicore.system.model.entity.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    boolean existsByNombre(String nombre);
}
