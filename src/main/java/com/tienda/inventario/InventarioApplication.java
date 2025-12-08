package com.tienda.inventario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.repositories.ProductoRepository;

@SpringBootApplication
public class InventarioApplication implements CommandLineRunner {

	@Autowired
	private ProductoRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(InventarioApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{	
		List<Producto> productos = (List<Producto>) repository.findAll();

		productos.stream().forEach(producto -> {
			System.out.println(producto);
		});
	}
}