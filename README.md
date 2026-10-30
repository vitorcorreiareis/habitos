# Hábitos 📋

Aplicativo Android para criação e acompanhamento de hábitos diários, construído com Jetpack Compose, arquitetura MVVM e Firebase Authentication.

---

## Funcionalidades

- **Autenticação** — cadastro e login via Firebase Auth (e-mail/senha)
- **Criação de hábitos** — nome, descrição, frequência (diário/semanal), cor e emoji personalizados
- **Acompanhamento diário** — marque hábitos como concluídos com um toque
- **Histórico de 14 dias** — visualize seu progresso recente em cada hábito
- **Sequências (streaks)** — cálculo automático de dias consecutivos
- **Taxa de conclusão** — percentual de dias concluídos sobre o total
- **Edição e exclusão** — gerencie seus hábitos a qualquer momento
- **Persistência local** — dados salvos com DataStore + kotlinx-serialization (sem banco de dados externo)

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM (ViewModel + StateFlow) |
| Navegação | Navigation Compose 2.9.0 |
| Persistência local | DataStore Preferences + kotlinx-serialization 1.7.3 |
| Autenticação | Firebase Auth (Firebase BOM 33.7.0) |
| Injeção de dependência | Manual (sem Hilt/Dagger) |
| Build | Android Gradle Plugin 9.3.3 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 37 |

---

## Arquitetura

O projeto segue o padrão **MVVM** recomendado pelo Google para apps Android modernos.

```
habitos/
├── data/
│   ├── model/
│   │   └── Habit.kt              # Entidade principal + enum HabitFrequency
│   └── repository/
│       ├── AuthRepository.kt     # Wrapper do Firebase Auth
│       └── HabitRepository.kt   # CRUD com DataStore Preferences
├── viewmodel/
│   ├── AuthViewModel.kt          # Estados: Idle / Loading / Success / Error
│   └── HabitViewModel.kt        # StateFlow de hábitos, streak e taxa
├── ui/
│   ├── auth/
│   │   ├── LoginScreen.kt        # Tela de login
│   │   └── RegisterScreen.kt    # Tela de cadastro
│   ├── habits/
│   │   ├── HabitListScreen.kt   # Lista com card de progresso e FAB
│   │   ├── HabitFormScreen.kt   # Formulário criar/editar
│   │   └── HabitDetailScreen.kt # Estatísticas e histórico do hábito
│   ├── navigation/
│   │   └── AppNavigation.kt     # NavHost com todas as rotas
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

### Fluxo de dados

```
UI (Composable)
    ↕ collectAsState / LaunchedEffect
ViewModel (StateFlow)
    ↕ suspend fun
Repository
    ↕ DataStore / Firebase SDK
Fonte de dados
```

---

## Como executar

### Pré-requisitos

- Android Studio Ladybug ou superior
- JDK 11+
- Conta no [Firebase Console](https://console.firebase.google.com/)

### Configuração do Firebase

1. Acesse o [Firebase Console](https://console.firebase.google.com/) e crie um projeto.
2. Registre o app Android com o pacote `com.example.habitos`.
3. Baixe o arquivo `google-services.json` e coloque-o em `app/`.
4. No Firebase Console, ative o provedor **E-mail/senha** em **Authentication → Sign-in method**.

### Build e execução

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/habitos.git
cd habitos

# Abra no Android Studio e sincronize o Gradle
# (ou via linha de comando)
./gradlew assembleDebug
./gradlew installDebug
```

> **Atenção:** o arquivo `google-services.json` **não** está versionado por conter chaves privadas do Firebase. Você precisa gerar o seu próprio.

---

## Capturas de tela

> *Em breve*

---

## Dependências principais

```toml
# gradle/libs.versions.toml (trecho)
[versions]
composeBom            = "2025.05.00"
navigationCompose     = "2.9.0"
datastorePreferences  = "1.1.4"
lifecycleViewmodel    = "2.11.0"
kotlinxSerialization  = "1.7.3"
firebaseBom           = "33.7.0"
```

---

## Estrutura de commits

O desenvolvimento foi dividido em camadas para facilitar a revisão:

1. **chore:** configuração inicial do projeto Android
2. **feat:** modelo de dados `Habit` e enum `HabitFrequency`
3. **feat:** repositórios de autenticação e persistência
4. **feat:** ViewModels com StateFlow
5. **feat:** telas de autenticação (login e cadastro)
6. **feat:** navegação com Compose Navigation
7. **feat:** telas de hábitos (lista, formulário e detalhes)
8. **docs:** README completo

---

## Licença

Distribuído sob a licença MIT. Veja [`LICENSE`](LICENSE) para mais detalhes.
