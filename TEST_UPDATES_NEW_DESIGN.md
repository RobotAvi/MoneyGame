# Обновление тестов под новый дизайн

## Обзор изменений

Все тесты были обновлены для работы с новым дизайном приложения, который включает:
- Зелёную цветовую схему
- Material Design компоненты
- Карточки с тенями и закруглёнными углами
- Новую структуру игрового экрана
- Обновлённые элементы UI

## Обновлённые файлы тестов

### 1. Unit тесты (`app/src/test/`)

#### `GameActivityTest.kt`
**Удалённые тесты:**
- `testMonthProgressBarColors()` - старый прогресс-бар больше не используется
- `testPlayerIconPositionMatchesMonthProgress()` - старая система позиционирования

**Добавленные тесты:**
- `testNewDesignTopPanelElements()` - проверка верхней панели с показателями
- `testDiceCardDisplay()` - проверка карточки кубика
- `testActionButtonsDisplay()` - проверка кнопок действий
- `testGameStatusDisplay()` - проверка статуса игры
- `testRecyclerViewAssetsDisplay()` - проверка списка активов
- `testNewDesignColorScheme()` - проверка цветовой схемы
- `testCardElevationAndCornerRadius()` - проверка теней и закруглений
- `testNewDesignLayoutStructure()` - проверка структуры layout
- `testNewDesignTextContent()` - проверка текстового контента
- `testNewDesignButtonText()` - проверка текста кнопок
- `testNewDesignVisibilityStates()` - проверка состояний видимости

### 2. UI тесты (`app/src/androidTest/`)

#### `CharacterCreationActivityTest.kt`
**Обновлённые тесты:**
- Удалены тесты старых элементов UI
- Добавлены тесты для нового дизайна карточек
- Обновлены проверки стилей кнопок и полей ввода

**Добавленные тесты:**
- `testNewDesignCardLayout()` - проверка layout карточек
- `testNewDesignButtonStyle()` - проверка стиля кнопок
- `testNewDesignTextInputStyle()` - проверка стиля полей ввода
- `testNewDesignColorScheme()` - проверка цветовой схемы
- `testNewDesignLayoutStructure()` - проверка структуры layout
- `testNewDesignAccessibility()` - проверка доступности
- `testNewDesignResponsiveLayout()` - проверка адаптивности

#### `NewDesignTest.kt` (новый файл)
**Специальные тесты для нового дизайна:**
- `testMainScreenNewDesign()` - тесты главного экрана
- `testCharacterCreationNewDesign()` - тесты экрана создания персонажа
- `testGameScreenNewDesign()` - тесты игрового экрана
- `testGameScreenTopPanel()` - тесты верхней панели
- `testGameScreenCenterArea()` - тесты центральной области
- `testGameScreenBottomPanel()` - тесты нижней панели
- `testDiceInteraction()` - тесты взаимодействия с кубиком
- `testNavigationButtons()` - тесты навигации
- `testNewDesignColorScheme()` - тесты цветовой схемы
- `testNewDesignAccessibility()` - тесты доступности

### 3. Maestro тесты (`maestro/`)

#### `screenshots.yaml`
**Обновления:**
- Обновлены названия скриншотов для нового дизайна
- Добавлены проверки новых элементов UI
- Обновлены селекторы для новых элементов
- Добавлены тесты для проверки статистики и цветовой схемы

#### `screenshots-minimal.yaml`
**Обновления:**
- Упрощены тесты для минимального набора проверок
- Обновлены под новый дизайн

#### `screenshots-stable.yaml`
**Обновления:**
- Добавлены стабильные проверки нового дизайна
- Обновлены тесты навигации
- Добавлены проверки основных элементов

#### `screenshots-tablet.yaml`
**Обновления:**
- Обновлены тесты для планшетов
- Добавлены проверки адаптивности нового дизайна

## Новые элементы для тестирования

### Игровой экран
1. **Верхняя панель:**
   - `tv_cash` - наличные
   - `tv_salary` - зарплата
   - `tv_passive_income` - пассивный доход
   - `tv_level` - уровень

2. **Центральная область:**
   - `card_dice` - карточка кубика
   - `iv_dice` - изображение кубика
   - `tv_dice_value` - значение кубика
   - `tv_game_status` - статус игры

3. **Нижняя панель:**
   - `btn_financial_statement` - кнопка финансов
   - `btn_assets` - кнопка активов
   - `btn_market` - кнопка рынка

4. **Скрытые элементы:**
   - `recyclerViewAssets` - список активов (скрыт по умолчанию)

### Экран создания персонажа
- Обновлённые поля ввода с новым стилем
- Карточки для профессий и мечт
- Обновлённая кнопка старта

## Цветовая схема

Новый дизайн использует:
- **Основной зелёный:** `#4CAF50`
- **Светло-зелёный:** `#81C784`
- **Тёмно-зелёный:** `#388E3C`
- **Светло-серый фон:** `#F5F5F5`
- **Белый:** `#FFFFFF`

## Запуск тестов

### Unit тесты
```bash
./gradlew test
```

### UI тесты
```bash
./gradlew connectedAndroidTest
```

### Maestro тесты
```bash
maestro test maestro/screenshots.yaml
maestro test maestro/screenshots-minimal.yaml
maestro test maestro/screenshots-stable.yaml
maestro test maestro/screenshots-tablet.yaml
```

## Известные ограничения

1. **Цветовые тесты:** Некоторые тесты цветовой схемы могут быть ограничены возможностями Robolectric
2. **Тени и закругления:** Проверка визуальных эффектов может быть ограничена
3. **Адаптивность:** Полная проверка адаптивности требует тестирования на разных устройствах

## Рекомендации

1. **Регулярное обновление:** Обновляйте тесты при изменении дизайна
2. **Скриншоты:** Используйте Maestro для создания скриншотов нового дизайна
3. **Доступность:** Проверяйте доступность новых элементов
4. **Производительность:** Мониторьте производительность новых UI элементов