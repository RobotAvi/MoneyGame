# Целевая архитектура (планы)

Ориентир на Чистую архитектуру и современный стек Android:

## Планируемые слои
- Presentation: Jetpack Compose + ViewModel (MVVM)
- Domain: UseCases, чистые сущности
- Data: Репозитории, источники данных (Room, Network)

## Технологии
- DI: Hilt
- Локальное хранилище: Room
- UI: Jetpack Compose
- Навигация: Navigation Compose

## Интеграции (позже)
- Аналитика, пуши, платежи
- Синхронизация с backend

Документ-источник: `goal_architecture.md` (подробные диаграммы C4).