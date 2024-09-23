package com.example.loja_prova

class Estoque {
    private val produtos = mutableListOf<Produto>()

    fun adicionarProduto(produto: Produto) {
        produtos.add(produto)
    }

    fun calcularValorTotalEstoque(): Float {
        return produtos.map { it.preco * it.estoque }.sum()
    }

    fun obterProdutos(): List<Produto> {
        return produtos
    }

    companion object {

        var instance = Estoque()
    }
}