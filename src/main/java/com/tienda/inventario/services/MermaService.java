package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.MermaProducto;
import com.tienda.inventario.entities.Producto;

public interface MermaService {

    Merma guardar(Merma merma);

    Merma buscarPorId(Integer id);

    List<Merma> listarTodas();
    
    List<Merma> listar();

    void eliminar(Integer id);

    public List<MermaProducto> mermasDeProducto(Producto producto);

    List<Merma> mermasPorTipoYRango(String tipoMerma, LocalDateTime desde, LocalDateTime hasta);

    List<Merma> mermasEntreFechas(LocalDateTime desde, LocalDateTime hasta);
}
