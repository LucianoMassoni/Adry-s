package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.AsientoDto;
import com.negocio.adris.model.dtos.CuentaDto;
import com.negocio.adris.model.entities.Cuenta;
import com.negocio.adris.model.exceptions.CuentaNotFoundException;
import com.negocio.adris.model.repositories.CuentaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CuentaServiceImpl implements CuentaService{
    private final Validator validator;
    private final CuentaRepository repo;

    @Inject
    public CuentaServiceImpl(Validator validator, CuentaRepository repo) {
        this.validator = validator;
        this.repo = repo;
    }

    private void validarCuentaDto(CuentaDto dto){
        Set<ConstraintViolation<CuentaDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    private Cuenta convertirDtoACuenta(CuentaDto dto){
        return new Cuenta(
                0, //id temporal
                dto.getTipo(),
                dto.getDetalle(),
                dto.getDebe(),
                dto.getHaber(),
                dto.getFecha()
        );
    }

    @Override
    public void validarAsiento(List<CuentaDto> cuentas){
        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        for (CuentaDto dto : cuentas){
            totalDebe = totalDebe.add(dto.getDebe());
            totalHaber = totalHaber.add(dto.getHaber());
        }

        if (totalDebe.compareTo(totalHaber) != 0){
            throw new IllegalArgumentException("El asiento no está balanceado: debe ≠ haber");
        }
    }

    @Override
    public void registrarAsiento(AsientoDto asientoDto){
        asientoDto.getCuentas().forEach(this::validarCuentaDto);
        validarAsiento(asientoDto.getCuentas());

        asientoDto.getCuentas()
                .stream()
                .map(this::convertirDtoACuenta)
                .forEach(repo::save);
    }

    @Override
    public void crearCuenta(CuentaDto dto) {
        validarCuentaDto(dto);

        Cuenta c = convertirDtoACuenta(dto);

        repo.save(c);
    }

    @Override
    public void modificarCuenta(CuentaDto dto, long id) throws CuentaNotFoundException {
        validarCuentaDto(dto);

        Cuenta c = repo.findById(id);
        c.setTipo(dto.getTipo());
        c.setDetalle(dto.getDetalle());
        c.setDebe(dto.getDebe());
        c.setHaber(dto.getHaber());
        c.setFecha(dto.getFecha());

        repo.update(c);
    }

    @Override
    public void eliminarCuenta(long id) throws CuentaNotFoundException {
        repo.findById(id);

        repo.delete(id);
    }

    @Override
    public Cuenta obtenerCuenta(long id) throws CuentaNotFoundException {
        return repo.findById(id);
    }

    @Override
    public List<Cuenta> obtenerTodasCuentas() throws CuentaNotFoundException {
        return repo.findAll();
    }
}
