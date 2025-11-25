package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.PagoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PagoServiceImpl implements PagoService {
    private final PagoRepository repo;
    private final Validator validator;
    private final GastoService gastoService;

    @Inject
    public PagoServiceImpl(PagoRepository repo, Validator validator, GastoService gastoService){
        this.repo = repo;
        this.validator = validator;
        this.gastoService = gastoService;
    }

    public void validarDto(PagoDto dto){
        Set<ConstraintViolation<PagoDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    @Override
    public void guardar(PagoDto dto) throws GastoNotFoundException, ProveedorNotFoundException {
        validarDto(dto);

        Gasto gasto = gastoService.getGastoById(dto.getGastoId());

        Pago p = new Pago(
                0L, // id temporal
                gasto,
                LocalDateTime.now(),
                dto.getMontoPagado()
        );

        repo.save(p);
        gastoService.agregarPago(gasto, p);
    }

    @Override
    public void modificar(long id, PagoDto dto) throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException {
        Pago p = repo.findById(id);
        Gasto gasto = gastoService.getGastoById(dto.getGastoId());

        p.setGasto(gasto);
        p.setMontoPagado(dto.getMontoPagado());

        repo.update(p);
        gastoService.agregarPago(gasto, p);
    }

    @Override
    public void eliminar(long id) throws PagoNotFoundException {
        Pago p = repo.findById(id);
        repo.delete(id);
    }

    @Override
    public Pago getPago(long id) throws PagoNotFoundException {
        return repo.findById(id);
    }

    @Override
    public List<Pago> getPagos() {
        return repo.findAll();
    }
}
