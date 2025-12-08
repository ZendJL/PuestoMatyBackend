package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Producto;

public interface ProductoService {

    List<Producto> listarTodos();

    Producto guardar(Producto producto);

    Producto buscarPorId(Long id);

    void eliminar(Long id);

    List<Producto> productosConStock();

    List<Producto> productosVendidosEnRango(LocalDateTime desde, LocalDateTime hasta);

    List<Producto> productosCompradosEnRango(LocalDateTime desde, LocalDateTime hasta);
}
