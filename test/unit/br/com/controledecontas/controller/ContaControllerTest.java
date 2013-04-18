package br.com.controledecontas.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.controledecontas.model.Conta;
import br.com.controledecontas.model.TipoConta;
import br.com.controledecontas.model.Usuario;
import br.com.controledecontas.model.UsuarioSession;
import br.com.controledecontas.service.ContaService;


public class ContaControllerTest {

	private Result result;
	private ContaController controller;
	private Validator validator;

	@Mock
	private ContaService service;
	@Mock
	private UsuarioSession usuarioSession;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new ContaController(result, service, usuarioSession, validator);
	}
	
	@Test
	public void deveriaAbrirTelaInicialComContasDoUltimoMes() {
		List<Conta> listaDeContas = new ArrayList<Conta>();
		
		when(service.pesquisaPorMesEAno(any(Usuario.class), anyInt(), anyInt())).thenReturn(listaDeContas);
		
		controller.index();
		
		assertTrue("Deve haver uma lista de contas.", result.included().containsKey("contas"));
	}
	
	@Test
	public void deveriaAbrirPaginaDeAdicionarComListaDeTipoDeConta() {
		controller.novo();
		
		assertTrue("Deve haver uma lista de tipos de Conta.", result.included().containsKey("tiposConta"));
	}
	
	@Test
	public void deveriaAbrirPaginaDeAtualizacao() {
		Conta conta = conta();
		
		when(service.pesquisaPorId(conta.getId())).thenReturn(conta);
		
		controller.paginaDeAtualizacao(conta.getId());
		
		assertTrue("Deve haver uma conta", result.included().containsKey("conta"));
	}
	
	@Test
	public void deveriaSalvarConta() {
		Conta conta = conta();
		
		when(usuarioSession.getUsuario()).thenReturn(criaUsuario());
		
		controller.salvar(conta);
		
		verify(service).salva(conta);

		assertNotNull("Usuário não deve ser nulo", conta.getUsuario());
		assertEquals(criaUsuario().getId(), conta.getUsuario().getId());
		assertTrue("Deveria retornar mensagem de sucesso.", result.included().containsKey("notice"));
		assertFalse("Não deveria conter mensagem de erro.", result.included().containsKey("erros"));
	}
	
	@Test(expected = ValidationException.class)
	public void naoDeveriaSalvarContaVazia() {
		Conta conta = contaVazia();
		
		when(usuarioSession.getUsuario()).thenReturn(criaUsuario());
		
		controller.salvar(conta);
	}
	
	@Test
	public void naoDeveriaSalvarContaException() {
		Conta conta = conta();
		
		when(usuarioSession.getUsuario()).thenReturn(criaUsuario());
		doThrow(new RuntimeException()).when(service).salva(conta);
		
		controller.salvar(conta);
		
		assertTrue("Deveria conter mensagem de erro.", result.included().containsKey("erros"));
		assertFalse("Não deveria conter mensagem de successo.", result.included().containsKey("notice"));
	}

	@Test
	public void deveriaAtualizarUmaConta() {
		Conta conta = conta();

		controller.atualizar(conta);
		
		verify(service).atualiza(conta);
		
		assertTrue("Deveria conter uma mensagem de sucesso", result.included().containsKey("notice"));
		assertFalse("Não deveria conter uma mensagem de erro", result.included().containsKey("erros"));
	}
	
	@Test(expected = ValidationException.class)
	public void naoDeveriaAtualizarUmaContaVazia() {
		Conta conta = contaVazia();
		
		controller.atualizar(conta);
	}
	
	@Test
	public void naoDeveriaAtualizarUmaContaException() {
		Conta conta = conta();
		
		doThrow(new RuntimeException()).when(service).atualiza(conta);
		
		controller.atualizar(conta);
		
		assertTrue("Deveria conter mensagem de erro.", result.included().containsKey("erros"));
		assertFalse("Não deveria conter mensagem de successo.", result.included().containsKey("notice"));
	}
	
	private Conta conta() {
		Conta conta = new Conta();
		
		conta.setId(-1);
		conta.setData(Calendar.getInstance().getTime());
		conta.setDescricao("Cartão de Crédito");
		conta.setTipoConta(TipoConta.DEBITO);
		conta.setValor(new BigDecimal("102.00"));
		
		return conta;
	}
	
	private Conta contaVazia() {
		Conta conta = new Conta();
		
		conta.setDescricao("");
		conta.setValor(new BigDecimal("0.00"));
		conta.setData(null);
		
		return conta;
	}
	
	private Usuario criaUsuario() {
		Usuario usuario = new Usuario();
		
		usuario.setId(1);
		usuario.setNome("Renan");
		usuario.setUsername("renanigt");
		usuario.setPassword("teste");
		
		return usuario;
	}
	
}
