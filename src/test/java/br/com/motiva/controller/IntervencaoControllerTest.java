package br.com.motiva.controller;

import br.com.motiva.dto.IntervencaoDto;
import br.com.motiva.exception.GlobalExceptionHandler;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.service.IntervencaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class IntervencaoControllerTest {

    @Mock
    private IntervencaoService service;

    @InjectMocks
    private IntervencaoController controller;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void deveRetornar201ComLocationAoCriarIntervencao() throws Exception {
        when(service.criar(any())).thenReturn(new IntervencaoDto.Response(5L, 1L, 2L, "Br", 10, 15, "umido",
                "Equipe Delta", "ROCADA_MECANIZADA", 40.0, LocalDateTime.now()));

        mvc.perform(post("/api/intervencoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trechoId\":1,\"equipeId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/intervencoes/5"))
                .andExpect(jsonPath("$.tipoServico").value("ROCADA_MECANIZADA"));
    }

    @Test
    public void deveRetornar400QuandoEquipeIdAusente() throws Exception {
        mvc.perform(post("/api/intervencoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trechoId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("equipeId é obrigatório"));

        verifyNoInteractions(service);
    }

    @Test
    public void deveRetornar400QuandoTrechoIdNaoForPositivo() throws Exception {
        mvc.perform(post("/api/intervencoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trechoId\":0,\"equipeId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("trechoId deve ser maior que zero"));

        verifyNoInteractions(service);
    }

    @Test
    public void deveRetornar400QuandoEquipeIncompativelComOClima() throws Exception {
        when(service.criar(any())).thenThrow(new RegraNegocioException("Este serviço exige equipe de roçada 'mecanizada'"));

        mvc.perform(post("/api/intervencoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trechoId\":1,\"equipeId\":1}"))
                .andExpect(status().isBadRequest());
    }
}
