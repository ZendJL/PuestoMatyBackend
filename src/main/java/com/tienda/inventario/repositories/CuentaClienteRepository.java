package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.inventario.entities.CuentaCliente;

public interface CuentaClienteRepository extends JpaRepository<CuentaCliente, Integer> {
    
    List<CuentaCliente> findByNombreContainingIgnoreCase(String nombre);
    List<CuentaCliente> findBySaldoGreaterThan(Float saldo);
    List<CuentaCliente> findBySaldoLessThanEqual(Float saldo);

@Query(value = """
    SELECT 
        cc.id,                          
        cc.nombre,                      
        cc.descripcion,                 
        COALESCE(cc.saldo, 0) as saldo, 
        COALESCE(SUM(v.total), 0) as total_facturado,      
        COUNT(v.id) as total_ventas,       
        COALESCE(SUM(a.cantidad), 0) as total_pagado,      
        COALESCE(SUM(v.total) - COALESCE(SUM(a.cantidad), 0), 0) as saldo_calculado, 
        -- ✅ [8] SOLO ABONO O VENTA (SIN updated_at)
        COALESCE(
            (SELECT MAX(a2.fecha) FROM abonos a2 WHERE a2.cuenta_id = cc.id),
            (SELECT MAX(v2.fecha) FROM ventas v2 WHERE v2.cuenta_id = cc.id),
            '1900-01-01 00:00:00'
        ) as ultima_actividad
    FROM cuenta_cliente cc 
    LEFT JOIN ventas v ON v.cuenta_id = cc.id 
    LEFT JOIN abonos a ON a.cuenta_id = cc.id
    GROUP BY cc.id, cc.nombre, cc.descripcion, cc.saldo 
    ORDER BY COALESCE(saldo, 0) DESC
    """, nativeQuery = true)
List<Object[]> resumenCompletoNative();



    @Query(value = """
    SELECT a.id, a.cantidad, a.viejo_saldo, a.nuevo_saldo, a.fecha
    FROM abonos a 
    WHERE a.cuenta_id = :cuentaId 
    ORDER BY a.fecha DESC 
    """, nativeQuery = true)
List<Object[]> ultimosAbonosByCuenta(@Param("cuentaId") Long cuentaId);



    @Query(value = """
        SELECT 
            v.id,           -- [0] id (Number)
            v.id,           -- [1] venta_id (Number)  
            0.0,            -- [2] pagocliente (Number)
            DATE_FORMAT(v.fecha, '%d/%m/%Y %h:%i %p'),  -- [3] fecha (String)
            COALESCE(v.status, 'PENDIENTE'),             -- [4] status (String)
            COALESCE(v.total, 0.0)                       -- [5] total (Number)
        FROM ventas v 
        WHERE v.cuenta_id = :cuentaId 
        ORDER BY v.fecha DESC 
        """, nativeQuery = true)
    List<Object[]> ultimasVentasByCuenta(@Param("cuentaId") Long cuentaId);

    @Query(value = """
    SELECT 
        c.id, c.nombre, c.descripcion,
        COUNT(v.id) as total_ventas,
        COALESCE(SUM(v.total), 0) as total_facturado,
        COALESCE(SUM(va.cantidad), 0) as total_pagado,
        COALESCE(SUM(v.total) - COALESCE(SUM(va.cantidad), 0), 0) as saldo,
        GROUP_CONCAT(v.id ORDER BY v.fecha DESC SEPARATOR ',') as ultimas_ventas_ids
    FROM cuenta_cliente c 
    LEFT JOIN ventas v ON v.cuenta_id = c.id AND v.status = 'PRESTAMO'
    LEFT JOIN venta_abonos va ON va.venta_id = v.id
    GROUP BY c.id, c.nombre, c.descripcion
    HAVING saldo > 0
    ORDER BY saldo DESC
    """, nativeQuery = true)
List<Object[]> resumenCuentasDeudores();
}
