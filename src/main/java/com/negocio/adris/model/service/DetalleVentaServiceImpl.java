package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.exceptions.DetalleVentaNotFoundException;
import com.negocio.adris.model.repositories.DetalleVentaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DetalleVentaServiceImpl implements DetalleVentaService{
    private final Validator validator;
    private final DetalleVentaRepository repo;

    @Inject
    public DetalleVentaServiceImpl(Validator validator, DetalleVentaRepository repo){
        this.validator = validator;
        this.repo = repo;
    }

    public void validarDetalleVentaDto(DetalleVentaDto dto){
        Set<ConstraintViolation<DetalleVentaDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    public DetalleVenta convertirDtoADetalleVenta(DetalleVentaDto dto, long ventaId){
        return new DetalleVenta(
                0, // id temporal, la DB le asigna un ID válido cuando se guarda
                ventaId,
                dto.getProducto().getId(),
                dto.getCantidad(),
                dto.getProducto().getPrecio(),
                dto.getDescuento(),
                dto.getSubtotal()
        );
    }

    @Override
    public void crearDetalleVenta(DetalleVentaDto dto, long ventaId) {
        validarDetalleVentaDto(dto);

        repo.save(convertirDtoADetalleVenta(dto, ventaId));
    }

    @Override
    public void modificarDetalleVenta(DetalleVentaDto dto, long id) throws DetalleVentaNotFoundException {
        validarDetalleVentaDto(dto);
        DetalleVenta detalleVenta = repo.findById(id);

        detalleVenta.setVentaId(detalleVenta.getVentaId()); // lo repito por las dudas de que me tire error después al hacer el update
        detalleVenta.setProductoId(dto.getProducto().getId());
        detalleVenta.setCantidad(dto.getCantidad());
        detalleVenta.setPrecioUnitario(dto.getProducto().getPrecio());
        detalleVenta.setDescuento(dto.getDescuento());
        detalleVenta.setSubtotal(dto.getSubtotal());

        repo.update(detalleVenta);
    }

    @Override
    public void eliminarDetalleVenta(long id) throws DetalleVentaNotFoundException {
        // Busca si existe, para que, en caso de que no exista tire la exception.
        DetalleVenta dv = repo.findById(id);

        repo.delete(id);
    }

    @Override
    public DetalleVenta obtenerDetalleVenta(long id) throws DetalleVentaNotFoundException {
        return repo.findById(id);
    }

    @Override
    public List<DetalleVenta> obtenerTodosLosDetalleVenta() throws DetalleVentaNotFoundException {
        return repo.findAll();
    }

    @Override
    public List<DetalleVenta> obtenerTodosLosDetalleVentaConIdVenta(long ventaId) throws DetalleVentaNotFoundException {
        return repo.findAllByVentaId(ventaId);
    }
}
