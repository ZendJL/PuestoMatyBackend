package com.tienda.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tienda.inventario.entities.Merma;

@Repository
public interface MermaRepository extends JpaRepository<Merma, Integer> {
    
    // ✅ EXISTENTES
    List<Merma> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
    List<Merma> findByTipoMermaAndFechaBetween(String tipoMerma, LocalDateTime desde, LocalDateTime hasta);
    
    // ✅ NUEVA: Reporte completo con productos (1 query)
@Query(value = """
    SELECT 
        m.id, m.tipo_merma, m.descripcion, m.fecha,
        mp.id as mp_id, p.id as producto_id, p.codigo, p.descripcion as producto_desc,
        mp.cantidad, mp.costo_unitario, mp.costo_total
    FROM merma m
    LEFT JOIN merma_producto mp ON mp.merma_id = m.id
    LEFT JOIN productos p ON p.id = mp.producto_id
    WHERE m.fecha BETWEEN :desde AND :hasta
    ORDER BY m.fecha DESC, mp.id
    """, nativeQuery = true)
List<Object[]> reporteMermasCompleto(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    
    // ✅ Costo estimado batch
    @Query(value = """
        SELECT 
            cp.id as lote_id, cp.fecha_compra, 
            vpl.costo_unitario, vpl.cantidad_consumida as cantidad_disponible
        FROM compra_productos cp
        JOIN venta_producto_lote vpl ON vpl.compra_producto_id = cp.id
        WHERE cp.producto_id = :productoId 
        AND vpl.cantidad_consumida > 0
        ORDER BY cp.fecha_compra ASC
        LIMIT :cantidad
        """, nativeQuery = true)
    List<Object[]> lotesParaCostoMerma(@Param("productoId") Integer productoId, @Param("cantidad") Integer cantidad);

@Query("SELECT DISTINCT m FROM Merma m " +  // ✅ DISTINCT evita duplicados
       "LEFT JOIN FETCH m.mermaProductos mp " +
       "LEFT JOIN FETCH mp.producto " +
       "WHERE m.fecha BETWEEN :desde AND :hasta")
List<Merma> findByFechaBetweenConProductos(@Param("desde") LocalDateTime desde, 
                                           @Param("hasta") LocalDateTime hasta);


@Query("SELECT m FROM Merma m LEFT JOIN FETCH m.mermaProductos mp LEFT JOIN FETCH mp.producto WHERE m.tipoMerma = :tipo AND m.fecha BETWEEN :desde AND :hasta")
List<Merma> findByTipoMermaAndFechaBetweenConProductos(@Param("tipo") String tipo, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

@Query("SELECT DISTINCT m FROM Merma m " +
       "LEFT JOIN FETCH m.mermaProductos mp " +
       "LEFT JOIN FETCH mp.producto " +
       "ORDER BY m.fecha DESC")
List<Merma> findAllConProductos();
}


