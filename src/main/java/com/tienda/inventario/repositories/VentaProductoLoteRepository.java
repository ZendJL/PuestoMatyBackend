package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProductoLote;

public interface VentaProductoLoteRepository extends JpaRepository<VentaProductoLote, Integer> {

    List<VentaProductoLote> findByVentaProducto_Venta(Venta venta);

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
    FROM venta_producto_lotes vpl
    JOIN venta_productos vp ON vp.id = vpl.venta_producto_id
    JOIN productos p ON p.id = vp.producto_id
    JOIN compra_productos cp ON cp.id = vpl.compra_producto_id
    WHERE vp.venta_id = :ventaId
    ORDER BY p.codigo, cp.fecha_compra
    """, nativeQuery = true)
List<Object[]> findCostosPorLotesDeVentaOptimizado(@Param("ventaId") Integer ventaId);

}
