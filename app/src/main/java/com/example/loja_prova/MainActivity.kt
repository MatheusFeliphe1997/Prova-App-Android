package com.example.loja_prova

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.benchmark.perfetto.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loja_prova.ui.theme.Loja_ProvaTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutMain()
        }
    }
}


@Composable
fun LayoutMain() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = "cadastro"
    ) {
        composable("cadastro") { CadastroProduto(navController) }
        composable("lista") { ListaProdutos(navController) }
        composable("detalhes/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            DetalhesProduto(navController, produto)
        }
        composable("estatisticas") { Estatisticas(navController) }
    }
}


@Composable
fun ListaProdutos(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
        Text(text = "LISTA DE PRODUTOS", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(Estoque.instance.obterProdutos()) { produto ->

                    Text(text = "${produto.nome} (${produto.estoque} unidades)")
                    Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    val produtoJson = Gson().toJson(produto)
                    navController.navigate("detalhes/$produtoJson")
                }) {
                    Text("Detalhes")
                    }
                }

                item {
                Button(onClick = { navController.navigate("estatisticas") }) {
                    Text("Estatísticas")
                    }
                     }
             }
    }
}



@Composable
fun CadastroProduto(navController: NavController) {
    // Campos de entrada
    val nome = remember { mutableStateOf("") }
    val categoria = remember { mutableStateOf("") }
    val preco = remember { mutableStateOf("") }
    val estoque = remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = nome.value, onValueChange = { nome.value = it }, label = { Text("Nome") })
        TextField(value = categoria.value, onValueChange = { categoria.value = it }, label = { Text("Categoria") })
        TextField(value = preco.value, onValueChange = { preco.value = it }, label = { Text("Preço") })
        TextField(value = estoque.value, onValueChange = { estoque.value = it }, label = { Text("Estoque") })

        Button(onClick = {
            if (nome.value.isNotEmpty() && categoria.value.isNotEmpty() &&
                preco.value.isNotEmpty() && estoque.value.isNotEmpty()) {

                try {
                    val precoFloat = preco.value.toFloat()
                    val estoqueInt = estoque.value.toInt()

                    if (precoFloat <= 0) {
                        Toast.makeText(context, "Preço não pode ser menor que 0", Toast.LENGTH_SHORT).show()
                    } else if (estoqueInt < 1) {
                        Toast.makeText(context, "Quantidade deve ser pelo menos 1", Toast.LENGTH_SHORT).show()
                    } else {
                        val produto = Produto(nome.value, categoria.value, precoFloat, estoqueInt)
                        Estoque.instance.adicionarProduto(produto)
                        navController.navigate("lista")
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Preço e quantidade devem ser numéricos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Cadastrar")
        }
    }
}

@Composable
fun DetalhesProduto(navController: NavController, produto: Produto) {
    Column(modifier = Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.Center) {
        Text(text = "DETALHES DO PRODUTO", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Nome: ${produto.nome}")
        Text(text = "Categoria: ${produto.categoria}")
        Text(text = "Preço: ${produto.preco}")
        Text(text = "Quantidade: ${produto.estoque}")

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun Estatisticas(navController: NavController) {
    val valorTotal = Estoque.instance.calcularValorTotalEstoque()
    val quantidadeTotal = Estoque.instance.obterProdutos().sumOf { it.estoque }

    Column(modifier = Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.Center) {
        Text(text = "ESTATÍSTICAS", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Valor Total do Estoque: R$ ${"%.2f".format(valorTotal)}")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotal")

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}