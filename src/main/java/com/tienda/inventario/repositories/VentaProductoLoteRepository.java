package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProductoLote;

public interface VentaProductoLoteRepository extends JpaRepository<VentaProductoLote, Integer> {

    List<VentaProductoLote> findByVentaProducto_Venta(Venta venta);
}
