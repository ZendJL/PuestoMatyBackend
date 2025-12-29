package com.tienda.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;

public interface AbonoRepository extends JpaRepository<Abono, Integer> {

    // Abonos de una cuenta
    List<Abono> findByCuentaOrderByFechaDesc(CuentaCliente cuenta);

    // Abonos por rango de fechas
    List<Abono> findByCuentaAndFechaBetweenOrderByFechaDesc(
            CuentaCliente cuenta, LocalDateTime desde, LocalDateTime hasta);

    // Todos los abonos por rango de fechas
    List<Abono> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);


     @Query("SELECT a FROM Abono a WHERE a.cuenta = :cuenta")
    List<Abono> findByCuentaCliente(@Param("cuenta") CuentaCliente cuenta);

}
