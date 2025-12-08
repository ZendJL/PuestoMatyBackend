package com.tienda.inventario.services;

import java.util.Date;
import java.util.List;

import com.tienda.inventario.entities.Producto;

public interface ProductoService {

    List<Producto> listarTodos();

    Producto guardar(Producto producto);

    Producto buscarPorId(Long id);

    void eliminar(Long id);

    List<Producto> productosConStock();

    List<Producto> productosVendidosEnRango(Date desde, Date hasta);

    List<Producto> productosCompradosEnRango(Date desde, Date hasta);
}
