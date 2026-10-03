package br.com.motiva.controller;

import br.com.motiva.dto.TrechoDto;
import br.com.motiva.service.TrechoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trechos")
public class TrechoController {

    private final TrechoService service;

    public TrechoController(TrechoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TrechoDto.Response>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrechoDto.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/vegetacao")
    public ResponseEntity<List<TrechoDto.Response>> buscarPorVegetacao(@RequestParam Double minimo) {
        return ResponseEntity.ok(service.buscarPorVegetacaoMinima(minimo));
    }

    @GetMapping("/clima/{clima}")
    public ResponseEntity<List<TrechoDto.Response>> buscarPorClima(@PathVariable String clima) {
        return ResponseEntity.ok(service.buscarPorClima(clima));
    }

    @PostMapping
    public ResponseEntity<TrechoDto.Response> criar(@Valid @RequestBody TrechoDto.Request dados) {
        TrechoDto.Response criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/trechos/" + criado.id())).body(criado);
    }

    @PostMapping("/simular-crescimento")
    public ResponseEntity<List<TrechoDto.Response>> simularCrescimento() {
        return ResponseEntity.ok(service.simularCrescimento());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrechoDto.Response> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody TrechoDto.Request dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
