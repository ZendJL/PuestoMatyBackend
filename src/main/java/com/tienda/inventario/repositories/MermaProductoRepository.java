package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.MermaProducto;
import com.tienda.inventario.entities.Producto;


public interface MermaProductoRepository extends JpaRepository<MermaProducto, Integer> {
    List<MermaProducto> findByProducto(Producto producto);
}
