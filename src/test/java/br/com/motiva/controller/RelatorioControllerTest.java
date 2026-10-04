package br.com.motiva.controller;

import br.com.motiva.dto.RelatorioDto;
import br.com.motiva.exception.GlobalExceptionHandler;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RelatorioControllerTest {

    @Mock
    private RelatorioService service;

    @InjectMocks
    private RelatorioController controller;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private RelatorioDto.Response relatorio(Long id) {
        return new RelatorioDto.Response(id, LocalDateTime.now(), 4, 4, 2, 2, 1, 1, 1, 1, List.of(), List.of());
    }

    @Test
    public void deveRetornar201ComLocationAoGerarRelatorio() throws Exception {
        when(service.gerar()).thenReturn(relatorio(8L));

        mvc.perform(post("/api/relatorios"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/relatorios/8"))
                .andExpect(jsonPath("$.totalTrechos").value(4));
    }

    @Test
    public void deveRotearPeriodoParaOEndpointCertoENaoParaOId() throws Exception {
        when(service.listarPorPeriodo(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(relatorio(1L)));

        mvc.perform(get("/api/relatorios/periodo").param("inicio", "2026-09-01").param("fim", "2026-09-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void deveRetornar400QuandoPeriodoInvertido() throws Exception {
        when(service.listarPorPeriodo(LocalDate.of(2026, 9, 30), LocalDate.of(2026, 9, 1)))
                .thenThrow(new RegraNegocioException("A data inicial não pode ser posterior à data final"));

        mvc.perform(get("/api/relatorios/periodo").param("inicio", "2026-09-30").param("fim", "2026-09-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornar400QuandoFaltaParametroDoPeriodo() throws Exception {
        mvc.perform(get("/api/relatorios/periodo").param("inicio", "2026-09-01"))
                .andExpect(status().isBadRequest());
    }
}
