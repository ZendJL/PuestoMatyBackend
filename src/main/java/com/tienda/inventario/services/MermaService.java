package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.Producto;

public interface MermaService {

    Merma guardar(Merma merma);

    Merma buscarPorId(Long id);

    List<Merma> listarTodas();

    void eliminar(Long id);

    List<Merma> mermasDeProducto(Producto producto);

    List<Merma> mermasPorTipoYRango(String tipoMerma, LocalDateTime desde, LocalDateTime hasta);

    List<Merma> mermasEntreFechas(LocalDateTime desde, LocalDateTime hasta);
}
