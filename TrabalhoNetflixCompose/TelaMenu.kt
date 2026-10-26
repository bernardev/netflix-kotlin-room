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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TelaMenu(viewModel: PerfilViewModel, voltar: () -> Unit, sair: () -> Unit, irParaEditar: () -> Unit, irParaDownloads: () -> Unit) {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val perfilDAO = db.perfilDAO()

    // Ao abrir a tela, busca os perfis no banco
    LaunchedEffect(Unit) {
        viewModel.carregar(perfilDAO)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(30.dp))

        Cabecalho("Perfis", voltar)

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(28, 28, 28)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(viewModel.perfilAtual, Color(232, 176, 4), Modifier.size(80.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = viewModel.perfilAtual,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            viewModel.perfis.forEach {
                ItemPerfil(
                    it.nome,
                    if (it.nome == viewModel.perfilAtual) Color(232, 176, 4) else Color(74, 107, 138),
                    Modifier.size(48.dp),
                    { viewModel.selecionar(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.clickable(onClick = { irParaEditar() }),
                shape = RoundedCornerShape(24.dp),
                color = Color(43, 43, 43)
            ) {
                Text(
                    text = "Editar perfil",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemMenu("Configuracoes do aplicativo", {})
        Spacer(modifier = Modifier.height(8.dp))
        ItemMenu("Downloads", irParaDownloads)
        Spacer(modifier = Modifier.height(8.dp))
        ItemMenu("Ajuda", {})
        Spacer(modifier = Modifier.height(8.dp))
        ItemMenu("Sair", sair)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Versao: 18.46.1 (17)",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Cabecalho(titulo: String, voltar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = { voltar() }),
            shape = RoundedCornerShape(18.dp),
            color = Color(255, 255, 255)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        }
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(36.dp))
    }
}

@Composable
fun ItemMenu(texto: String, aoClicar: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { aoClicar() }),
        shape = RoundedCornerShape(8.dp),
        color = Color(28, 28, 28)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = ">",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
