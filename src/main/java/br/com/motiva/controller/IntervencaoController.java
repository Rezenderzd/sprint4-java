package br.com.motiva.controller;

import br.com.motiva.dto.IntervencaoDto;
import br.com.motiva.service.IntervencaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/intervencoes")
public class IntervencaoController {

    private final IntervencaoService service;

    public IntervencaoController(IntervencaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<IntervencaoDto.Response>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntervencaoDto.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/equipe/{nomeEquipe}")
    public ResponseEntity<List<IntervencaoDto.Response>> buscarPorEquipe(@PathVariable String nomeEquipe) {
        return ResponseEntity.ok(service.buscarPorEquipe(nomeEquipe));
    }

    @PostMapping
    public ResponseEntity<IntervencaoDto.Response> criar(@Valid @RequestBody IntervencaoDto.Request dados) {
        IntervencaoDto.Response criada = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/intervencoes/" + criada.id())).body(criada);
    }

    @PostMapping("/gerar")
    public ResponseEntity<IntervencaoDto.GeracaoResultado> gerar() {
        return ResponseEntity.ok(service.gerarIntervencoes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntervencaoDto.Response> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody IntervencaoDto.Request dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
