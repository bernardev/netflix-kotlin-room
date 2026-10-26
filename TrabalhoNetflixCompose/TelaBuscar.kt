package com.example.netflix

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun TelaBuscar(viewModel: PerfilViewModel, filmeViewModel: FilmeViewModel, irParaInicio: () -> Unit, irParaMenu: () -> Unit, irParaFilme: (Int) -> Unit) {

    var busca by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val filmeDAO = db.filmeDAO()

    // Ao abrir a tela, busca os filmes no banco
    LaunchedEffect(Unit) {
        filmeViewModel.carregar(filmeDAO)
    }

    Scaffold(
        bottomBar = { BarraInferior("buscar", viewModel.perfilAtual, irParaInicio, {}, irParaMenu) }
    ) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        modifier = Modifier.width(240.dp),
                        value = busca,
                        onValueChange = { busca = it },
                        placeholder = { Text("Buscar ou adicionar") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (busca == "") {
                                Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
                            } else {
                                filmeViewModel.adicionar(busca, filmeDAO)
                                busca = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.width(110.dp).height(80.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(122, 122, 122)
                    ) {
                        Capa("A lista de Schindler")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Filme Recomendado:",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "A lista de Schindler",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {

                    items(filmeViewModel.filmes) {
                        ItemFilme(it.nome) {
                            irParaFilme(it.id)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ItemFilme(nomeFilme: String, aoClicar: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(6.dp)
            .clickable(onClick = { aoClicar() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.width(110.dp).height(80.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(62, 92, 118)
        ) {
            Capa(nomeFilme)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = nomeFilme,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
