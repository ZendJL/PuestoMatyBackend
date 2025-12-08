package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.entities.Producto;

public interface VentaProductoRepository extends JpaRepository<VentaProducto, Long> {

    List<VentaProducto> findByVenta(Venta venta);

    List<VentaProducto> findByProducto(Producto producto);
}
