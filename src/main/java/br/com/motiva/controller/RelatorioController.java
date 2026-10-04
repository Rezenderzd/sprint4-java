package br.com.motiva.controller;

import br.com.motiva.dto.RelatorioDto;
import br.com.motiva.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RelatorioDto.Response> gerar() {
        RelatorioDto.Response gerado = service.gerar();
        return ResponseEntity.created(URI.create("/api/relatorios/" + gerado.id())).body(gerado);
    }

    @GetMapping
    public ResponseEntity<List<RelatorioDto.Response>> listarHistorico() {
        return ResponseEntity.ok(service.listarHistorico());
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RelatorioDto.Response>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioDto.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
