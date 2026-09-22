# 🌱 SIMV - Sistema de Monitoramento de Vegetação

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![CameraX](https://img.shields.io/badge/Camera-CameraX%201.4.1-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/training/camerax)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-orange.svg?style=flat)](https://developer.android.com)

**SIMV (Sistema de Monitoramento de Vegetação)** é um aplicativo Android desenvolvido em **Kotlin** e **Jetpack Compose** voltado para o acompanhamento e coleta de registros fotográficos em campo para monitoramento ambiental e de vegetação.

---

## 📱 Funcionalidades

O aplicativo foi projetado com uma interface moderna em *Dark Mode* e navegação contínua entre as etapas da sessão de monitoramento (*Run Session*):

1. **Tela Inicial (Start Screen)**:
   - Apresentação do sistema e botão de acesso rápido para iniciar a sessão de monitoramento.

2. **Configuração da Sessão (Setup Run Screen)**:
   - Entrada de metadados da coleta: **Nome do Projeto**, **Localização** e **ID do Operador**.
   - Integração com GPS do dispositivo: obtenção de coordenadas geográficas (latitude/longitude) via botão interativo.
   - Validação de formulário com feedback em tempo real.

3. **Visão da Câmera (Camera View Screen)**:
   - Integração completa com a **CameraX** (preview em tempo real com suporte a hardware de câmera).
   - Grade de auxílio para enquadramento baseada na **Regra dos Terços** desenhada com Canvas.
   - Efeito visual dinâmico de *flash* ao capturar foto.
   - Contador interativo de fotos capturadas (com limite máximo configurável de até 20 fotos por corrida).
   - Cronômetro ativo em tempo real (`HH:mm:ss`) monitorando o tempo decorrido da sessão.
   - Opção de encerrar a corrida antecipadamente (*Stop Run*).

4. **Conclusão da Sessão (Run Completed Screen)**:
   - Resumo completo da corrida finalizada: contagem total de fotos capturadas e tempo total de duração.
   - Badge de confirmação com ícone de marca de verificação desenhado nativamente via Canvas.
   - Reinicialização de sessão para novos registros.

---

## 🏗️ Arquitetura do Projeto

O projeto adota os padrões **MVVM (Model-View-ViewModel)** e **Clean Architecture**, promovendo separação de responsabilidades, testabilidade e manutenibilidade.

```text
com.example.simv/
├── data/
│   ├── model/
│   │   └── RunSession.kt          # Modelo de dados da sessão de monitoramento
│   └── repository/
│       └── RunRepository.kt       # Gerenciamento de estado e fluxo de dados da sessão
├── domain/
│   └── usecase/
│       └── RunUseCase.kt          # Regras de negócio e formatação de dados (tempo, validação)
├── ui/
│   ├── screens/
│   │   ├── StartScreen.kt         # Tela de boas-vindas
│   │   ├── SetupRunScreen.kt      # Formulario de configuração e GPS
│   │   ├── CameraViewScreen.kt    # Câmera em tempo real, grid overlay e captura
│   │   └── RunCompletedScreen.kt  # Tela de resumo e conclusão
│   ├── theme/
│   │   ├── Color.kt               # Paleta de cores (SimvCyan, SimvDarkBg, etc.)
│   │   ├── Theme.kt               # Configuração do MaterialTheme
│   │   └── Type.kt                # Tipografia do app
│   └── viewmodel/
│       └── RunViewModel.kt        # Gerenciamento de estado da UI, cronômetro e navegação
└── MainActivity.kt                # Ponto de entrada do app com transições Crossfade
```

---

## 🛠️ Tecnologias e Bibliotecas

- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **Interface Gráfica**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Câmera**: [CameraX 1.4.1](https://developer.android.com/training/camerax) (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)
- **Localização**: Android `LocationManager` (GPS e Network Provider)
- **Programação Assíncrona & Fluxos**: Kotlin Coroutines & `StateFlow`
- **Navegação & Animações**: `Crossfade` com especificações de transição suave (`tween`)
- **Desenho Personalizado**: `Canvas` do Compose (Overlay de terços e ícones customizados)

---

## 📋 Requisitos de Sistema

- **Android Studio**: Ladybug / Jellyfish ou mais recente
- **JDK**: 11 ou superior
- **Compile SDK**: 37
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Dispositivo**: Aparelho físico com câmera e GPS habilitado (ou emulador com suporte a câmera)

---

## 🔒 Permissões do Android

O aplicativo requer as seguintes permissões declaradas no `AndroidManifest.xml`:

- `android.permission.CAMERA`: Necessária para abertura e captura de imagens via CameraX.
- `android.permission.ACCESS_FINE_LOCATION`: Obtenção de localização precisa via GPS.
- `android.permission.ACCESS_COARSE_LOCATION`: Obtenção de localização aproximada via rede.

---

## 🚀 Como Executar o Projeto

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/mateusMMagalhaes/SPRINT3_ANDROIDSTUDIO.git
   ```

2. **Abrir no Android Studio**:
   - Abra o Android Studio e selecione **Open**.
   - Navegue até a pasta `simv` do projeto e confirme.

3. **Sincronizar o Gradle**:
   - Aguarde o término da sincronização do Gradle (*Gradle Sync*).

4. **Executar o Aplicativo**:
   - Conecte um dispositivo Android via depuração USB ou inicie um emulador.
   - Clique no botão **Run** (`Shift + F10`) no Android Studio.

---

## 📝 Licença

Este projeto foi desenvolvido como parte de atividades acadêmicas/projeto de monitoramento (Sprint 3). Todos os direitos reservados aos autores.