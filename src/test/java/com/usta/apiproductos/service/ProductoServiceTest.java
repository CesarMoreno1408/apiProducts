package com.usta.apiproductos.service;

import com.usta.apiproductos.integration.ImgBBService;
import com.usta.apiproductos.model.Producto;
import com.usta.apiproductos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Productos")
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private ImgBBService imgBBService;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

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
    }

    // ============== PRUEBAS LISTAR ==============
    @Test
    @DisplayName("Debe listar todos los productos exitosamente")
    void testListarTodos() {
        // Arrange
        Producto producto2 = Producto.builder()
                .id(2L)
                .nombre("Mouse")
                .descripcion("Mouse inalámbrico")
                .tipo("Accesorios")
                .precio(25.0)
                .activo(true)
                .build();

        List<Producto> productos = Arrays.asList(producto, producto2);
        when(repository.findAll()).thenReturn(productos);

        // Act
        List<Producto> resultado = service.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        assertEquals("Mouse", resultado.get(1).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay productos")
    void testListarVacio() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Producto> resultado = service.listar();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).findAll();
    }

    // ============== PRUEBAS BUSCAR POR ID ==============
    @Test
    @DisplayName("Debe buscar un producto por ID exitosamente")
    void testBuscarPorIdExitoso() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Producto resultado = service.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(1500.0, resultado.getPrecio());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe")
    void testBuscarPorIdNoEncontrado() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> service.buscarPorId(999L)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(999L);
    }

    // ============== PRUEBAS GUARDAR ==============
    @Test
    @DisplayName("Debe guardar un producto sin imagen exitosamente")
    void testGuardarSinImagen() {
        // Arrange
        Producto nuevoProducto = Producto.builder()
                .nombre("Teclado")
                .descripcion("Teclado mecánico")
                .tipo("Accesorios")
                .precio(85.0)
                .activo(true)
                .build();

        when(repository.save(nuevoProducto)).thenReturn(producto);

        // Act
        Producto resultado = service.guardar(nuevoProducto, null);

        // Assert
        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());
        verify(imgBBService, never()).subirImagen(anyString());
        verify(repository, times(1)).save(nuevoProducto);
    }

    @Test
    @DisplayName("Debe guardar un producto con imagen exitosamente")
    void testGuardarConImagen() {
        // Arrange
        String imagenBase64 = "data:image/jpeg;base64,/9j/4AAQSkZ...";
        String urlImagen = "https://imgbb.com/imagen123.jpg";

        Producto productoSinImagen = Producto.builder()
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .tipo("Electrónica")
                .precio(350.0)
                .activo(true)
                .build();

        when(imgBBService.subirImagen(imagenBase64)).thenReturn(urlImagen);
        when(repository.save(any(Producto.class))).thenReturn(producto);

        // Act
        Producto resultado = service.guardar(productoSinImagen, imagenBase64);

        // Assert
        assertNotNull(resultado);
        verify(imgBBService, times(1)).subirImagen(imagenBase64);
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe guardar un producto con validación de datos")
    void testGuardarConDatosCompletos() {
        // Arrange
        Producto productoCompleto = Producto.builder()
                .nombre("Impresora")
                .descripcion("Impresora multifunción")
                .tipo("Oficina")
                .precio(200.0)
                .imagenUrl("https://ejemplo.com/impresora.jpg")
                .activo(true)
                .build();

        when(repository.save(productoCompleto)).thenReturn(productoCompleto);

        // Act
        Producto resultado = service.guardar(productoCompleto, null);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getNombre());
        assertNotNull(resultado.getDescripcion());
        assertNotNull(resultado.getPrecio());
        assertTrue(resultado.getActivo());
        verify(repository, times(1)).save(productoCompleto);
    }

    // ============== PRUEBAS ACTUALIZAR ==============
    @Test
    @DisplayName("Debe actualizar un producto exitosamente")
    void testActualizarExitoso() {
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

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenReturn(productoActualizado);

        // Act
        Producto resultado = service.actualizar(1L, productoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Laptop Pro", resultado.getNombre());
        assertEquals(2000.0, resultado.getPrecio());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar producto no existente")
    void testActualizarNoEncontrado() {
        // Arrange
        Producto productoNuevo = Producto.builder()
                .nombre("Nuevo Producto")
                .precio(100.0)
                .build();

        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> service.actualizar(999L, productoNuevo)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar solo los campos válidos")
    void testActualizarCamposEspecificos() {
        // Arrange
        Producto actualizacion = Producto.builder()
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripción")
                .tipo("Tech")
                .precio(1800.0)
                .activo(false)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Producto resultado = service.actualizar(1L, actualizacion);

        // Assert
        assertEquals("Laptop Actualizada", resultado.getNombre());
        assertEquals("Nueva descripción", resultado.getDescripcion());
        assertEquals("Tech", resultado.getTipo());
        assertEquals(1800.0, resultado.getPrecio());
        assertEquals(false, resultado.getActivo());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Producto.class));
    }

    // ============== PRUEBAS ELIMINAR ==============
    @Test
    @DisplayName("Debe eliminar un producto exitosamente")
    void testEliminarExitoso() {
        // Act
        service.eliminar(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe poder eliminar un producto que existe")
    void testEliminarProductoExistente() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        service.eliminar(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe permitir eliminar sin validar si existe previamente")
    void testEliminarSinValidacion() {
        // Act
        service.eliminar(999L);

        // Assert
        verify(repository, times(1)).deleteById(999L);
    }
}
