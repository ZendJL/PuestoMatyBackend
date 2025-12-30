package com.tienda.inventario.repositories;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.inventario.entities.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Reportes de ventas por rango de fechas
    List<Venta> findByFechaBetweenOrderByFechaAsc(LocalDateTime desde, LocalDateTime hasta);

    // Ventas de un día (pasas desde=00:00 y hasta=23:59)
    List<Venta> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    // Ventas por estado (finalizada, en_proceso, cancelada, etc.)
    List<Venta> findByStatus(String status);

    @Query(value = """
    SELECT 
        -- ✅ RESUMEN VENTAS
        COUNT(v.id) as totalVentas,
        COALESCE(SUM(v.total), 0) as totalFacturado,
        COUNT(CASE WHEN v.status = 'COMPLETADA' THEN 1 END) as ventasCompletadas,
        AVG(v.total) as ticketPromedio,
        
        -- ✅ TOP 5 CLIENTES
        cc1.nombre as cliente1, SUM(CASE WHEN cc.id = cc1.id THEN v.total ELSE 0 END) as cliente1_ventas,
        cc2.nombre as cliente2, SUM(CASE WHEN cc.id = cc2.id THEN v.total ELSE 0 END) as cliente2_ventas,
        
        -- ✅ TOP 5 PRODUCTOS
        p1.descripcion as producto1, SUM(CASE WHEN p.id = p1.id THEN vp.cantidad ELSE 0 END) as producto1_cant,
        p2.descripcion as producto2, SUM(CASE WHEN p.id = p2.id THEN vp.cantidad ELSE 0 END) as producto2_cant
        
    FROM ventas v
    LEFT JOIN cuenta_cliente cc ON v.cuenta_id = cc.id
    LEFT JOIN venta_producto vp ON vp.venta_id = v.id
    LEFT JOIN productos p ON vp.producto_id = p.id
    LEFT JOIN cuenta_cliente cc1 ON cc1.id = (SELECT cc2.id FROM cuenta_cliente cc2 JOIN ventas v2 ON v2.cuenta_id = cc2.id WHERE v2.fecha BETWEEN :desde AND :hasta GROUP BY cc2.id ORDER BY SUM(v2.total) DESC LIMIT 1)
    LEFT JOIN cuenta_cliente cc2 ON cc2.id = (SELECT cc3.id FROM cuenta_cliente cc3 JOIN ventas v3 ON v3.cuenta_id = cc3.id WHERE v3.fecha BETWEEN :desde AND :hasta GROUP BY cc3.id ORDER BY SUM(v3.total) DESC LIMIT 1 OFFSET 1)
    LEFT JOIN productos p1 ON p1.id = (SELECT p2.id FROM productos p2 JOIN venta_producto vp2 ON vp2.producto_id = p2.id JOIN ventas v4 ON v4.id = vp2.venta_id WHERE v4.fecha BETWEEN :desde AND :hasta GROUP BY p2.id ORDER BY SUM(vp2.cantidad) DESC LIMIT 1)
    WHERE v.fecha BETWEEN :desde AND :hasta
    """, nativeQuery = true)
Object[] reporteVentasCompleto(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);


@Query(value = """
    SELECT 
        v.id,                    
        v.fecha,                 
        v.total,                 
        COALESCE(v.status, 'PENDIENTE'),  
        v.pago_cliente,          
        cc.id as cuenta_id,      
        cc.nombre as cuenta_nombre, 
        COUNT(vp.id) as total_productos  
    FROM ventas v 
    LEFT JOIN cuenta_cliente cc ON v.cuenta_id = cc.id
    LEFT JOIN venta_productos vp ON vp.venta_id = v.id  -- ✅ venta_productos
    WHERE v.fecha BETWEEN :desde AND :hasta
    GROUP BY v.id, v.fecha, v.total, v.status, v.pago_cliente, cc.id, cc.nombre
    ORDER BY v.fecha DESC
    """, nativeQuery = true)
List<Object[]> ventasReporteOptimizado(
    @Param("desde") LocalDateTime desde, 
    @Param("hasta") LocalDateTime hasta
);

@Query(value = """
    SELECT 
        vp.id, p.id as producto_id, p.codigo, p.descripcion,
        vp.cantidad, vp.precio_unitario, vp.costo_total
    FROM venta_productos vp 
    JOIN productos p ON p.id = vp.producto_id
    WHERE vp.venta_id = :ventaId
    """, nativeQuery = true)
List<Object[]> productosDeVentaOptimizado(@Param("ventaId") Integer ventaId);

@Query(value = """
    SELECT 
        p.id as producto_id,
        p.codigo,
        p.descripcion as producto_descripcion,
        cp.id as lote_id,
        cp.fecha_compra,
        vpl.cantidad_consumida as cantidad,
        vpl.costo_unitario,
        vpl.costo_total
    FROM venta_producto_lote vpl
    JOIN venta_productos vp ON vp.id = vpl.venta_producto_id
    JOIN productos p ON p.id = vp.producto_id
    JOIN compra_productos cp ON cp.id = vpl.compra_producto_id
    WHERE vp.venta_id = :ventaId
    ORDER BY p.codigo, cp.fecha_compra
    """, nativeQuery = true)
List<Object[]> findCostosPorLotesDeVentaOptimizado(@Param("ventaId") Integer ventaId);



}
