package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.tienda.inventario.dto.MermaCostoRequestDto;
import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.MermaProducto;
import com.tienda.inventario.entities.Producto;

public interface MermaService {

    Merma guardar(Merma merma);

    Merma crearMermaConProductos(Merma merma);

    List<Merma> listar();

    Merma buscarPorId(Integer id);

    void eliminar(Integer id);

    List<MermaProducto> mermasDeProducto(Producto producto);

    List<Merma> mermasPorTipoYRango(String tipoMerma,
                                    LocalDateTime desde,
                                    LocalDateTime hasta);

    List<Merma> mermasEntreFechas(LocalDateTime desde,
                                  LocalDateTime hasta);

    // NUEVO: costo estimado FIFO sin modificar BD
    float calcularCostoMermaProducto(Integer productoId, Integer cantidad);


public List<Float> calcularCostosMermaBatch(List<MermaCostoRequestDto> requests);
  List<Map<String, Object>> reporteMermasCompleto(LocalDateTime desde, LocalDateTime hasta);
//List<Merma> reporteMermas(LocalDateTime desde, LocalDateTime hasta, String tipo);
List<Map<String, Object>> reporteMermasPorTipoCompleto(LocalDateTime desde, LocalDateTime hasta, String tipo);

}
