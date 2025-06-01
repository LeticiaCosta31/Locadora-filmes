package com.seuprojeto.locadora.model;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    void testClienteGetSet() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("Teste Cliente");
        cliente.setTelefone("123456789");
        cliente.setEndereco("Rua Exemplo, 123");

        assertEquals(1, cliente.getId());
        assertEquals("Teste Cliente", cliente.getNome());
        assertEquals("123456789", cliente.getTelefone());
        assertEquals("Rua Exemplo, 123", cliente.getEndereco());
    }

    @Test
    void testFilmeGetSet() {
        Filme filme = new Filme();
        filme.setId(10);
        filme.setTitulo("Filme Teste");
        filme.setDiretor("Diretor X");
        filme.setAnoLancamento(2020);
        filme.setGenero("Gênero Y");
        filme.setDataEstreia("2020-01-01");
        filme.setCategoria("Categoria Z");
        filme.setDuracao(120);
        filme.setQuantidadeEstoque(15);

        assertEquals(10, filme.getId());
        assertEquals("Filme Teste", filme.getTitulo());
        assertEquals("Diretor X", filme.getDiretor());
        assertEquals(2020, filme.getAnoLancamento());
        assertEquals("Gênero Y", filme.getGenero());
        assertEquals("2020-01-01", filme.getDataEstreia());
        assertEquals("Categoria Z", filme.getCategoria());
        assertEquals(120, filme.getDuracao());
        assertEquals(15, filme.getQuantidadeEstoque());
    }

    @Test
    void testLocacaoGetSet() {
        Cliente cliente = new Cliente(1, "Cliente Teste", "111222333", "Rua Teste");
        Filme filme = new Filme(2, "Filme Teste", "Categoria", 90, 5, "Diretor Teste", 2021, "Genero", "2021-01-01");
        LocalDate dataAluguel = LocalDate.now();
        LocalDate dataPrevista = LocalDate.now().plusDays(7);

        Locacao locacao = new Locacao();
        locacao.setId(100);
        locacao.setCliente(cliente);
        locacao.setFilme(filme);
        locacao.setDataAluguel(dataAluguel);
        locacao.setDataPrevistaDevolucao(dataPrevista);
        locacao.setDevolvido(false);

        assertEquals(100, locacao.getId());
        assertEquals(cliente, locacao.getCliente());
        assertEquals(filme, locacao.getFilme());
        assertEquals(dataAluguel, locacao.getDataAluguel());
        assertEquals(dataPrevista, locacao.getDataPrevistaDevolucao());
        assertFalse(locacao.isDevolvido());

        locacao.registrarDevolucao();
        assertTrue(locacao.isDevolvido());
    }
     @Test
    void testCategoriaGetSet() {
        Categoria categoria = new Categoria(1, "Ação");
        assertEquals(1, categoria.getId());
        assertEquals("Ação", categoria.getNome());

        categoria.setId(2);
        categoria.setNome("Comédia");
        assertEquals(2, categoria.getId());
        assertEquals("Comédia", categoria.getNome());
    }

    // --- Teste Estoque ---
    @Test
    void testEstoqueAdicionarAtualizarVerificar() {
        Estoque estoque = new Estoque();

        Filme filme = new Filme(1, "Filme Teste", "Categoria", 90, 0,
                "Diretor Teste", 2021, "Genero", "2021-01-01");

        // Inicialmente sem estoque
        assertFalse(estoque.verificarDisponibilidade(filme));
        assertEquals(0, estoque.quantidadeDisponivel(filme));

        // Adiciona 5 unidades
        estoque.adicionarFilme(filme, 5);
        assertEquals(5, estoque.quantidadeDisponivel(filme));
        assertTrue(estoque.verificarDisponibilidade(filme));
        assertEquals(5, filme.getQuantidadeEstoque());

        // Atualiza para 3 unidades
        estoque.atualizarEstoque(filme, 3);
        assertEquals(3, estoque.quantidadeDisponivel(filme));
        assertEquals(3, filme.getQuantidadeEstoque());
    }

    // --- Teste Funcionario ---
    @Test
    void testFuncionarioGetSetEAutenticacao() {
        Funcionario funcionario = new Funcionario(1, "José", "Gerente", "jose123", "senhaSegura");

        assertEquals(1, funcionario.getId());
        assertEquals("José", funcionario.getNome());
        assertEquals("Gerente", funcionario.getCargo());
        assertEquals("jose123", funcionario.getLogin());
        assertEquals("senhaSegura", funcionario.getSenha());

        // Testa autenticação correta e incorreta
        assertTrue(funcionario.autenticar("jose123", "senhaSegura"));
        assertFalse(funcionario.autenticar("jose123", "senhaErrada"));
        assertFalse(funcionario.autenticar("outroLogin", "senhaSegura"));
    }

    // --- Teste Pagamento ---
    @Test
    void testPagamentoValidarProcessar() {
        Locacao locacao = new Locacao(1, new Cliente(), new Filme(), LocalDate.now(), LocalDate.now().plusDays(7));
        Pagamento pagamento = new Pagamento(1, locacao, 100.0, LocalDate.now(), "Dinheiro");

        assertEquals(1, pagamento.getId());
        assertEquals(locacao, pagamento.getLocacao());
        assertEquals(100.0, pagamento.getValor());
        assertEquals("Dinheiro", pagamento.getTipo());

        assertTrue(pagamento.validarPagamento());

        // Pagamento válido não deve lançar exceção
        assertDoesNotThrow(() -> pagamento.processarPagamento());

        // Testa pagamento inválido (valor zero)
        Pagamento pagamentoInvalido1 = new Pagamento(2, locacao, 0, LocalDate.now(), "Dinheiro");
        assertFalse(pagamentoInvalido1.validarPagamento());
        assertThrows(IllegalArgumentException.class, () -> pagamentoInvalido1.processarPagamento());

        // Testa pagamento inválido (tipo incorreto)
        Pagamento pagamentoInvalido2 = new Pagamento(3, locacao, 50, LocalDate.now(), "Cheque");
        assertFalse(pagamentoInvalido2.validarPagamento());
        assertThrows(IllegalArgumentException.class, () -> pagamentoInvalido2.processarPagamento());
    }
}

