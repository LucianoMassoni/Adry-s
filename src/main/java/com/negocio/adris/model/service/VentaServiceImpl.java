package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.model.repositories.VentaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VentaServiceImpl implements VentaService{
    private final Validator validator;
    private final VentaRepository repo;
    private final DetalleVentaService detalleVentaService;

    @Inject
    public VentaServiceImpl(Validator validator, VentaRepository repo, DetalleVentaService detalleVentaService){
        this.validator = validator;
        this.repo = repo;
        this.detalleVentaService = detalleVentaService;
    }

    private void validarVenta(VentaDto dto){
        Set<ConstraintViolation<VentaDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    private Venta convertirDtoAVenta(VentaDto dto){
        return new Venta(
                0, // Id temporal (la DB le asigna un Id valido cuando lo guarda)
                dto.getFecha(),
                dto.getTotal()
        );
    }

    @Override
    public void CrearVenta(VentaDto dto) {
        validarVenta(dto);
        dto.getDetalleVentaDtos().forEach(detalleVentaService::validarDetalleVentaDto);

        Venta v = convertirDtoAVenta(dto);
        repo.save(v);
        dto.getDetalleVentaDtos().forEach(detalle -> detalleVentaService.crearDetalleVenta(detalle, v.getId()));
    }

    @Override
    public void modificarVenta(VentaDto dto, long id) throws VentaNotFoundException {
        validarVenta(dto);

        Venta v = repo.findById(id);

        v.setFecha(dto.getFecha());
        v.setTotal(dto.getTotal());

        repo.update(v);
    }

    @Override
    public void eliminarVenta(long id) throws VentaNotFoundException {
        // Traigo la venta, para que, en caso de que no exista una venta
        // con ese id tire la exception
        Venta v = repo.findById(id);

        repo.delete(id);
    }

    @Override
    public Venta obtenerVenta(long id) throws VentaNotFoundException {
        return repo.findById(id);
    }

    @Override
    public List<Venta> obtenerTodasLasVentas() throws VentaNotFoundException {
        return repo.findAll();
    }
}
