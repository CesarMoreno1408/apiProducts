package com.usta.apiproductos.controller;

import com.usta.apiproductos.model.Producto;
import com.usta.apiproductos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor

public class ProductoController {

    private final ProductoService service;

    // LISTAR TODOS
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // CREAR PRODUCTO
    @PostMapping
    public Producto guardar(
            @RequestBody Producto producto,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam(required = false) String imagenBase64) {

        String imagenUrlFinal = imagenUrl != null ? imagenUrl : imagenBase64;
        return service.guardar(producto, imagenUrlFinal);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public Producto actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        return service.actualizar(id, producto);
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Producto eliminado correctamente";
    }

}
