# language: pt
Funcionalidade: Comprar Produto
  Como um usuario logado
  Eu quero escolher um produto
  e visualizar esse produto no carrinho
  Para concluir o pedido

  @validacao_inicial
  Cenario: Deve mostrar uma lista de oito produtos na pagina inicial
    Dado que estou na pagina inicial
    Quando não estou logado
    Entao visualizo 8 produtos disponiveis
    E carrinho esta zerado

  @fluxo_padrao
  Esquema do Cenario: Deve mostrar produto escolhido confirmado
    Dado que estou na pagina inicial
    Quando estou logado
    E seleciono um produto na posicao <posicao>
    E nome do produto na tela principal e na tela de produto eh <nomeProduto>
    E preco do produto na tela principal e na tela de produto eh <precoProduto>
    E adiciono o produto no carrinho com tamanho <tamanhoProduto> cor <corProduto> e quantidade <quantidadeProduto>
    Entao produto aparece na confirmacao com nome <nomeProduto> preco <precoProduto> tamanho <tamanhoProduto> cor <corProduto> e quantidade <quantidadeProduto>

    Exemplos: 
      | posicao | nomeProduto                   | precoProduto | tamanhoProduto | corProduto | quantidadeProduto |
      |       0 | "Hummingbird printed t-shirt" | "$19.12"     | "M"            | "Black"    |                 2 |
      |       1 | "Hummingbird printed sweater" | "$28.72"     | "XL"           | "N/A"      |                 3 |
