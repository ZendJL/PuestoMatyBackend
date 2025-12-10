package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Producto;

public interface ProductoService {

    List<Producto> listarTodos();

    Producto guardar(Producto producto);

    Producto buscarPorId(Integer id);

    void eliminar(Integer id);

    List<Producto> findByActivoTrue();

    List<Producto> productosConStock();

    List<Producto> productosCompradosEnRango(LocalDateTime desde, LocalDateTime hasta);
}
