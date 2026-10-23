# 🌱 SIMV - Sistema de Monitoramento de Vegetação

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![CameraX](https://img.shields.io/badge/Camera-CameraX%201.4.1-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/training/camerax)
[![Room](https://img.shields.io/badge/DB-Room-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-orange.svg?style=flat)](https://developer.android.com)
[![Sprint](https://img.shields.io/badge/Sprint-4-blue.svg?style=flat)](https://github.com/mateusMMagalhaes/SPRINT3_ANDROIDSTUDIO)

**SIMV (Sistema de Monitoramento de Vegetação)** é um aplicativo Android desenvolvido em **Kotlin** e **Jetpack Compose** voltado para o acompanhamento e coleta de registros fotográficos em campo para monitoramento ambiental e de vegetação.

> 🆕 **Sprint 4** — Evolução para MVP completo com persistência local (Room + SharedPreferences), arquitetura desacoplada (UI / Domain / Data), gerenciamento de estados (Loading / Success / Error) e histórico de sessões.

---

## 👥 Integrantes do Grupo

<!-- TODO: Preencher com nome e RM de cada integrante -->
| Nome | RM |
|---|---|
| _(a preencher)_ | _(a preencher)_ |
| _(a preencher)_ | _(a preencher)_ |

---

## 🔗 Links

| Recurso | Link |
|---|---|
| 📦 Repositório GitHub | [github.com/mateusMMagalhaes/SPRINT3_ANDROIDSTUDIO](https://github.com/mateusMMagalhaes/SPRINT3_ANDROIDSTUDIO) |
| 🎨 Protótipo / Wireframe | _(a preencher)_ |
| 🎥 Vídeo de Demonstração | _(a preencher — YouTube não listado)_ |

---

## 📱 Funcionalidades

O aplicativo apresenta uma interface moderna em *Dark Mode* com navegação fluida entre as etapas da sessão de monitoramento (*Run Session*):

1. **Tela Inicial (Start Screen)**
   - Apresentação do sistema com acesso rápido para iniciar uma nova sessão.
   - Botão de acesso ao **Histórico de Sessões**.

2. **Configuração da Sessão (Setup Run Screen)**
   - Entrada de metadados: **Nome do Projeto**, **Localização** e **ID do Operador**.
   - Auto-preenchimento do último ID do Operador via **SharedPreferences**.
   - Integração com GPS do dispositivo (latitude/longitude via `LocationManager`).
   - Validação de formulário com feedback de erro em tempo real.

3. **Visão da Câmera (Camera View Screen)**
   - Preview em tempo real via **CameraX** com grade de auxílio (Regra dos Terços).
   - Efeito visual de *flash* ao capturar foto.
   - Contador de fotos capturadas (limite máximo configurável).
   - Cronômetro ativo em tempo real (`HH:mm:ss`).
   - Opção de encerrar a sessão antecipadamente (*Stop Run*).

4. **Conclusão da Sessão (Run Completed Screen)**
   - Resumo: total de fotos e tempo de duração.
   - Sessão **persistida automaticamente** no banco de dados Room.
   - Feedback visual de sucesso/erro ao salvar.

5. **Histórico de Sessões (History Screen)** *(novo na Sprint 4)*
   - Lista de todas as sessões realizadas (persistidas localmente).
   - Estados visuais: *Carregando*, *Vazio* e *Sessões encontradas*.
   - Possibilidade de deletar registros individuais.

---

## 🏗️ Arquitetura do Projeto

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com princípios de **Clean Architecture**, separando claramente as responsabilidades entre as camadas.

```text
com.example.simv/
├── data/
│   ├── local/
│   │   ├── RunSessionEntity.kt     # Entidade Room (tabela run_sessions)
│   │   ├── RunSessionDao.kt        # Data Access Object (CRUD com Flow)
│   │   ├── SimvDatabase.kt         # Singleton do banco Room
│   │   ├── RunSessionMapper.kt     # Conversão Entity ↔ Domain Model
│   │   └── UserPreferences.kt      # SharedPreferences (operadorId, maxFotos)
│   ├── model/
│   │   └── RunSession.kt           # Modelo de domínio da sessão
│   └── repository/
│       ├── RunRepository.kt        # Repositório principal (Room + estado atual)
│       └── PreferencesRepository.kt # Repositório de preferências simples
├── domain/
│   └── usecase/
│       └── RunUseCase.kt           # Regras de negócio, validação e formatação
├── navigation/
│   └── SimvNavigation.kt           # Enum de telas e lógica de navegação
├── ui/
│   ├── screens/
│   │   ├── StartScreen.kt          # Tela inicial
│   │   ├── SetupRunScreen.kt       # Formulário + GPS
│   │   ├── CameraViewScreen.kt     # Câmera + captura
│   │   ├── RunCompletedScreen.kt   # Resumo da sessão
│   │   └── HistoryScreen.kt        # Histórico de sessões (novo)
│   ├── theme/
│   │   ├── Color.kt                # Paleta de cores
│   │   ├── Theme.kt                # MaterialTheme
│   │   └── Type.kt                 # Tipografia
│   └── viewmodel/
│       ├── RunViewModel.kt         # Gerenciamento de estado da UI
│       └── UiState.kt              # Sealed class Loading/Success/Error
└── MainActivity.kt                 # Ponto de entrada com Crossfade
```

### Fluxo de Dados

```
UI (Screens)
    ↕ events / state
ViewModel (RunViewModel)
    ↕ suspend fun / Flow
UseCase (RunUseCase)
    ↕ result / Flow
Repository (RunRepository / PreferencesRepository)
    ↕ SQL / Prefs
Data Sources (Room DAO / SharedPreferences)
```

---

## 💾 Persistência de Dados

### Room Database — `SimvDatabase`
- **Entidade**: `RunSessionEntity` — representa cada sessão salva (projectName, location, operatorId, photosCaptured, startTime, endTime, isCompleted).
- **DAO**: `RunSessionDao` — operações `insert`, `update`, `delete`, `getAllSessions` (retorna `Flow<List<...>>`).
- **Mapper**: converte entre o modelo de domínio (`RunSession`) e a entidade do banco (`RunSessionEntity`).
- **Comportamento**: ao concluir uma sessão (botão *Done* ou atingir limite de fotos), a sessão é salva automaticamente.
- **Recuperação**: dados sobrevivem ao fechamento do app; o histórico é carregado automaticamente ao abrir `HistoryScreen`.

### SharedPreferences — `UserPreferences`
- Salva o último **ID do Operador** usado para auto-preenchimento na próxima sessão.
- Salva o **número máximo de fotos** configurado pelo usuário.
- Utiliza valores padrão (`""` e `20`) quando não há dados salvos.

---

## ⚙️ Gerenciamento de Estados

### `UiState<T>` — Sealed Class

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

| Estado | Quando ocorre | Feedback Visual |
|---|---|---|
| `Loading` | Carregando histórico do banco | Indicador de progresso circular |
| `Success` | Dados carregados / sessão salva | Conteúdo exibido / mensagem de confirmação |
| `Error` | Falha ao salvar/carregar no banco | Mensagem de erro amigável, sem crash |

### Boas Práticas Aplicadas
- `remember` — estado efêmero de UI (ex: visibilidade de campo).
- `rememberSaveable` — estado que sobrevive a mudanças de configuração (rotação de tela).
- `StateFlow` — fluxo de estado reativo do ViewModel para a UI.
- `Flow` — fluxo de dados reativos do banco de dados (Room) para o ViewModel.
- `viewModelScope.launch` — operações assíncronas com `try/catch` para tratamento de erros.

---

## 🛠️ Tecnologias e Bibliotecas

| Tecnologia | Uso |
|---|---|
| [Kotlin](https://kotlinlang.org/) | Linguagem principal |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3) | Interface declarativa |
| [CameraX 1.4.1](https://developer.android.com/training/camerax) | Preview e captura de imagem |
| [Room](https://developer.android.com/training/data-storage/room) | Banco de dados SQLite local |
| [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences) | Configurações e preferências simples |
| [KSP](https://github.com/google/ksp) | Processador de anotações para Room |
| Kotlin Coroutines + `StateFlow` / `Flow` | Programação assíncrona e reativa |
| Android `LocationManager` | Obtenção de GPS |
| `Crossfade` (Compose Animation) | Transições suaves entre telas |
| `Canvas` (Compose) | Ícones customizados e overlay de grade |
| `lifecycle-viewmodel-compose` | Integração ViewModel com Compose |

---

## 📋 Requisitos de Sistema

- **Android Studio**: Ladybug / Jellyfish ou mais recente
- **JDK**: 11 ou superior
- **Compile SDK**: 37
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Dispositivo**: Aparelho físico com câmera e GPS (ou emulador com suporte a câmera)

---

## 🔒 Permissões do Android

| Permissão | Motivo |
|---|---|
| `android.permission.CAMERA` | Abertura e captura via CameraX |
| `android.permission.ACCESS_FINE_LOCATION` | Localização precisa via GPS |
| `android.permission.ACCESS_COARSE_LOCATION` | Localização aproximada via rede |

---

## ✅ Checklist Sprint 4

- [ ] Estrutura organizada em `ui/`, `domain/`, `data/`, `navigation/`
- [ ] Navegação funcionando entre todas as telas
- [ ] Persistência local com Room implementada
- [ ] SharedPreferences para dados simples implementadas
- [ ] Repositório de dados configurado
- [ ] `UiState` (Loading / Success / Error) aplicado
- [ ] Tratamento de erros sem crashes
- [ ] Dados preservados após reinicialização do app
- [ ] Histórico de sessões funcionando
- [ ] `rememberSaveable` utilizado adequadamente
- [ ] Código organizado e passível de defesa técnica

---

## 🚀 Como Executar o Projeto

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/mateusMMagalhaes/SPRINT3_ANDROIDSTUDIO.git
   ```

2. **Abrir no Android Studio**:
   - Selecione **Open** e navegue até a pasta `simv/`.

3. **Sincronizar o Gradle**:
   - Aguarde o término da sincronização (*Gradle Sync*).
   - As dependências do Room e KSP serão baixadas automaticamente.

4. **Executar o Aplicativo**:
   - Conecte um dispositivo Android via USB ou inicie um emulador.
   - Clique em **Run** (`Shift + F10`).

---

## 📝 Licença

Este projeto foi desenvolvido como parte de atividades acadêmicas — Sprint 4 (Persistência, Arquitetura Desacoplada e MVP Completo). Todos os direitos reservados aos autores.