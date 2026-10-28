# Netflix Gameday — Trabalho 3: Persistência de Dados com Room

App Android em Kotlin + Jetpack Compose que imita a Netflix. Ele continua o Trabalho 2: tem as mesmas telas e a mesma navegação com NavHost. A diferença é que agora os **perfis** e os **filmes** ficam salvos de verdade em um banco **Room** (SQLite) no próprio aparelho e continuam lá depois que o app é fechado.

> Disciplina: Desenvolvimento de Aplicativos Móveis — Trabalho 3 de 4 da A2.

---

## Como rodar o projeto

1. Abra o projeto no **Android Studio**. Se for criar um projeto novo, use o modelo *Empty Activity* com o pacote `com.example.netflix`.
2. Os arquivos `.kt` da pasta `TrabalhoNetflixCompose/` vão em `app/src/main/java/com/example/netflix/`.
3. As imagens da pasta `TrabalhoNetflixCompose/res/drawable/` vão em `app/src/main/res/drawable/`.
4. Configure o Gradle como mostrado na seção abaixo e clique em **Sync Now**.
5. Rode o app em um emulador ou celular (botão ▶ Run).

### Configuração do Room no Gradle (mesma ordem vista em aula)

**Passo 1 — `build.gradle.kts` do projeto:** adicione o plugin do KSP e sincronize *antes* de mexer no módulo.

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
}
```

> A versão do KSP precisa ser compatível com a versão do Kotlin do projeto (veja o aviso no slide "Adicionar o plug-in KSP ao projeto"). `2.3.4` é a versão usada no exemplo da aula.

**Passo 2 — `build.gradle.kts` do módulo (`app`):** ative o KSP nos `plugins` e sincronize.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}
```

**Passo 3 — ainda no módulo:** adicione as dependências do Room em `dependencies` e sincronize de novo.

```kotlin
val room_version = "2.8.0"

implementation("androidx.room:room-runtime:$room_version")
implementation("androidx.room:room-ktx:$room_version")
ksp("androidx.room:room-compiler:$room_version")
```

> `2.8.0` é a versão mostrada no slide da página de versões do Room. O outro slide da aula usa `2.6.1`, mas só para o Android Studio 2022.3.1. Com o Android Studio atual, use a `2.8.0`.

As dependências que já vinham do Trabalho 2 (Navigation Compose, ViewModel no Compose e ícones do Material) continuam iguais.

---

## Estrutura do Room

| Arquivo | O que é |
|---|---|
| `Filme.kt` | `@Entity(tableName = "filmes")` com `id` (`@PrimaryKey(autoGenerate = true)`), `nome` e `baixado` |
| `Perfil.kt` | `@Entity(tableName = "perfis")` com `id` (`@PrimaryKey(autoGenerate = true)`) e `nome` |
| `FilmeDAO.kt` | `@Dao` com `inserir`, `buscarTodos`, `atualizar`, `deletar` e as consultas filtradas `buscarPorId(id)` e `buscarBaixados()` |
| `PerfilDAO.kt` | `@Dao` com `inserir`, `buscarTodos`, `atualizar` e `deletar` |
| `AppDatabase.kt` | `@Database(entities = [Filme::class, Perfil::class], version = 1)` em padrão Singleton (`companion object` + `@Volatile` + `synchronized`) |
| `Componentes.kt` | Cores do app, barra superior (`TopAppBar`) e botão vermelho reutilizados em todas as telas |

Todos os métodos dos DAOs são `suspend fun`. Toda chamada ao banco é feita dentro de `CoroutineScope(Dispatchers.IO).launch { ... }` e protegida com `try/catch` + `Log.e(...)`, como no exemplo de Filmes da aula.

### Onde cada operação do CRUD acontece no app

| Operação | Onde, no app | Método do DAO |
|---|---|---|
| **Buscar** | Ao abrir as telas Perfis, Início, Buscar, Menu e Downloads (`LaunchedEffect(Unit)`) | `buscarTodos()` / `buscarBaixados()` |
| **Buscar por id** | Tela do filme: recebe só o id pela rota `filme/{id}` e busca o filme no banco | `buscarPorId(id)` |
| **Inserir** | Botão **Add** na tela de Perfis (novo perfil) e na tela Buscar (novo filme) | `inserir(...)` |
| **Atualizar** | **Salvar nome** na tela do filme (perfil admin), **Salvar** em Editar perfil, **Baixar** na tela do filme e a lixeira em Downloads (muda o campo `baixado`) | `atualizar(...)` |
| **Remover** | **Excluir filme** na tela do filme (perfil admin) e **Excluir perfil** em Editar perfil | `deletar(...)` |

---

## Melhorias de layout e usabilidade (pedido do professor)

Usamos só componentes vistos no material da disciplina (slides e código das aulas):

| Onde | O que mudou | Componente do material |
|---|---|---|
| Todas as telas | Paleta única: fundo preto, cartões cinza-escuro e vermelho da Netflix (`Componentes.kt`) | `Color(r, g, b)` |
| Menu, Editar perfil, Downloads e Filme | Barra superior padrão com título e botão de voltar, no lugar do cabeçalho feito à mão | `TopAppBar` + `TopAppBarDefaults.topAppBarColors` + `IconButton` |
| Botões de ação (Add, Salvar, Baixar, Excluir) | Botões no vermelho da Netflix; ações destrutivas (Excluir) em cinza, para não serem tocadas por engano | `Surface` + `clickable` + `shadowElevation` |
| Perfis, Buscar e Downloads | Itens das listas em cartões, com espaçamento uniforme; o perfil atual ganha borda vermelha | `Card` + `CardDefaults.cardColors` + `BorderStroke` + `Arrangement.spacedBy` |
| Tela do filme | Capa maior com o nome por cima; o conteúdo agora **rola** (antes, no perfil Admin, as recomendações ficavam cortadas); área do administrador separada num cartão | `LazyColumn` + `items` |
| Downloads | Contador de filmes baixados e aviso quando a lista está vazia | `Text`, `Card`, `if/else` |
| Menu | A fileira de avatares estourava a largura com muitos perfis; virou o item "Trocar de perfil" | `Column` + `Arrangement.spacedBy` |
| Barra inferior | Aba atual destacada em vermelho | `Surface` |

---

## Documentação do processo e das decisões

### Como estava o projeto no Trabalho 2 e o que mudou

No Trabalho 2, os perfis e os filmes eram listas de `String` dentro dos ViewModels (`mutableStateListOf`). Adicionar, renomear e excluir funcionavam, mas tudo sumia quando o app era fechado. Para saber qual filme abrir, a tela de filme lia o `filmeAtual` guardado no ViewModel.

No Trabalho 3:
- os perfis e os filmes viraram **entidades do Room**, cada uma com um `id` gerado pelo banco;
- os ViewModels deixaram de ser a "fonte" dos dados e só guardam uma cópia do que foi lido do banco. Toda alteração grava no banco e depois lê a lista de novo;
- a tela do filme passou a receber **só o id pela navegação** (`filme/{id}`) e a buscar o filme no banco com `buscarPorId`;
- a lista de downloads deixou de ser uma lista separada e virou o campo `baixado` do próprio filme, lido com a consulta `buscarBaixados()`.

### Quais Entity, DAO e Database foram criados, e por que esses campos

- **`Filme`** (`id`, `nome`, `baixado`): o `nome` já existia. O campo `baixado` substitui a lista separada de downloads do Trabalho 2. Assim, "baixar" e "remover download" viram um **update** no mesmo registro, e não precisamos de uma terceira tabela só para guardar nomes repetidos.
- **`Perfil`** (`id`, `nome`): os perfis só tinham nome no Trabalho 2. As opções "reproduzir próximo episódio" e "prévias" continuam apenas na memória porque não fazem parte das listas do app.
- **`FilmeDAO`** e **`PerfilDAO`**: um DAO por entidade, com os 4 métodos básicos.
- **`AppDatabase`**: um único banco (`netflix_database`) reunindo as duas entidades, no padrão Singleton visto em aula, para não abrir várias conexões.

### Como estruturamos as queries do DAO

Além dos 4 métodos básicos, o `FilmeDAO` tem duas consultas filtradas por campo:
- `@Query("SELECT * FROM filmes WHERE id = :id") suspend fun buscarPorId(id: Int): Filme` — usada pela tela do filme;
- `@Query("SELECT * FROM filmes WHERE baixado = 1") suspend fun buscarBaixados(): List<Filme>` — usada pela tela de Downloads. O Room guarda `Boolean` como 0/1.

### Dificuldades em migrar de `mutableStateListOf` para Room e como resolvemos

- **Listas de texto viraram listas de objetos:** as telas usavam o nome do filme ou do perfil para tudo. Agora usam `it.nome` para mostrar e `it.id` para navegar, atualizar e excluir.
- **Carga inicial:** o banco começa vazio na primeira execução. Para o app abrir com os mesmos filmes e perfis do Trabalho 2, quando a busca volta vazia gravamos os itens iniciais e buscamos de novo. Consequência: se todos os itens forem excluídos, os iniciais voltam na próxima abertura.
- **Cartazes fixos:** as telas Início e Filme têm cartazes fixos por nome (Outer Banks, O Mentalista...). Como a tela do filme agora precisa de um id, criamos `idPorNome(...)` no ViewModel. Se o filme foi renomeado ou excluído, o app mostra "Filme nao encontrado" em vez de abrir uma tela vazia.
- **Conflito de nomes:** já existia uma função de tela chamada `Perfil(...)`, com o mesmo nome da nova entidade. A função de tela foi renomeada para `ItemPerfil(...)`.
- **Tela atualizada depois de editar:** quando o filme é renomeado ou baixado, a tela atualiza o próprio estado na hora, sem esperar a próxima leitura do banco.

> ✏️ **PREENCHER PELO TRIO:** complementem com o que realmente aconteceu com vocês ao rodar no Android Studio (erros de Gradle/KSP, sincronização etc.).

### Teste de persistência (prints / vídeo)

> ✏️ **PREENCHER PELO TRIO:** prints ou vídeo curto mostrando o roteiro abaixo.
>
> 1. Adicionar um filme na tela Buscar (ex.: "Duna") e um perfil na tela de Perfis.
> 2. Entrar com o perfil Admin, abrir um filme, renomear e baixar.
> 3. Excluir um filme.
> 4. **Fechar o app completamente** (tirar da lista de apps recentes) e abrir de novo.
> 5. Mostrar que o filme e o perfil adicionados, o nome alterado e o download continuam lá, e que o filme excluído não voltou.

### Dificuldades de cada integrante

> ✏️ **PREENCHER PELO TRIO:** algum integrante teve dificuldade em algum ponto? Como resolveram?

---

## O que não entra neste trabalho

O banco é só local (Room/SQLite no aparelho). Não há login, servidor, API nem sincronização na nuvem.
