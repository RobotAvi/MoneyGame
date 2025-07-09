# Отчет об исправлении бага: Двойное списание средств

## Описание бага

В приложении «Журнал финансов» была обнаружена критическая ошибка в расчёте баланса после проведения операций с расходами.

### Суть проблемы

- **До операции**: На счету было 8 000 рублей (сумма доходов: «Бонус» + «Начальный капитал»).
- **Операция**: «Непредвиденные расходы» — минус 5 000 рублей.
- **Ожидаемый результат**: После вычета расходов баланс должен составлять 3 000 рублей (8 000 - 5 000 = 3 000).
- **Фактический результат**: В приложении отображался баланс -2 000 рублей.

### Причина бага

Проблема заключалась в **двойном списании средств** в нескольких местах кода:

1. **Прямое списание**: `player.cash -= amount`
2. **Списание через логирование**: `player.logExpense()` → `cash -= amount`

Это приводило к тому, что одна и та же сумма списывалась дважды.

## Исправленные места

### 1. GameActivity.kt - showDoodadEvent() (строки 421-426)
**Было:**
```kotlin
player.cash -= expense
player.logExpense(FinancialCategory.EMERGENCY, expense, expenseReasons.random())
```

**Стало:**
```kotlin
player.logExpense(FinancialCategory.EMERGENCY, expense, expenseReasons.random())
```

### 2. GameActivity.kt - showCharityEvent() (строки 442-443)
**Было:**
```kotlin
player.cash -= donation
player.logExpense(FinancialCategory.CHARITY, donation, "Пожертвование...")
```

**Стало:**
```kotlin
player.logExpense(FinancialCategory.CHARITY, donation, "Пожертвование...")
```

### 3. GameActivity.kt - Налоговая проверка (строки 386-387)
**Было:**
```kotlin
player.cash -= taxAmount
player.logExpense(FinancialCategory.TAXES, taxAmount, "Доплата налогов...")
```

**Стало:**
```kotlin
player.logExpense(FinancialCategory.TAXES, taxAmount, "Доплата налогов...")
```

### 4. GameActivity.kt - showInvestmentDetails() (строки 590-591)
**Было:**
```kotlin
player.cash -= investment.cost
player.logExpense(FinancialCategory.INVESTMENT, investment.cost, "Инвестиция...")
```

**Стало:**
```kotlin
player.logExpense(FinancialCategory.INVESTMENT, investment.cost, "Инвестиция...")
```

### 5. GameActivity.kt - buyHealthcare() (строки 968-970)
**Было:**
```kotlin
player.cash -= cost
player.logExpense(FinancialCategory.EMERGENCY, cost, "Медицинские услуги...")
```

**Стало:**
```kotlin
player.logExpense(FinancialCategory.EMERGENCY, cost, "Медицинские услуги...")
```

### 6. Player.kt - processMonthlyOperations() (строка 235)
**Было:**
```kotlin
// Логируем и списываем все расходы через logExpense()
logExpense(FinancialCategory.FOOD, foodExpenses, "Ежемесячные расходы на еду")
// ... другие logExpense вызовы

// Списываем общие расходы (ВСЕГДА, даже если уходим в минус)
cash -= totalMonthlyExpenses  // ← ДВОЙНОЕ СПИСАНИЕ!
```

**Стало:**
```kotlin
// Логируем и списываем все расходы через logExpense()
logExpense(FinancialCategory.FOOD, foodExpenses, "Ежемесячные расходы на еду")
// ... другие logExpense вызовы
// Убрали дублирующее списание totalMonthlyExpenses
```

## Результат исправления

После исправления всех мест с двойным списанием:

- **Непредвиденные расходы 5 000₽** теперь корректно списывают только 5 000₽
- **Баланс после операции**: 8 000₽ - 5 000₽ = **3 000₽** ✅
- **Ежемесячные расходы** списываются только один раз
- **Все финансовые операции** корректно отражаются в журнале

## Проверка исправления

Для проверки исправления можно:

1. Запустить игру с начальным капиталом 5 000₽
2. Получить бонус 3 000₽ (итого 8 000₽)
3. Попасть на поле "Непредвиденные расходы" (5 000₽)
4. Убедиться, что баланс стал 3 000₽, а не -2 000₽

## Дополнительные улучшения

- Все финансовые операции теперь проходят через единый метод `logExpense()`
- Улучшена консистентность данных в финансовом журнале
- Устранена возможность рассинхронизации между балансом и записями в журнале