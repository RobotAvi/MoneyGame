# Архитектура MoneyGame (C4 Model)

## 1. Контекст (Context)

**MoneyGame** — мобильное Android-приложение для обучения финансовой грамотности.

- Весь код относится к одному контейнеру: **Android-приложение MoneyGame**.

**Схема уровня Контекст:**
```mermaid
flowchart TD
    User["Пользователь"] <--> App["MoneyGame (Android-приложение)"]
    App -->|"ОС Android"| OS["Android OS"]
    App -->|"Google Play (установка, обновления)"| GP["Google Play"]
```

---

## 2. Контейнер (Container)

- **Android-приложение** (единственный контейнер).

**Схема уровня Контейнер:**
```mermaid
flowchart TD
    App["Android-приложение"]
    subgraph App
        UI["UI (экраны, адаптеры)"]
        Logic["Логика игры"]
        Data["Данные, шаблоны"]
        Models["Модели (структура)"]
        Resources["Ресурсы (layout, drawable, values)"]
    end
```

---

## 3. Компоненты (Components) и их классы

### 3.1. UI-компоненты (экраны, интерфейс)
- MainActivity — главный экран
- GameActivity — основной игровой процесс
- CharacterCreationActivity — расширенное создание персонажа
- ProfessionSelectionActivity — выбор профессии и мечты
- RulesActivity — экран с правилами
- **Adapters** (отображение списков):
  - adapters/AssetAdapter.kt
  - adapters/ProfessionAdapter.kt
  - adapters/DreamAdapter.kt
  - (и другие в adapters/)

**Схема UI-компонента:**
```mermaid
flowchart TD
    UI["UI-компоненты"]
    UI --> MainActivity[MainActivity]
    UI --> GameActivity[GameActivity]
    UI --> CharacterCreationActivity[CharacterCreationActivity]
    UI --> ProfessionSelectionActivity[ProfessionSelectionActivity]
    UI --> RulesActivity[RulesActivity]
    UI --> Adapters["Adapters (AssetAdapter, ProfessionAdapter, DreamAdapter, ...)"]
```

### 3.2. Компонент бизнес-логики
- GameManager — управление состоянием игры

**Схема компонента бизнес-логики:**
```mermaid
flowchart TD
    Logic["Бизнес-логика"]
    Logic --> GameManager[GameManager]
```

### 3.3. Компонент моделей (структура данных)
- models/Player.kt — игрок
- models/GameState.kt — состояние игры
- models/Card.kt — карточки, профессии, мечты, активы, пассивы, инвестиции
- models/Asset.kt, models/Liability.kt, models/Investment.kt — отдельные сущности
- models/FinancialEntry.kt — финансовый журнал
- models/ProfessionalRisk.kt — профессиональные риски
- (и другие в models/)

**Схема компонента моделей:**
```mermaid
flowchart TD
    Models["Модели"]
    Models --> Player[Player]
    Models --> GameState[GameState]
    Models --> Card[Card]
    Models --> Asset[Asset]
    Models --> Liability[Liability]
    Models --> Investment[Investment]
    Models --> FinancialEntry[FinancialEntry]
    Models --> ProfessionalRisk[ProfessionalRisk]
```

### 3.4. Компонент данных
- data/GameDataManager.kt — статические игровые данные
- data/FinancialTemplates.kt — шаблоны финансовых профилей

**Схема компонента данных:**
```mermaid
flowchart TD
    Data["Данные"]
    Data --> GameDataManager[GameDataManager]
    Data --> FinancialTemplates[FinancialTemplates]
```

**Общая схема уровня Компоненты (для контекста):**
```mermaid
flowchart TD
    subgraph App["Android-приложение"]
        UI["UI-компоненты"]
        Logic["Бизнес-логика"]
        Models["Модели"]
        Data["Данные"]
    end

    UI --> Logic
    Logic --> Models
    Logic --> Data
    Data --> Models
```

---

## 4. Код (Code) — детализация по файлам

### UI (экраны и адаптеры)
- MainActivity.kt — главный экран, навигация
- GameActivity.kt — игровой процесс, обработка событий, обновление UI
- CharacterCreationActivity.kt — создание персонажа
- ProfessionSelectionActivity.kt — выбор профессии/мечты
- RulesActivity.kt — правила игры
- adapters/AssetAdapter.kt, adapters/ProfessionAdapter.kt, adapters/DreamAdapter.kt — списки в RecyclerView

### Логика и данные
- GameManager.kt — игровая логика, управление состоянием
- data/GameDataManager.kt — статические игровые данные
- data/FinancialTemplates.kt — шаблоны финансовых профилей

### Модели
- models/Player.kt — игрок
- models/GameState.kt — состояние игры
- models/Card.kt — карточки, профессии, мечты, активы, пассивы, инвестиции
- models/Asset.kt, models/Liability.kt, models/Investment.kt — отдельные сущности
- models/FinancialEntry.kt — финансовые операции, журнал
- models/ProfessionalRisk.kt — профессиональные риски
- (и другие в models/)

**Схема уровня Код:**
```mermaid
flowchart TD
    MainActivity[MainActivity.kt] --> CharCreate[CharacterCreationActivity.kt]
    MainActivity --> ProfSel[ProfessionSelectionActivity.kt]
    MainActivity --> Rules[RulesActivity.kt]
    CharCreate --> GameActivity[GameActivity.kt]
    ProfSel --> GameActivity
    GameActivity --> GameManager[GameManager.kt]
    GameActivity --> AssetAdapter[adapters/AssetAdapter.kt]
    GameActivity --> ProfAdapter[adapters/ProfessionAdapter.kt]
    GameActivity --> DreamAdapter[adapters/DreamAdapter.kt]
    GameManager --> Player[models/Player.kt]
    GameManager --> GameState[models/GameState.kt]
    GameManager --> Card[models/Card.kt]
    GameManager --> Asset[models/Asset.kt]
    GameManager --> Liability[models/Liability.kt]
    GameManager --> Investment[models/Investment.kt]
    GameManager --> FinancialEntry[models/FinancialEntry.kt]
    GameManager --> ProfessionalRisk[models/ProfessionalRisk.kt]
    GameManager --> GameDataManager[data/GameDataManager.kt]
    GameManager --> FinancialTemplates[data/FinancialTemplates.kt]
```

---

## Как определить принадлежность любого класса?

- **adapters/** — UI-компонент (адаптер для списков)
- **models/** — компонент моделей (структура данных, бизнес-логика)
- **data/** — компонент данных (статические/пользовательские данные, шаблоны)
- **корень game/ + Activity** — UI-компонент (экран)
- **GameManager.kt** — бизнес-логика (ядро игры)
- **GameDataManager.kt, FinancialTemplates.kt** — компонент данных

**Схема принадлежности по папкам:**
```mermaid
flowchart TD
    src --> adapters
    src --> models
    src --> data
    src --> game
    adapters --> AssetAdapter
    adapters --> ProfessionAdapter
    adapters --> DreamAdapter
    models --> Player
    models --> GameState
    models --> Card
    models --> Asset
    models --> Liability
    models --> Investment
    models --> FinancialEntry
    models --> ProfessionalRisk
    data --> GameDataManager
    data --> FinancialTemplates
    game --> MainActivity
    game --> GameActivity
    game --> CharacterCreationActivity
    game --> ProfessionSelectionActivity
    game --> RulesActivity
    game --> GameManager
```

---

## Пример

- **app/src/main/java/com/financialsuccess/game/models/Player.kt**  
  → Компонент моделей, слой бизнес-логики, отвечает за структуру и методы игрока.
- **app/src/main/java/com/financialsuccess/game/adapters/AssetAdapter.kt**  
  → UI-компонент, отвечает за отображение списка активов.
- **app/src/main/java/com/financialsuccess/game/data/FinancialTemplates.kt**  
  → Компонент данных, отвечает за шаблоны финансовых профилей.

---

Если укажете конкретный класс — скажу его принадлежность и роль максимально подробно!
