package br.com.controledecontas.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;

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
	private ContaController contaController;
	private Validator validator;
	
	@Mock
	private ContaService contaService;
	@Mock
	private UsuarioSession usuarioSession;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		contaController = new ContaController(result, contaService, usuarioSession, validator);
	}
	
	@Test
	public void deveriaAbrirTelaDeSalvarConta() {
		contaController.novo();
		assertFalse("Não deveria retornar mensagem de erro.", result.included().containsKey("erros"));
	}
	
	@Test
	public void deveriaSalvarConta() {
		Conta conta = criaConta();
		
		when(usuarioSession.isLogado()).thenReturn(true);
		when(usuarioSession.getUsuario()).thenReturn(criaUsuario());
		
		contaController.salvar(conta);
		
		verify(contaService).salva(conta);
		
		assertTrue("Deveria retornar mensagem de sucesso.", result.included().containsKey("notice"));
		assertFalse("Não deveria conter mensagem de erro.", result.included().containsKey("erros"));
	}
	
	@Test
	public void naoDeveriaSalvarContaSemUsuarioLogado() {
		Conta conta = criaConta();
		
		when(usuarioSession.isLogado()).thenReturn(false);
		
		contaController.salvar(conta);
		
		verify(contaService, never()).salva(conta);
		
		assertTrue("Deveria conter mensagem de erro.", result.included().containsKey("erros"));
		assertFalse("Não deveria conter mensagem de sucesso.", result.included().containsKey("notice"));
	}
	
	@Test(expected = ValidationException.class)
	public void naoDeveriaSalvarContaVazia() {
		Conta conta = criaContaVazia();
		
		when(usuarioSession.isLogado()).thenReturn(true);
		when(usuarioSession.getUsuario()).thenReturn(criaUsuario());
		
		contaController.salvar(conta);
	}
	
	private Conta criaConta() {
		Conta conta = new Conta();
		
		conta.setData(Calendar.getInstance().getTime());
		conta.setDescricao("Cartão de Crédito");
		conta.setTipoConta(TipoConta.DEBITO);
		conta.setValor(new BigDecimal("102.00"));
		conta.setUsuario(criaUsuario());
		
		return conta;
	}
	
	private Conta criaContaVazia() {
		Conta conta = new Conta();
		
		conta.setDescricao("");
		conta.setValor(new BigDecimal("0.00"));
		conta.setData(null);
		
		return conta;
	}
	
	private Usuario criaUsuario() {
		Usuario usuario = new Usuario();
		
		usuario.setNome("Renan");
		usuario.setUsername("renanigt");
		usuario.setPassword("teste");
		
		return usuario;
	}
	
}
