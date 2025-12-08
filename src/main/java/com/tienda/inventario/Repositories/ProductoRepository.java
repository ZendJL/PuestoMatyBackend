package com.tienda.inventario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.tienda.inventario.entities.Producto;

public interface ProductoRepository extends CrudRepository<Producto,Long>{

    List<Producto> findByDescripcion(String descripcion);

    List<Producto> findByCodigo(String codigo);

    List<Producto> findByProveedor(String proveedor);

    @Query("select p from productos where p.codigo like '%?1%'")
    List<Producto> buscarPorCodigo(String codigo);


    @Query("select p from productos where p.descripcion like '%?1%'")
    List<Producto> buscarPorDescripcion(String descripcion);

}
