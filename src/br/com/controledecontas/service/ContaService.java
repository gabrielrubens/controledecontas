package br.com.controledecontas.service;

import java.util.List;

import br.com.controledecontas.model.Conta;
import br.com.controledecontas.model.TipoConta;
import br.com.controledecontas.model.Usuario;

public interface ContaService {

	Conta pesquisaPorId(Integer id);

	void salva(Conta conta);

	void deleta(Conta conta);

	void atualiza(Conta conta);

	List<Conta> pesquisaPorTipo(Usuario usuario, TipoConta tipoConta);

}
