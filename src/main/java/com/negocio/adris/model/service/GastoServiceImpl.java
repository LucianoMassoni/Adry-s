package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.GastoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.GastoRepository;
import com.negocio.adris.utils.onCreate;
import com.negocio.adris.utils.onUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GastoServiceImpl implements GastoService {
    private final GastoRepository repo;
    private final Validator validator;
    private final ProveedorService proveedorService;

    @Inject
    public GastoServiceImpl(GastoRepository repo, Validator validator, ProveedorService proveedorService){
        this.repo = repo;
        this.validator = validator;
        this.proveedorService = proveedorService;
    }

    private void validar(GastoDto dto, Class clase){
        Set<ConstraintViolation<GastoDto>> violations = validator.validate(dto, clase);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    @Override
    public void crear(GastoDto dto) throws ProveedorNotFoundException {
        validar(dto, onCreate.class);

        LocalDateTime now = LocalDateTime.now();

        Proveedor proveedor = proveedorService.getProveedor(dto.getProveedorId());

        Gasto g = new Gasto(
                0L, // id temporal
                proveedor,
                now,
                dto.getFechaVencimiento(),
                dto.getMonto(),
                dto.getNota(),
                false
        );

        repo.save(g);
    }

    @Override
    public void modificar(long id, GastoDto dto) throws GastoNotFoundException, ProveedorNotFoundException {
        validar(dto, onUpdate.class);
        Gasto g = repo.findById(id);
        g.setProveedor(proveedorService.getProveedor(dto.getProveedorId()));
        g.setFechaVencimiento(dto.getFechaVencimiento());
        g.setMonto(dto.getMonto());
        g.setNota(dto.getNota());

        repo.update(g);
    }

    @Override
    public void eliminar(long id) throws GastoNotFoundException {
        Gasto g = repo.findById(id);

        repo.delete(id);
    }

    @Override
    public Gasto getGastoById(long id) throws GastoNotFoundException{
        return repo.findById(id);
    }

    @Override
    public List<Gasto> getGastos() {
        return repo.findAll();
    }

    @Override
    public void agregarPago(Gasto g, Pago p){
        g.getPagos().add(p);
        repo.update(g);
    }

    @Override
    public void saldarDeuda(Gasto g){
        g.setSaldado(true);
        repo.update(g);
    }
}
