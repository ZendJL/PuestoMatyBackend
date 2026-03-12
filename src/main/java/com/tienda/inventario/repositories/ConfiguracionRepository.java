package com.tienda.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Configuracion;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {}
