package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.MermaProducto;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.repositories.MermaProductoRepository;
import com.tienda.inventario.repositories.MermaRepository;
import com.tienda.inventario.services.MermaService;

@Service
public class MermaServiceImpl implements MermaService {

    private final MermaRepository mermaRepository;
    private final MermaProductoRepository mermaProductoRepository;

    public MermaServiceImpl(MermaRepository mermaRepository,
                            MermaProductoRepository mermaProductoRepository) {
        this.mermaRepository = mermaRepository;
        this.mermaProductoRepository = mermaProductoRepository;
    }

    @Override
    @Transactional
    public Merma guardar(Merma merma) {
        if (merma.getMermaProductos() != null) {
            for (MermaProducto mp : merma.getMermaProductos()) {
                mp.setMerma(merma);
            }
        }
        return mermaRepository.save(merma);
    }

    @Override
    @Transactional(readOnly = true)
    public Merma buscarPorId(Long id) {
        return mermaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Merma> listarTodas() {
        return mermaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Merma> listar() {
        return mermaRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        mermaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaProducto> mermasDeProducto(Producto producto) {
        return mermaProductoRepository.findByProducto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Merma> mermasPorTipoYRango(String tipoMerma,
                                           LocalDateTime desde,
                                           LocalDateTime hasta) {
        return mermaRepository.findByTipoMermaAndFechaSalidaBetweenOrderByFechaSalidaAsc(tipoMerma, desde, hasta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Merma> mermasEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return mermaRepository.findByFechaSalidaBetweenOrderByFechaSalidaAsc(desde, hasta);
    }
}
