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
}
