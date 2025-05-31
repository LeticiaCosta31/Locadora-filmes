package com.seuprojeto.locadora.ui;

import com.seuprojeto.locadora.model.Cliente;
import com.seuprojeto.locadora.model.Locacao;
import com.seuprojeto.locadora.model.Filme;
import com.seuprojeto.locadora.service.Locadora;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UiTest {


    @Test
    void validarEntradaTexto_DeveRejeitarEntradaVaziaEAceitarTexto() {
        // Simulamos 2 entradas vazias e depois uma válida "Texto válido"
        String input = "\n\nTexto válido\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        String resultado = validador.validarEntradaTexto(scanner, "Digite algo: ");
        assertEquals("Texto válido", resultado);
    }

    @Test
    void validarEntradaNumerica_DeveAceitarApenasNumero() {
        // Simula uma entrada inválida "abc" e depois "123"
        String input = "abc\n123\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        int resultado = validador.validarEntradaNumerica(scanner, "Digite um número: ");
        assertEquals(123, resultado);
    }

    @Test
    void validarEntradaData_DeveAceitarDataValida() {
        // Simula uma data inválida (30/02/2023) e depois uma válida (28/02/2023)
        String input = "2023-02-30\n2023-02-28\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        String data = validador.validarEntradaData(scanner, "Digite uma data: ");
        assertEquals("2023-02-28", data);
    }

    @Test
    void validarAnoLancamento_DeveAceitarAnoDentroDoIntervalo() {
        // Simula um ano inválido (1800) e depois válido (2020)
        String input = "1800\n2020\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        int ano = validador.validarAnoLancamento(scanner, "Digite ano: ");
        assertEquals(2020, ano);
    }

    @Test
    void validarEntradaTexto_DeveRejeitarApenasEspacos() {
        String input = "   \n\t\nTexto válido\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        String resultado = validador.validarEntradaTexto(scanner, "Digite algo: ");
        assertEquals("Texto válido", resultado);
    }

    @Test
    void validarEntradaNumerica_DeveAceitarNumeroNegativo() {
        String input = "-5\n10\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        int resultado = validador.validarEntradaNumerica(scanner, "Digite um número: ");
        assertEquals(-5, resultado);
    }

    @Test
    void validarEntradaData_DeveAceitarDatasNosLimites() {
        String input = "1900-01-01\n2100-12-31\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        String data1 = validador.validarEntradaData(scanner, "Digite data 1: ");
        assertEquals("1900-01-01", data1);

        String data2 = validador.validarEntradaData(scanner, "Digite data 2: ");
        assertEquals("2100-12-31", data2);
    }

    @Test
    void validarEntradaData_DeveRejeitarFormatoIncorreto() {
        String input = "01-01-2020\n2020-01-01\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        String data = validador.validarEntradaData(scanner, "Digite uma data: ");
        assertEquals("2020-01-01", data);
    }

    @Test
    void validarAnoLancamento_DeveRejeitarAnoInvalidoEEntradaTexto() {
        String input = "1800\ntexto\n2000\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        int ano = validador.validarAnoLancamento(scanner, "Digite ano: ");
        assertEquals(2000, ano);
    }

    @Test
    void validarEntradaNumerica_DeveRejeitarStringsAntesDoNumero() {
        String input = "abc\nxyz\n42\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ValidadorEntrada validador = new ValidadorEntrada();

        int resultado = validador.validarEntradaNumerica(scanner, "Digite um número: ");
        assertEquals(42, resultado);
    }

    @Test
    void testValidarEntradaTexto_comValorVazio_retornaValido() {
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("", "   ", "Texto válido");
        ValidadorEntrada validador = new ValidadorEntrada();

        String resultado = validador.validarEntradaTexto(scanner, "Mensagem: ");
        assertEquals("Texto válido", resultado);
    }

    @Test
    void testListarFilmes_mostraMensagemQuandoListaVazia() throws Exception {
        Locadora locadora = mock(Locadora.class);
        when(locadora.listarFilmes()).thenReturn(Collections.emptyList());

        Method method = Main.class.getDeclaredMethod("listarFilmes", Locadora.class);
        method.setAccessible(true);

        // Capturar saída do console
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        method.invoke(null, locadora);

        String saida = outContent.toString();
        assertTrue(saida.contains("Nenhum filme cadastrado"));

        System.setOut(System.out);
    }

    @Test
    void testListarClientes_mostraMensagemQuandoListaVazia() throws Exception {
        Locadora locadoraMock = mock(Locadora.class);
        when(locadoraMock.listarClientes()).thenReturn(Collections.emptyList());

        Method method = Main.class.getDeclaredMethod("listarClientes", Locadora.class);
        method.setAccessible(true);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        method.invoke(null, locadoraMock);

        System.setOut(originalOut);

        String saida = outContent.toString();
        assertTrue(saida.contains("Nenhum cliente cadastrado"));
    }

    @Test
    void testListarClientes_comLista_comClientesMostraDetalhes() throws Exception {
        Locadora locadoraMock = mock(Locadora.class);
        Cliente cliente = new Cliente(1, "Cliente Teste", "123456789", "Rua Teste");
        when(locadoraMock.listarClientes()).thenReturn(List.of(cliente));

        Method method = Main.class.getDeclaredMethod("listarClientes", Locadora.class);
        method.setAccessible(true);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        method.invoke(null, locadoraMock);

        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("Cliente Teste"));
        assertTrue(output.contains("123456789"));
    }

    @Test
    void testListarLocacoes_mostraMensagemQuandoListaVazia() throws Exception {
        Locadora locadoraMock = mock(Locadora.class);
        when(locadoraMock.listarLocacoes()).thenReturn(Collections.emptyList());

        Method method = Main.class.getDeclaredMethod("listarLocacoes", Locadora.class);
        method.setAccessible(true);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        method.invoke(null, locadoraMock);

        System.setOut(originalOut);

        String saida = outContent.toString();
        assertTrue(saida.contains("Nenhuma locação encontrada"));
    }

    @Test
    void testListarLocacoes_comLista_mostraDetalhes() throws Exception {
        Locadora locadoraMock = mock(Locadora.class);
        Cliente cliente = new Cliente(1, "Cliente Teste", "123", "Rua X");
        Filme filme = new Filme(1, "Filme Teste", "Categoria", 90, 5, "Diretor", 2020, "Genero", "2020-01-01");
        Locacao locacao = new Locacao(1, cliente, filme, LocalDate.now(), LocalDate.now().plusDays(7));
        when(locadoraMock.listarLocacoes()).thenReturn(List.of(locacao));

        Method method = Main.class.getDeclaredMethod("listarLocacoes", Locadora.class);
        method.setAccessible(true);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        method.invoke(null, locadoraMock);

        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("Cliente Teste"));
        assertTrue(output.contains("Filme Teste"));
    }

    

}
