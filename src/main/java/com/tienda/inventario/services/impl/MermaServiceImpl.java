package com.tienda.inventario.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.repositories.MermaRepository;
import com.tienda.inventario.services.MermaService;

@Service
@Transactional
public class MermaServiceImpl implements MermaService {

    private final MermaRepository mermaRepository;

    public MermaServiceImpl(MermaRepository mermaRepository) {
        this.mermaRepository = mermaRepository;
    }

    @Override
    public Merma guardar(Merma merma) {
        return mermaRepository.save(merma);
    }

    @Override
    public Merma buscarPorId(Long id) {
        return mermaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Merma> listarTodas() {
        return mermaRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        mermaRepository.deleteById(id);
    }

    @Override
    public List<Merma> mermasDeProducto(Producto producto) {
        return mermaRepository.findByProducto(producto);
    }

    @Override
    public List<Merma> mermasPorTipoYRango(String tipoMerma, Date desde, Date hasta) {
        return mermaRepository
                .findByTipoMermaAndFechaSalidaBetweenOrderByFechaSalidaAsc(tipoMerma, desde, hasta);
    }

    @Override
    public List<Merma> mermasEntreFechas(Date desde, Date hasta) {
        return mermaRepository.findByFechaSalidaBetweenOrderByFechaSalidaAsc(desde, hasta);
    }
}
