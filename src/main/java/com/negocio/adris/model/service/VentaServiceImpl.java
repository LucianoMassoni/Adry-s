package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.model.repositories.VentaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VentaServiceImpl implements VentaService{
    private final Validator validator;
    private final VentaRepository repo;
    private final ProductoService productoService;

    @Inject
    public VentaServiceImpl(Validator validator, VentaRepository repo, ProductoService productoService){
        this.validator = validator;
        this.repo = repo;
        this.productoService = productoService;
    }

    private void validarDetalleVentaDto(DetalleVentaDto dto){
        Set<ConstraintViolation<DetalleVentaDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    private BigDecimal getSubTotal(DetalleVentaDto dto){
        if (!dto.getProducto().esDivisible()){
            return dto.getCantidad().
                    multiply(dto.getProducto().getPrecio()).
                    multiply(BigDecimal.ONE.subtract(dto.getDescuento().divide(BigDecimal.valueOf(100)))
                    );
        }

        return dto.getCantidad().multiply(dto.getPrecio()).multiply(BigDecimal.ONE.subtract(dto.getDescuento().divide(BigDecimal.valueOf(100))));
    }
    public DetalleVenta convertirDtoADetalleVenta(DetalleVentaDto dto){
        BigDecimal subTotal = getSubTotal(dto);

        DetalleVenta detalleVenta = new DetalleVenta(
                0, // id temporal, la DB le asigna un ID válido cuando se guarda
                dto.getProducto(),
                dto.getCantidad(),
                dto.getProducto().getPrecio(),
                dto.getDescuento(),
                subTotal
        );

        return detalleVenta;
    }

    void validarVenta(VentaDto dto){
        Set<ConstraintViolation<VentaDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }
    }

    BigDecimal calcularTotal(List<DetalleVentaDto> detalleVentaDtoList){
        return detalleVentaDtoList.stream().
                map(this::getSubTotal).
                reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Venta convertirDtoAVenta(VentaDto dto){
        BigDecimal total = calcularTotal(dto.getDetalleVentaDtos());

        if (dto.getFormaDePago().equals(FormaDePago.EFECTIVO) && total.compareTo(BigDecimal.valueOf(20000)) > 0){
            total.multiply(BigDecimal.valueOf(0.9));
        }

        List<DetalleVenta> detalleVentaList = new ArrayList<>();
        dto.getDetalleVentaDtos().forEach(detalleVentaDto -> detalleVentaList.add(convertirDtoADetalleVenta(detalleVentaDto)));

        return new Venta(
                0, // Id temporal (la DB le asigna un Id valido cuando lo guarda)
                dto.getFormaDePago(),
                dto.getFecha(),
                total,
                detalleVentaList
        );
    }

    @Override
    public void crearVenta(VentaDto dto) {
        validarVenta(dto);
        dto.getDetalleVentaDtos().forEach(this::validarDetalleVentaDto);

        Venta v = convertirDtoAVenta(dto);
        v.getDetalleVentas().forEach(detalleVenta -> productoService.comprarProducto(detalleVenta.getProducto(), detalleVenta.getCantidad()));
        repo.save(v);
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

    @Override
    public List<Venta> obtenerVentasPorDia(LocalDateTime fecha) throws VentaNotFoundException {
        return repo.getAllVentasByFecha(fecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public BigDecimal obtenerGananciaPorDia(LocalDateTime fecha) throws VentaNotFoundException {
        List<Venta> lista = repo.getAllVentasByFecha(fecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
        BigDecimal ganancia = lista.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2);

        return ganancia;
    }

    @Override
    public List<Venta> obtenerVentasPorMes(LocalDateTime fecha) throws VentaNotFoundException {
        String f = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
        f = f.split("\\d*-")[0];

        return repo.getAllVentasByFecha(f);
    }

    @Override
    public BigDecimal obtenerGananciaPorMes(LocalDateTime fecha) throws VentaNotFoundException {
        String f = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
        f = f.split("\\d*-")[0];

        List<Venta> lista = repo.getAllVentasByFecha(f);
        BigDecimal ganancia = lista.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2);

        return ganancia;
    }

    @Override
    public Map<LocalDate, BigDecimal> getFacturacionMes(YearMonth yearMonth) {
        if (yearMonth.isAfter(YearMonth.now())) throw new IllegalArgumentException("No se puede obtener la facturación de un mes futuro");
        return repo.getFacturacionMes(yearMonth);
    }
}
