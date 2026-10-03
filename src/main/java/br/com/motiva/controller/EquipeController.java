package br.com.motiva.controller;

import br.com.motiva.dto.EquipeDto;
import br.com.motiva.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    private final EquipeService service;

    public EquipeController(EquipeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EquipeDto.Response>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeDto.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/rocada/{tipo}")
    public ResponseEntity<List<EquipeDto.Response>> buscarPorRocada(@PathVariable String tipo) {
        return ResponseEntity.ok(service.buscarPorRocada(tipo));
    }

    @PostMapping
    public ResponseEntity<EquipeDto.Response> criar(@Valid @RequestBody EquipeDto.Request dados) {
        EquipeDto.Response criada = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/equipes/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipeDto.Response> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody EquipeDto.Request dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
