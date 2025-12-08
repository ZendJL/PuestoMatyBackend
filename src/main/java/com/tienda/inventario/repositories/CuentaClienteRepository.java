package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.CuentaCliente;

public interface CuentaClienteRepository extends JpaRepository<CuentaCliente, Long> {

    // Buscar por nombre
    List<CuentaCliente> findByNombreContainingIgnoreCase(String nombre);

    // Cuentas con deuda (saldo > 0)
    List<CuentaCliente> findBySaldoGreaterThan(Float saldo);

    // Cuentas en saldo cero o a favor
    List<CuentaCliente> findBySaldoLessThanEqual(Float saldo);
}
