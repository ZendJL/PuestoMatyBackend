package com.tienda.inventario.Repositories;

import org.springframework.data.repository.CrudRepository;
import com.tienda.inventario.entities.Producto;

public interface ProductoRepository extends CrudRepository<Producto,Long>{

}
