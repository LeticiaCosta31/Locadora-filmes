package com.seuprojeto.locadora.service;

import com.seuprojeto.locadora.model.Cliente;
import com.seuprojeto.locadora.model.Estoque;
import com.seuprojeto.locadora.model.Filme;
import com.seuprojeto.locadora.model.Locacao;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceTest {

    private Locadora locadora;

    @Mock
    private Locadora locadoraMock;

    @Mock
    private Estoque estoqueMock;

    @Mock
    private Notificacao notificacaoMock;

    @InjectMocks
    private SistemaLocadora sistemaLocadora;

    @BeforeAll
    void setup() {
        locadora = new Locadora();
    }

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
        locadora.limparTabelas();
    }

    @AfterAll
    void tearDown() {
        locadora.close();
    }

    // --- TESTES FILME ---

    @Test
    void testCadastrarEListarFilme() {
        Filme filme = new Filme(1, "Vingadores", "Ação", 142, 10, "Joss Whedon", 2012, "Ação", "2012-04-25");
        locadora.cadastrarFilme(filme);

        List<Filme> filmes = locadora.listarFilmes();
        assertEquals(1, filmes.size());

        Filme f = filmes.get(0);
        assertEquals("Vingadores", f.getTitulo());
        assertEquals("Joss Whedon", f.getDiretor());
        assertEquals(10, f.getQuantidadeEstoque());
    }

    @Test
    void testBuscarFilmePorIdExistente() {
        Filme filme = new Filme(2, "Homem de Ferro", "Ação", 126, 5, "Jon Favreau", 2008, "Ação", "2008-05-02");
        locadora.cadastrarFilme(filme);

        Filme encontrado = locadora.buscarFilmePorId(2);
        assertNotNull(encontrado);
        assertEquals("Homem de Ferro", encontrado.getTitulo());
    }

    @Test
    void testBuscarFilmePorIdInexistente() {
        Filme encontrado = locadora.buscarFilmePorId(999);
        assertNull(encontrado);
    }

    @Test
    void testAtualizarFilme() {
        Filme filme = new Filme(3, "Thor", "Ação", 115, 5, "Kenneth Branagh", 2011, "Ação", "2011-04-21");
        locadora.cadastrarFilme(filme);

        Filme atualizado = new Filme(3, "Thor: Ragnarok", "Ação/Comédia", 130, 10, "Taika Waititi", 2017,
                "Ação/Comédia", "2017-10-25");
        locadora.atualizarFilme(atualizado);

        Filme filmeAtualizado = locadora.buscarFilmePorId(3);
        assertNotNull(filmeAtualizado);
        assertEquals("Thor: Ragnarok", filmeAtualizado.getTitulo());
        assertEquals("Taika Waititi", filmeAtualizado.getDiretor());
        assertEquals(2017, filmeAtualizado.getAnoLancamento());
        assertEquals("Ação/Comédia", filmeAtualizado.getGenero());
        assertEquals("2017-10-25", filmeAtualizado.getDataEstreia());
        assertEquals("Ação/Comédia", filmeAtualizado.getCategoria());
        assertEquals(130, filmeAtualizado.getDuracao());
        assertEquals(10, filmeAtualizado.getQuantidadeEstoque());
    }

    @Test
    void testDeletarFilme() {
        Filme filme = new Filme(4, "Capitão América", "Ação", 124, 4, "Joe Johnston", 2011, "Ação", "2011-07-22");
        locadora.cadastrarFilme(filme);

        locadora.deletarFilme(4);

        Filme buscado = locadora.buscarFilmePorId(4);
        assertNull(buscado);
    }

    // --- TESTES CLIENTE ---

    @Test
    void testCadastrarEBuscarCliente() {
        Cliente cliente = new Cliente(1, "João Silva", "123456789", "Rua A, 123");
        locadora.cadastrarCliente(cliente);

        Cliente encontrado = locadora.buscarClientePorId(1);
        assertNotNull(encontrado);
        assertEquals("João Silva", encontrado.getNome());
        assertEquals("123456789", encontrado.getTelefone());
    }

    @Test
    void testListarClientes() {
        locadora.cadastrarCliente(new Cliente(2, "Maria", "987654321", "Rua B, 456"));
        locadora.cadastrarCliente(new Cliente(3, "Pedro", "555555555", "Rua C, 789"));

        List<Cliente> clientes = locadora.listarClientes();
        assertEquals(2, clientes.size());
    }

    // --- TESTES LOCAÇÃO ---

    @Test
    void testRealizarLocacaoERegistrarDevolucao() {
        Filme filme = new Filme(5, "Inception", "Suspense", 148, 3, "Nolan", 2010, "Suspense", "2010-07-16");
        locadora.cadastrarFilme(filme);
        Cliente cliente = new Cliente(4, "Ana", "111222333", "Rua D, 101");
        locadora.cadastrarCliente(cliente);

        LocalDate dataPrevista = LocalDate.now().plusDays(7);
        Locacao locacao = locadora.realizarLocacao(cliente.getId(), filme.getId(), dataPrevista);

        assertNotNull(locacao);
        assertFalse(locacao.isDevolvido());
        assertEquals(cliente.getId(), locacao.getCliente().getId());
        assertEquals(filme.getId(), locacao.getFilme().getId());

        // Verifica se estoque diminuiu
        Filme filmeAtualizado = locadora.buscarFilmePorId(filme.getId());
        assertEquals(2, filmeAtualizado.getQuantidadeEstoque());

        // Registra devolução
        locadora.registrarDevolucao(locacao.getId());

        // Busca a locação atualizada do banco para verificar status devolvido
        List<Locacao> locacoes = locadora.listarLocacoes();
        Locacao locacaoAtualizada = locacoes.stream()
                .filter(l -> l.getId() == locacao.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Locação não encontrada após devolução"));

        assertTrue(locacaoAtualizada.isDevolvido());

        // Estoque deve voltar ao normal
        filmeAtualizado = locadora.buscarFilmePorId(filme.getId());
        assertEquals(3, filmeAtualizado.getQuantidadeEstoque());
    }

    @Test
    void testListarLocacoes() {
        Filme filme = new Filme(6, "Matrix Reloaded", "Ação", 138, 6, "Wachowski", 2003, "Ação", "2003-05-15");
        locadora.cadastrarFilme(filme);
        Cliente cliente = new Cliente(5, "Lucas", "777888999", "Rua E, 202");
        locadora.cadastrarCliente(cliente);

        LocalDate dataPrevista = LocalDate.now().plusDays(5);
        locadora.realizarLocacao(cliente.getId(), filme.getId(), dataPrevista);

        List<Locacao> locacoes = locadora.listarLocacoes();
        assertEquals(1, locacoes.size());
    }

    @Test
    void testRealizarLocacaoFilmeSemEstoque() {
        Filme filme = new Filme(7, "Filme Sem Estoque", "Drama", 120, 0, "Diretor", 2024, "Drama", "2024-01-01");
        locadora.cadastrarFilme(filme);
        Cliente cliente = new Cliente(6, "Cliente Teste", "000000000", "Rua Teste");
        locadora.cadastrarCliente(cliente);

        LocalDate dataPrevista = LocalDate.now().plusDays(3);

        Exception ex = assertThrows(IllegalStateException.class, () -> {
            locadora.realizarLocacao(cliente.getId(), filme.getId(), dataPrevista);
        });
        assertEquals("Filme sem estoque disponível", ex.getMessage());
    }

    @Test
    void testRealizarLocacaoClienteInexistente() {
        Filme filme = new Filme(8, "Filme Existente", "Ação", 130, 5, "Diretor", 2023, "Ação", "2023-06-01");
        locadora.cadastrarFilme(filme);

        LocalDate dataPrevista = LocalDate.now().plusDays(3);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            locadora.realizarLocacao(9999, filme.getId(), dataPrevista);
        });
        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void testRealizarLocacaoFilmeInexistente() {
        Cliente cliente = new Cliente(7, "Cliente Existente", "999999999", "Rua Existente");
        locadora.cadastrarCliente(cliente);

        LocalDate dataPrevista = LocalDate.now().plusDays(3);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            locadora.realizarLocacao(cliente.getId(), 9999, dataPrevista);
        });
        assertEquals("Filme não encontrado", ex.getMessage());
    }

    @Test
    void testRegistrarDevolucaoLocacaoInexistente() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            locadora.registrarDevolucao(9999);
        });
        assertTrue(ex.getMessage().contains("Locação não encontrada"));
    }

    @Test
    void testCadastrarClienteSemEndereco() {
        Cliente cliente = new Cliente(8, "Cliente Sem Endereco", "111222333", null);
        locadora.cadastrarCliente(cliente);

        Cliente encontrado = locadora.buscarClientePorId(cliente.getId());
        assertNotNull(encontrado);
        assertNull(encontrado.getEndereco());
    }

     // --- TESTES NOTIFICAÇÃO ---

    @Test
    void testEnviarNotificacaoAtraso() {
        Notificacao notificacao = new Notificacao();
        Cliente cliente = new Cliente(1, "João", "1234", "Rua A");
        assertDoesNotThrow(() -> notificacao.enviarNotificacaoAtraso(cliente));
    }

    @Test
    void testEnviarNotificacaoPromocao() {
        Notificacao notificacao = new Notificacao();
        Cliente cliente = new Cliente(2, "Maria", "5678", "Rua B");
        assertDoesNotThrow(() -> notificacao.enviarNotificacaoPromocao(cliente, "Promoção 50% OFF"));
    }

    @Test
    void testEnviarAvisoFuncionario() {
        Notificacao notificacao = new Notificacao();
        assertDoesNotThrow(() -> notificacao.enviarAvisoFuncionario("Aviso importante"));
    }

    // --- TESTES RELATORIOLOCACOES ---

    @Test
    void testGerarRelatorioPorPeriodo() {
        Cliente cliente = new Cliente(1, "Ana", "111", "Rua X");
        Filme filme = new Filme(1, "Filme1", "Cat", 90, 5, "Dir", 2020, "Gen", "2020-01-01");
        Locacao loc1 = new Locacao(1, cliente, filme, LocalDate.of(2023,1,10), LocalDate.of(2023,1,20));
        Locacao loc2 = new Locacao(2, cliente, filme, LocalDate.of(2023,2,5), LocalDate.of(2023,2,15));
        Locacao loc3 = new Locacao(3, cliente, filme, LocalDate.of(2023,3,1), LocalDate.of(2023,3,10));

        List<Locacao> resultado = RelatorioLocacoes.gerarRelatorioPorPeriodo(List.of(loc1, loc2, loc3),
                LocalDate.of(2023,1,1), LocalDate.of(2023,2,28));

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(loc1));
        assertTrue(resultado.contains(loc2));
        assertFalse(resultado.contains(loc3));
    }

    @Test
    void testFilmesMaisAlugados() {
        Cliente cliente = new Cliente(1, "Ana", "111", "Rua X");
        Filme filme1 = new Filme(1, "Filme1", "Cat", 90, 5, "Dir", 2020, "Gen", "2020-01-01");
        Filme filme2 = new Filme(2, "Filme2", "Cat", 100, 3, "Dir", 2021, "Gen", "2021-01-01");
        Filme filme3 = new Filme(3, "Filme3", "Cat", 120, 2, "Dir", 2019, "Gen", "2019-01-01");

        List<Locacao> locacoes = List.of(
            new Locacao(1, cliente, filme1, LocalDate.now(), LocalDate.now().plusDays(5)),
            new Locacao(2, cliente, filme1, LocalDate.now(), LocalDate.now().plusDays(5)),
            new Locacao(3, cliente, filme2, LocalDate.now(), LocalDate.now().plusDays(5)),
            new Locacao(4, cliente, filme3, LocalDate.now(), LocalDate.now().plusDays(5)),
            new Locacao(5, cliente, filme2, LocalDate.now(), LocalDate.now().plusDays(5)),
            new Locacao(6, cliente, filme1, LocalDate.now(), LocalDate.now().plusDays(5))
        );

        List<Map.Entry<Filme, Long>> top2 = RelatorioLocacoes.filmesMaisAlugados(locacoes, 2);

        assertEquals(2, top2.size());
        assertEquals(filme1, top2.get(0).getKey());
        assertEquals(3L, top2.get(0).getValue());
        assertEquals(filme2, top2.get(1).getKey());
        assertEquals(2L, top2.get(1).getValue());
    }

    // --- TESTES SISTEMALOCADORA (com mocks) ---

    @Test
    void testCadastrarFilme() {
        Filme filme = new Filme(1, "Teste Filme", "Cat", 90, 10, "Dir", 2022, "Gen", "2022-01-01");
        sistemaLocadora.cadastrarFilme(filme, 10);
        verify(locadoraMock).cadastrarFilme(filme);
        verify(estoqueMock).adicionarFilme(filme, 10);
    }

    @Test
    void testListarFilmesDisponiveis() {
        Filme f1 = new Filme(1, "F1", "Cat", 100, 5, "Dir", 2020, "Gen", "2020-01-01");
        Filme f2 = new Filme(2, "F2", "Cat", 90, 0, "Dir", 2021, "Gen", "2021-01-01");

        when(locadoraMock.listarFilmes()).thenReturn(List.of(f1, f2));
        when(estoqueMock.verificarDisponibilidade(f1)).thenReturn(true);
        when(estoqueMock.verificarDisponibilidade(f2)).thenReturn(false);

        List<Filme> disponiveis = sistemaLocadora.listarFilmesDisponiveis();

        assertEquals(1, disponiveis.size());
        assertTrue(disponiveis.contains(f1));
        assertFalse(disponiveis.contains(f2));
    }

    @Test
    void testRealizarLocacao_filmeNaoEncontrado() {
        when(locadoraMock.buscarFilmePorId(1)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            sistemaLocadora.realizarLocacao(1, 1, LocalDate.now().plusDays(5));
        });
        assertEquals("Filme não encontrado", ex.getMessage());
    }

    @Test
    void testRealizarLocacao_filmeIndisponivel() {
        Filme filme = new Filme(1, "F1", "Cat", 100, 5, "Dir", 2020, "Gen", "2020-01-01");
        when(locadoraMock.buscarFilmePorId(1)).thenReturn(filme);
        when(estoqueMock.verificarDisponibilidade(filme)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            sistemaLocadora.realizarLocacao(1, 1, LocalDate.now().plusDays(5));
        });
        assertEquals("Filme não disponível no estoque", ex.getMessage());
    }

    @Test
    void testRealizarLocacao_sucesso() {
        Filme filme = new Filme(1, "F1", "Cat", 100, 5, "Dir", 2020, "Gen", "2020-01-01");
        Locacao locacao = new Locacao(1, new Cliente(), filme, LocalDate.now(), LocalDate.now().plusDays(7));

        when(locadoraMock.buscarFilmePorId(1)).thenReturn(filme);
        when(estoqueMock.verificarDisponibilidade(filme)).thenReturn(true);
        when(locadoraMock.realizarLocacao(1, 1, LocalDate.now().plusDays(7))).thenReturn(locacao);
        when(estoqueMock.quantidadeDisponivel(filme)).thenReturn(5);

        Locacao resultado = sistemaLocadora.realizarLocacao(1, 1, LocalDate.now().plusDays(7));

        assertEquals(locacao, resultado);
        verify(estoqueMock).atualizarEstoque(filme, 4);
    }

    @Test
    void testRegistrarDevolucao_locacaoNaoEncontrada() {
        when(locadoraMock.listarLocacoes()).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            sistemaLocadora.registrarDevolucao(99);
        });
        assertEquals("Locação não encontrada", ex.getMessage());
    }

    @Test
    void testRegistrarDevolucao_sucesso() {
        Filme filme = new Filme(1, "F1", "Cat", 100, 5, "Dir", 2020, "Gen", "2020-01-01");
        Locacao locacao = new Locacao(1, new Cliente(), filme, LocalDate.now(), LocalDate.now().plusDays(7));

        when(locadoraMock.listarLocacoes()).thenReturn(List.of(locacao));
        when(estoqueMock.quantidadeDisponivel(filme)).thenReturn(5);

        sistemaLocadora.registrarDevolucao(1);

        verify(locadoraMock).registrarDevolucao(1);
        verify(estoqueMock).atualizarEstoque(filme, 6);
    }


}
