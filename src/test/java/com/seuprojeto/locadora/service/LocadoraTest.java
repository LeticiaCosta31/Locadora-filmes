package com.seuprojeto.locadora.service;

import com.seuprojeto.locadora.model.Cliente;
import com.seuprojeto.locadora.model.Filme;
import com.seuprojeto.locadora.model.Locacao;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LocadoraTest {

    private Locadora locadora;

    @BeforeAll
    void setup() {
        locadora = new Locadora();
    }

    @BeforeEach
    void limparTabelas() {
       locadora.limparTabelas();
    }

    @AfterAll
    void tearDown() {
        // Fecha a conexão da Locadora para liberar recursos
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
}
