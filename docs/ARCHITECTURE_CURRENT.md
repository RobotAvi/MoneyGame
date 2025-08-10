# Текущая архитектура приложения

Проект использует классические Android-компоненты и разделение ответственности:

## Слои и компоненты
- UI: Activities, RecyclerView, ViewBinding
- Логика: `GameManager`, модели в `models/`
- Данные: `GameDataManager` (статические данные), `GameSaveManager` (JSON в filesDir)

## Структура кода (упрощенно)
```
app/src/main/java/com/financialsuccess/game/
├── models/ (Player, GameState, FinancialEntry, Asset, ProfessionalRisk, ...)
├── data/ (GameDataManager, GameSaveManager)
├── adapters/ (AssetAdapter, FinancialJournalAdapter, ...)
├── animation/ (MainScreenAnimationManager)
├── GameActivity.kt
├── MainActivity.kt
├── CharacterCreationActivity.kt
├── ProfessionSelectionActivity.kt
└── GameManager.kt
```

## Ключевые зависимости
- AndroidX AppCompat, Material, RecyclerView, Navigation
- Kotlin 1.9.22, AGP 8.12.0

## Что планируется (не внедрено)
- ViewModel/MVVM, DI (Hilt), Room, Jetpack Compose