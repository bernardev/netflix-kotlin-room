package com.example.netflix

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TelaDownloads(viewModel: PerfilViewModel, filmeViewModel: FilmeViewModel, irParaInicio: () -> Unit, irParaBuscar: () -> Unit, irParaMenu: () -> Unit, irParaFilme: (Int) -> Unit) {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val filmeDAO = db.filmeDAO()

    // Ao abrir a tela, busca no banco so os filmes baixados
    LaunchedEffect(Unit) {
        filmeViewModel.carregarBaixados(filmeDAO)
    }

    Scaffold(
        bottomBar = { BarraInferior("downloads", viewModel.perfilAtual, irParaInicio, irParaBuscar, irParaMenu) }
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
                Cabecalho("Downloads", irParaMenu)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {

                    items(filmeViewModel.baixados) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ItemFilme(it.nome) {
                                irParaFilme(it.id)
                            }

                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable(onClick = { filmeViewModel.excluirDownload(it, filmeDAO) }),
                                shape = RoundedCornerShape(18.dp),
                                color = Color(255, 255, 255)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Excluir download"
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
