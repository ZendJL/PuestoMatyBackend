package com.tienda.inventario.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.repositories.ProductoRepository;
import com.tienda.inventario.services.ProductoService;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> productosConStock() {
        return productoRepository.findByCantidadGreaterThan(0L);
    }

    @Override
    public List<Producto> productosVendidosEnRango(Date desde, Date hasta) {
        return productoRepository.findByUltimaVentaBetween(desde, hasta);
    }

    @Override
    public List<Producto> productosCompradosEnRango(Date desde, Date hasta) {
        return productoRepository.findByUltimaCompraBetween(desde, hasta);
    }
}
