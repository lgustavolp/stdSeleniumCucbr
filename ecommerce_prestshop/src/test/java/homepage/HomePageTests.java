package homepage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

//Importacoes JUnit 5
//Asserts
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//Annotation Test
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {
	
	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}
	
	@Test
	public void testValidarCarrinhoZerado_ZeroItensnoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}
	
	ProdutoPage produtoPage;
	String nomeProduto_ProdutoPage;
	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais(){
		int indice = 0;
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);
		
		//System.out.println(nomeProduto_HomePage);
		//System.out.println(precoProduto_HomePage);
		
		produtoPage = homePage.clicarProduto(indice);
		
		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();
		
		//System.out.println(nomeProduto_ProdutoPage);
		//System.out.println(precoProduto_ProdutoPage);
		
		assertThat(nomeProduto_HomePage.toUpperCase(),is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage,is(precoProduto_ProdutoPage));
	}
	
	LoginPage loginPage;
	@Test
	public void testLoginComSucesso_UsuarioLogado () {
		//Clicar no Botao Sign In na Home Page
		loginPage = homePage.clicarBotaoSignIn();
		
		//Preencher Usuario e Senha
		loginPage.preencherEmail("lgpereira@teste.com");
		loginPage.preencherPassword("gustavo");
		
		//Clicar no Botao Sign In para Logar
		loginPage.clicarBotaoSignIn();
		
		//Validar se o usuario esta Logado de fato
		assertThat(homePage.estaLogado("Luiz Gustavo Pereira"), is(true));
		
		carregarPaginaInicial();
		
	}
	
	@ParameterizedTest
	@CsvFileSource(resources="/massaTeste_Login.csv", numLinesToSkip=1, delimiter=';')
	public void testLogin_UsuarioLogadoComDadoValidos(String nomeTeste, String email, 
			String password, String nomeUsuario, String resultado) {
		
		//Clicar no Botao Sign In na Home Page
		loginPage = homePage.clicarBotaoSignIn();
		
		//Preencher Usuario e Senha
		loginPage.preencherEmail(email);
		loginPage.preencherPassword(password);
		
		//Clicar no Botao Sign In para Logar
		loginPage.clicarBotaoSignIn();
		
		boolean esperado_loginOK;
		
		if (resultado.equals("positivo")) 
			esperado_loginOK = true;
		else
			esperado_loginOK = false;
		
		//Validar se o usuario esta Logado de fato
		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOK));
		
		capturarTela(nomeTeste,resultado);
		
		if (esperado_loginOK)
			homePage.clicaBotaoSignOut();
			
		carregarPaginaInicial();
		
	}
	
	
	ModalProdutoPage modalProdutoPage;
	
	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {
		
		String tamanhoProduto = "M";
		String corProduto = "Black";
		Integer quantidadeProduto = 2;
		
		//--Pre-condicao
		//Usuario Logado
		if(!homePage.estaLogado("Luiz Gustavo Pereira")) {
			testLoginComSucesso_UsuarioLogado ();
		}
		
		//--Teste
		//Selecionando Produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();
		
		//Selecioanr Tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da Lista:" + listaOpcoes.size());
		
		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);
		
		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da Lista:" + listaOpcoes.size());
		
		//Selecionar Cor
		produtoPage.selecionarCorPreta();
		
		//Selecionar Quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);
		
		//Adicionar ao Carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();
		
		//Validacoes
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado().endsWith("Product successfully added to your shopping cart"));
		
		System.out.println(modalProdutoPage.obterDescricaoProduto());
		
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		
		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);
		
		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));
		
		String subTotalString = modalProdutoPage.obterSubTotal();
		subTotalString = subTotalString.replace("$", "");
		Double subTotal = Double.parseDouble(subTotalString);
		
		Double subTotalCalculado = quantidadeProduto * precoProduto;
		
		assertThat(subTotalCalculado, is(subTotalCalculado));
		
	}
	
	//Valores Esperados
	
	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_input_quantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;
	
	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subtotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalTotal + esperado_shippingTotal;
	Double esperado_totalTaxInclTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;
	
	String esperado_nomeCliente = "Luiz Gustavo Pereira";	

	CarrinhoPage carrinhoPage;
	
	@Test
	public void testIrParaCarrinho_InformacoesPersistidas() {
		//--Pre-condicoes
		//Produto Incluido na Tela ModalProdutoPage
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();
		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();
		
		//Teste
		
		//Valida todos os elementos da tela
		System.out.println("*** TELA DO CARRINHO ***");

		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));
		
		System.out.println("*** ITENS TOTAIS ***");
		
		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
		
		//Assercoes Hamcrest
		
		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()), is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()), is(esperado_subtotalProduto));
		
		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()), is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()), is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()), is(esperado_totalTaxInclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));
		
		//Assercoes JUnit
		/*
		assertEquals(carrinhoPage.obter_nomeProduto(), esperado_nomeProduto);
		assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), esperado_precoProduto);
		assertEquals(carrinhoPage.obter_tamanhoProduto(), esperado_tamanhoProduto);
		assertEquals(carrinhoPage.obter_corProduto(), esperado_corProduto);
		assertEquals(carrinhoPage.obter_input_quantidadeProduto(), esperado_input_quantidadeProduto);
		assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()), esperado_subtotalProduto);
		assertEquals(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()), esperado_numeroItensTotal);
		assertEquals(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()), esperado_subtotalTotal);
		assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), esperado_shippingTotal);
		assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()), esperado_totalTaxExclTotal);
		assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()), esperado_totalTaxInclTotal);
		assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), esperado_taxesTotal);
		*/
	}
	
	CheckoutPage checkoutPage;
	
	@Test
	public void testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		// Pre-condicoes
		
		//Produto Disponivel no Carrinho de Compras
		testIrParaCarrinho_InformacoesPersistidas();
		
		//Teste
		
		//Clicar no botao 
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();
		
		//Preencher informacoes
		
		//Validar informacooes na tela
		
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxInclTotal()), is(esperado_totalTaxInclTotal));
		//assertThat(checkoutPage.obter_nomeCliente(), is(esperado_nomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));
		
		checkoutPage.clicarBotaoContinueAddress();
		
		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, " tax excl.");
		Double encontrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);
		
		assertThat(encontrado_shippingValor_Double,is(esperado_shippingTotal));
		
		checkoutPage.clicarbotaoContinueShipping();
		
		//Selecionar opcao "Pay by Check"
		checkoutPage.selecionarRadioPayByCheck();
		
		//Validar valor do check (amount)
		String encontrado_amountPayByCheck = checkoutPage.obter_amountPayByCheck();
		encontrado_amountPayByCheck = Funcoes.removeTexto(encontrado_amountPayByCheck, " (tax incl.)");
		Double encontrado_amoutPayByCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayByCheck);
		
		assertThat(encontrado_amoutPayByCheck_Double,is(esperado_totalTaxInclTotal));		
		
		//Clicar na opcaoo "I agree"
		checkoutPage.selecionarCheckBoxIAgree();
		
		assertTrue(checkoutPage.estaSelecionadoCheckBoxIAgree());
	}
	
	@Test
	
	public void testFinalizarPedido_pedidoRealizadoComSucesso() {
		//Pre-condicoes
		//Checkout completamente concluido
		testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();
		
		//Teste
		//Clicar no botao para confirmar o pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();
		
		//Validar valores da tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		//assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(),is("YOUR ORDER IS CONFIRMED"));
		
		assertThat(pedidoPage.obter_email(),is("lgpereira@teste.com"));
		assertThat(pedidoPage.obter_totalProdutos(),is(esperado_subtotalProduto));
		assertThat(pedidoPage.obter_totalTaxIncl(),is(esperado_totalTaxInclTotal));
		assertThat(pedidoPage.obter_metodoPagamento(),is("check"));
		
	}
	
}
