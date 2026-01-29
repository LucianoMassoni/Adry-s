package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.PagoRepository;
import com.negocio.adris.utils.Utils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Pago guardar(PagoDto dto) throws GastoNotFoundException, ProveedorNotFoundException {
        validarDto(dto);

        Gasto gasto = gastoService.getGastoById(dto.getGastoId());

        if (dto.getMontoPagado().compareTo(gasto.getMontoRestante()) > 0)
            throw new IllegalArgumentException("Error al pagar.\n\n Se trató de pagar $" + Utils.bigDecimalFormatter(dto.getMontoPagado()) +
                    " cuando se necesitaba solo $" + Utils.bigDecimalFormatter(gasto.getMontoRestante()) + " para saldar la deuda.");

        Pago p = new Pago(
                0L, // id temporal
                gasto,
                dto.getFechaDePago().atStartOfDay(),
                dto.getMontoPagado()
        );

        if (p.getMontoPagado().compareTo(gasto.getMontoRestante()) == 0){
            gastoService.saldarDeuda(gasto);
        }

        repo.save(p);
        return p;
    }

    @Override
    public Pago modificar(long id, PagoDto dto) throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException {
        Pago p = repo.findById(id);
        Gasto gasto = gastoService.getGastoById(dto.getGastoId());

        p.setGasto(gasto);
        p.setMontoPagado(dto.getMontoPagado());

        repo.update(p);
        return p;
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

    @Override
    public List<Pago> getAllPagosPorDia(LocalDateTime fecha) {
        return repo.getAllPagosPorFecha(fecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public BigDecimal getTotalPagoPorDia(LocalDateTime fecha) {
        List<Pago> lista = repo.getAllPagosPorFecha(fecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
        BigDecimal ganancia = lista.stream()
                .map(Pago::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return ganancia;
    }

    @Override
    public List<Pago> getAllPagosPorMes(LocalDateTime fecha) {
        String f = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
        f = f.split("\\d*-")[0];

        return repo.getAllPagosPorFecha(f);
    }

    @Override
    public BigDecimal getTotalPagoPorMes(LocalDateTime fecha) {
        String f = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
        f = f.split("\\d*-")[0];

        List<Pago> lista = repo.getAllPagosPorFecha(f);

        BigDecimal ganancia = lista.stream()
                .map(Pago::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return ganancia;
    }
}
