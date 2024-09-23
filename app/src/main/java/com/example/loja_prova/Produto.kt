package com.example.loja_prova

import androidx.compose.runtime.saveable.Saver

data class Produto(
    val nome: String,
    val categoria: String,
    val preco: Float,
    val estoque: Int
) {
    companion object {
        val listaProdutos = mutableListOf<Produto>()
    }
}
