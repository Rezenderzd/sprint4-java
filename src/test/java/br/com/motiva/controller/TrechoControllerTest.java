package br.com.motiva.controller;

import br.com.motiva.dto.TrechoDto;
import br.com.motiva.exception.GlobalExceptionHandler;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.service.TrechoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TrechoControllerTest {

    @Mock
    private TrechoService service;

    @InjectMocks
    private TrechoController controller;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void deveRetornar200AoBuscarTrechoExistente() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(
                new TrechoDto.Response(1L, "Br", 10, 15, 20.0, "umido", false, NivelPrioridade.NORMAL));

        mvc.perform(get("/api/trechos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeTrecho").value("Br"));
    }

    @Test
    public void deveRetornar404QuandoTrechoNaoExiste() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Trecho não encontrado: 99"));

        mvc.perform(get("/api/trechos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Trecho não encontrado: 99"));
    }

    @Test
    public void deveRetornar201AoCriarTrecho() throws Exception {
        when(service.criar(any())).thenReturn(
                new TrechoDto.Response(1L, "Br", 10, 15, 20.0, "umido", false, NivelPrioridade.NORMAL));

        mvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeTrecho\":\"Br\",\"quilometroInicial\":10,\"quilometroFinal\":15,"
                                + "\"nivelVegetacaoEmCm\":20.0,\"tipoClima\":\"umido\",\"comSensor\":false}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/trechos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.prioridade").value("NORMAL"));
    }

    @Test
    public void deveRetornar400QuandoNomeVazio() throws Exception {
        mvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeTrecho\":\"\",\"quilometroInicial\":10,\"quilometroFinal\":15,"
                                + "\"nivelVegetacaoEmCm\":20.0,\"tipoClima\":\"umido\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornar404AoAtualizarInexistente() throws Exception {
        when(service.atualizar(eq(9L), any())).thenThrow(new RecursoNaoEncontradoException("Trecho não encontrado: 9"));

        mvc.perform(put("/api/trechos/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeTrecho\":\"Br\",\"quilometroInicial\":10,\"quilometroFinal\":15,"
                                + "\"nivelVegetacaoEmCm\":20.0,\"tipoClima\":\"umido\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar204AoDeletar() throws Exception {
        mvc.perform(delete("/api/trechos/1")).andExpect(status().isNoContent());
    }

    @Test
    public void deveRetornar404AoDeletarInexistente() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Trecho não encontrado: 7")).when(service).deletar(7L);

        mvc.perform(delete("/api/trechos/7")).andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar400QuandoOBancoRecusaPorIntegridade() throws Exception {
        doThrow(new DataIntegrityViolationException("ORA-02292: integrity constraint violated"))
                .when(service).deletar(5L);

        mvc.perform(delete("/api/trechos/5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(
                        "Operação não permitida: ela viola uma restrição de integridade dos dados"));
    }

    @Test
    public void deveRetornar500NoMesmoFormatoJsonQuandoOcorreErroInesperado() throws Exception {
        when(service.buscarPorId(3L)).thenThrow(new IllegalStateException("detalhe interno"));

        mvc.perform(get("/api/trechos/3"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensagem").value("Erro interno inesperado. Tente novamente mais tarde."));
    }

    @Test
    public void deveRetornar405NoMesmoFormatoJsonQuandoOMetodoHttpNaoEPermitido() throws Exception {
        mvc.perform(patch("/api/trechos/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
