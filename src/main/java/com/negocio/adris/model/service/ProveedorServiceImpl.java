package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.dtos.ProveedorDto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.ProductoRepository;
import com.negocio.adris.model.repositories.ProveedorRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProveedorServiceImpl implements ProveedorService {
    private final Validator validator;
    private final ProveedorRepository repo;

    @Inject
    public ProveedorServiceImpl(ProveedorRepository repo, Validator validator){
        this.repo = repo;
        this.validator = validator;
    }

    private void validar(ProveedorDto dto){
        Set<ConstraintViolation<ProveedorDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    @Override
    public void guardar(ProveedorDto dto) {
        validar(dto);

        Proveedor p = new Proveedor(
                0L, // id temporal
                dto.getNombre(),
                dto.getTelefono()
        );

        repo.save(p);
    }

    @Override
    public void modificar(long id, ProveedorDto dto) throws ProveedorNotFoundException {
        validar(dto);
        Proveedor p = repo.findById(id);

        p.setNombre(dto.getNombre());
        p.setTelefono(dto.getTelefono());

        repo.update(p);
    }

    @Override
    public void eliminar(long id) throws ProveedorNotFoundException {
        Proveedor p = repo.findById(id);

        repo.delete(id);
    }

    @Override
    public Proveedor getProveedor(long id) throws ProveedorNotFoundException {
        return repo.findById(id);
    }

    @Override
    public List<Proveedor> getProveedores() {
        return repo.findAll();
    }
}
