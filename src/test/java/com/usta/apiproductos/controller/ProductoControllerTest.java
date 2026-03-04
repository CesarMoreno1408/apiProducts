package com.usta.apiproductos.controller;

import com.usta.apiproductos.model.Producto;
import com.usta.apiproductos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Controlador de Productos")
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    private Producto producto;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop de alta gama")
                .tipo("Electrónica")
                .precio(1500.0)
                .imagenUrl("https://ejemplo.com/imagen.jpg")
                .activo(true)
                .build();

        producto2 = Producto.builder()
                .id(2L)
                .nombre("Mouse")
                .descripcion("Mouse inalámbrico")
                .tipo("Accesorios")
                .precio(25.0)
                .imagenUrl("https://ejemplo.com/mouse.jpg")
                .activo(true)
                .build();
    }

    // ============== PRUEBAS LISTAR ==============
    @Test
    @DisplayName("Listar - Debe retornar lista de todos los productos")
    void testListarProductos() {
        // Arrange
        List<Producto> productos = Arrays.asList(producto, producto2);
        when(service.listar()).thenReturn(productos);

        // Act
        List<Producto> resultado = controller.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        assertEquals("Mouse", resultado.get(1).getNombre());
        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("Listar - Debe retornar lista vacía cuando no hay productos")
    void testListarProductosVacio() {
        // Arrange
        when(service.listar()).thenReturn(Arrays.asList());

        // Act
        List<Producto> resultado = controller.listar();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(service, times(1)).listar();
    }

    // ============== PRUEBAS OBTENER POR ID ==============
    @Test
    @DisplayName("Obtener por ID - Debe retornar un producto por ID")
    void testObtenerProductoPorId() {
        // Arrange
        when(service.buscarPorId(1L)).thenReturn(producto);

        // Act
        Producto resultado = controller.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        assertEquals("Laptop de alta gama", resultado.getDescripcion());
        assertEquals("Electrónica", resultado.getTipo());
        assertEquals(1500.0, resultado.getPrecio());
        assertTrue(resultado.getActivo());
        verify(service, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Obtener por ID - Debe lanzar excepción cuando el producto no existe")
    void testObtenerProductoPorIdNoEncontrado() {
        // Arrange
        when(service.buscarPorId(999L))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        // Act & Assert
        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> controller.obtenerPorId(999L)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(service, times(1)).buscarPorId(999L);
    }

    // ============== PRUEBAS CREAR ==============
    @Test
    @DisplayName("Guardar - Debe crear un nuevo producto sin imagen")
    void testCrearProducto() {
        // Arrange
        Producto nuevoProducto = Producto.builder()
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .tipo("Electrónica")
                .precio(350.0)
                .activo(true)
                .build();

        Producto productoCreado = Producto.builder()
                .id(3L)
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .tipo("Electrónica")
                .precio(350.0)
                .imagenUrl("https://ejemplo.com/monitor.jpg")
                .activo(true)
                .build();

        when(service.guardar(any(Producto.class), any())).thenReturn(productoCreado);

        // Act
        Producto resultado = controller.guardar(nuevoProducto, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Monitor", resultado.getNombre());
        assertEquals(350.0, resultado.getPrecio());
        verify(service, times(1)).guardar(any(Producto.class), any());
    }

    @Test
    @DisplayName("Guardar - Debe crear un producto con imagen")
    void testCrearProductoConImagen() {
        // Arrange
        Producto nuevoProducto = Producto.builder()
                .nombre("Teclado")
                .descripcion("Teclado mecánico")
                .tipo("Accesorios")
                .precio(85.0)
                .activo(true)
                .build();

        Producto productoConImagen = Producto.builder()
                .id(4L)
                .nombre("Teclado")
                .descripcion("Teclado mecánico")
                .tipo("Accesorios")
                .precio(85.0)
                .imagenUrl("https://imgbb.com/teclado123.jpg")
                .activo(true)
                .build();

        String imagenBase64 = "data:image/jpeg;base64,/9j/4AAQSkZ...";
        when(service.guardar(any(Producto.class), eq(imagenBase64)))
                .thenReturn(productoConImagen);

        // Act
        Producto resultado = controller.guardar(nuevoProducto, imagenBase64);

        // Assert
        assertNotNull(resultado);
        assertEquals(4L, resultado.getId());
        assertEquals("https://imgbb.com/teclado123.jpg", resultado.getImagenUrl());
        verify(service, times(1)).guardar(any(Producto.class), eq(imagenBase64));
    }

    // ============== PRUEBAS ACTUALIZAR ==============
    @Test
    @DisplayName("Actualizar - Debe actualizar un producto existente")
    void testActualizarProducto() {
        // Arrange
        Producto productoActualizado = Producto.builder()
                .id(1L)
                .nombre("Laptop Pro")
                .descripcion("Laptop de última generación")
                .tipo("Electrónica")
                .precio(2000.0)
                .imagenUrl("https://ejemplo.com/imagen.jpg")
                .activo(true)
                .build();

        when(service.actualizar(1L, productoActualizado))
                .thenReturn(productoActualizado);

        // Act
        Producto resultado = controller.actualizar(1L, productoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop Pro", resultado.getNombre());
        assertEquals(2000.0, resultado.getPrecio());
        verify(service, times(1)).actualizar(1L, productoActualizado);
    }

    @Test
    @DisplayName("Actualizar - Debe lanzar excepción si el producto no existe")
    void testActualizarProductoNoEncontrado() {
        // Arrange
        Producto productoNuevo = Producto.builder()
                .nombre("Nuevo")
                .precio(100.0)
                .build();

        when(service.actualizar(999L, productoNuevo))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        // Act & Assert
        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> controller.actualizar(999L, productoNuevo)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(service, times(1)).actualizar(999L, productoNuevo);
    }

    @Test
    @DisplayName("Actualizar - Debe actualizar solo campos específicos")
    void testActualizarProductoCamposEspecificos() {
        // Arrange
        Producto actualizacion = Producto.builder()
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripción")
                .tipo("Tech")
                .precio(1800.0)
                .activo(false)
                .build();

        Producto resultado = Producto.builder()
                .id(1L)
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripción")
                .tipo("Tech")
                .precio(1800.0)
                .imagenUrl("https://ejemplo.com/imagen.jpg")
                .activo(false)
                .build();

        when(service.actualizar(1L, actualizacion))
                .thenReturn(resultado);

        // Act
        Producto respuesta = controller.actualizar(1L, actualizacion);

        // Assert
        assertEquals("Laptop Actualizada", respuesta.getNombre());
        assertEquals(1800.0, respuesta.getPrecio());
        assertFalse(respuesta.getActivo());
        verify(service, times(1)).actualizar(1L, actualizacion);
    }

    // ============== PRUEBAS ELIMINAR ==============
    @Test
    @DisplayName("Eliminar - Debe eliminar un producto")
    void testEliminarProducto() {
        // Arrange
        doNothing().when(service).eliminar(1L);

        // Act
        String resultado = controller.eliminar(1L);

        // Assert
        assertEquals("Producto eliminado correctamente", resultado);
        verify(service, times(1)).eliminar(1L);
    }

    @Test
    @DisplayName("Eliminar - Debe permitir eliminar un producto que no existe")
    void testEliminarProductoNoExistente() {
        // Arrange
        doNothing().when(service).eliminar(999L);

        // Act
        String resultado = controller.eliminar(999L);

        // Assert
        assertEquals("Producto eliminado correctamente", resultado);
        verify(service, times(1)).eliminar(999L);
    }

    @Test
    @DisplayName("Eliminar - Debe retornar mensaje de éxito")
    void testEliminarProductoMensaje() {
        // Arrange
        doNothing().when(service).eliminar(anyLong());

        // Act
        String resultado = controller.eliminar(1L);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.contains("eliminado"));
        assertTrue(resultado.contains("correctamente"));
    }

    // ============== PRUEBAS DE FLUJO COMPLETO ==============
    @Test
    @DisplayName("Flujo CRUD: Crear, Leer, Actualizar, Eliminar")
    void testFlujoCRUD() {
        // 1. Crear
        Producto nuevo = Producto.builder()
                .nombre("Producto CRUD")
                .descripcion("Test CRUD")
                .tipo("Test")
                .precio(99.99)
                .activo(true)
                .build();

        Producto creado = Producto.builder()
                .id(100L)
                .nombre("Producto CRUD")
                .descripcion("Test CRUD")
                .tipo("Test")
                .precio(99.99)
                .activo(true)
                .build();

        when(service.guardar(any(Producto.class), any())).thenReturn(creado);

        Producto resultadoCrear = controller.guardar(nuevo, null);
        assertNotNull(resultadoCrear);
        assertEquals(100L, resultadoCrear.getId());

        // 2. Leer
        when(service.buscarPorId(100L)).thenReturn(creado);

        Producto resultadoLeer = controller.obtenerPorId(100L);
        assertNotNull(resultadoLeer);
        assertEquals("Producto CRUD", resultadoLeer.getNombre());

        // 3. Actualizar
        Producto actualizado = Producto.builder()
                .id(100L)
                .nombre("Producto CRUD Actualizado")
                .descripcion("Test CRUD Actualizado")
                .tipo("Test")
                .precio(149.99)
                .activo(true)
                .build();

        when(service.actualizar(eq(100L), any(Producto.class))).thenReturn(actualizado);

        Producto resultadoActualizar = controller.actualizar(100L, actualizado);
        assertEquals("Producto CRUD Actualizado", resultadoActualizar.getNombre());

        // 4. Eliminar
        doNothing().when(service).eliminar(100L);

        String resultadoEliminar = controller.eliminar(100L);
        assertTrue(resultadoEliminar.contains("eliminado"));

        // Verificar llamadas
        verify(service, times(1)).guardar(any(Producto.class), any());
        verify(service, times(1)).buscarPorId(100L);
        verify(service, times(1)).actualizar(eq(100L), any(Producto.class));
        verify(service, times(1)).eliminar(100L);
    }
}


