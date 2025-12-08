package com.tienda.inventario.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.Producto;

public interface MermaRepository extends JpaRepository<Merma, Long> {

    // Mermas de un producto
    List<Merma> findByProducto(Producto producto);

    // Mermas por tipo (EXPIRADO, USO_PERSONAL, MAL_ESTADO...)
    List<Merma> findByTipoMerma(String tipoMerma);

    // Mermas por tipo y rango de fechas (para reportes diario/semanal/mensual)
    List<Merma> findByTipoMermaAndFechaSalidaBetweenOrderByFechaSalidaAsc(
            String tipoMerma, Date desde, Date hasta);

    // Todas las mermas por rango de fechas
    List<Merma> findByFechaSalidaBetweenOrderByFechaSalidaAsc(
            Date desde, Date hasta);
}
