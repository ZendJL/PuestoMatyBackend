package com.tienda.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.inventario.dto.VentaProductoResumenDto;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;

public interface VentaProductoRepository extends JpaRepository<VentaProducto, Integer> {

    List<VentaProducto> findByVenta(Venta venta);

    List<VentaProducto> findByProducto(Producto producto);
     
   @Query("""
    select new com.tienda.inventario.dto.VentaProductoResumenDto(
        p.id,
        p.codigo,
        p.descripcion,
        coalesce(sum(vp.cantidad), 0),
        coalesce(sum(vp.precioUnitario * vp.cantidad), 0.0),
        coalesce(sum(vp.costoTotal), 0.0)
    )
    from VentaProducto vp
    join vp.venta v
    join vp.producto p
    where v.fecha between :desde and :hasta
    group by p.id, p.codigo, p.descripcion
""")
List<VentaProductoResumenDto> resumenPorProducto(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
);

@Query(value = """
    SELECT 
        p.id, p.descripcion, 
        cp.id, cp.fecha_compra,
        vpl.cantidad_consumida, vpl.costo_unitario, vpl.costo_total
    FROM venta_producto_lote vpl
    JOIN venta_producto vp ON vp.id = vpl.venta_producto_id
    JOIN productos p ON p.id = vp.producto_id
    JOIN compra_producto cp ON cp.id = vpl.compra_producto_id
    WHERE vp.venta_id = :ventaId
    ORDER BY p.descripcion, cp.fecha_compra
    """, nativeQuery = true)
List<Object[]> findCostosPorLotesDeVenta(@Param("ventaId") Integer ventaId);
}
