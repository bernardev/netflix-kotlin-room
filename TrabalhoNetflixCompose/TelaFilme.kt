package com.example.netflix

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaFilme(id: Int, viewModel: PerfilViewModel, filmeViewModel: FilmeViewModel, irParaInicio: () -> Unit, irParaBuscar: () -> Unit, irParaMenu: () -> Unit, irParaFilme: (Int) -> Unit, voltar: () -> Unit) {

    var novoNome by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val filmeDAO = db.filmeDAO()

    // O filme desta tela vem direto do banco, pelo id recebido na rota "filme/{id}"
    var filme by remember { mutableStateOf(Filme(0, "", false)) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            filme = buscarFilmePorId(id, filmeDAO)
        }
        filmeViewModel.carregar(filmeDAO)
    }

    Scaffold(
        bottomBar = { BarraInferior("filme", viewModel.perfilAtual, irParaInicio, irParaBuscar, irParaMenu) }
    ) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    color = Color(62, 92, 118)
                ) {
                    Capa(filme.nome)
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "▶ Assistir",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = filme.nome,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (filme.baixado) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(46, 158, 91)
                        ) {
                            Text(
                                text = "Baixado",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(0.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BotaoBanner("▶ Assistir", Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    BotaoBanner(
                        "↓ Baixar",
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    filmeViewModel.baixar(filme, filmeDAO)
                                    filme = Filme(filme.id, filme.nome, true)
                                    Toast.makeText(context, "Download concluido", Toast.LENGTH_SHORT).show()
                                }
                            )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Descricao do filme",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Assista ${filme.nome} na Netflix. Baixe para ver sem internet.",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    viewModel.nomesAdmin.forEach {
                        if (it == viewModel.perfilAtual) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = novoNome,
                                onValueChange = { novoNome = it },
                                placeholder = { Text("Novo nome do filme") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = {
                                        if (novoNome == "") {
                                            Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
                                        } else {
                                            filmeViewModel.renomear(filme, novoNome, filmeDAO)
                                            filme = Filme(filme.id, novoNome, filme.baixado)
                                            novoNome = ""
                                        }
                                    }
                                ) {
                                    Text("Salvar nome")
                                }

                                Button(
                                    onClick = {
                                        filmeViewModel.excluir(filme, filmeDAO)
                                        voltar()
                                    }
                                ) {
                                    Text("Excluir filme")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }

                    Text(
                        text = "Recomendacoes",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Cartaz("O Mentalista", Color(140, 28, 19)) {
                            abrirFilme("O Mentalista", filmeViewModel, context, irParaFilme)
                        }
                        Cartaz("The Vampire Diaries", Color(46, 42, 79)) {
                            abrirFilme("The Vampire Diaries", filmeViewModel, context, irParaFilme)
                        }
                        Cartaz("Bird Box", Color(75, 96, 67)) {
                            abrirFilme("Bird Box", filmeViewModel, context, irParaFilme)
                        }
                    }
                }
            }
        }
    }
}
