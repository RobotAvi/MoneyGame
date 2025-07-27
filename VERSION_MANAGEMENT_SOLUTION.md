# Решение проблемы с BuildConfig

## Проблема
Ошибка `Unresolved reference: BuildConfig` возникала из-за изменений в Android Gradle Plugin (AGP). В старых версиях AGP класс `BuildConfig` генерировался автоматически, но в новых версиях это поведение изменилось.

## Анализ истории
1. **До коммита `2454960`**: `BuildConfig` генерировался автоматически
2. **В коммите `2454960`**: Был добавлен `buildConfig true`, что изменило поведение
3. **После этого**: `BuildConfig` стал недоступен в CI/CD среде

## Новое решение

### Принцип работы
Вместо сложной системы с `BuildConfig` используем простые константы в `MainActivity.kt`:

```kotlin
companion object {
    private const val APP_VERSION = "1.83"
    private const val BUILD_NUMBER = "247" // Номер текущего PR
}
```

### Автоматическое обновление версии
Создан скрипт `update_version.sh` для автоматического обновления версии:

```bash
./update_version.sh <version> <build_number>
```

**Пример:**
```bash
./update_version.sh 1.83 247
```

### Что обновляется
1. **`app/build.gradle`**:
   - `versionCode` (автоматически увеличивается)
   - `versionName`

2. **`app/src/main/java/com/financialsuccess/game/MainActivity.kt`**:
   - `APP_VERSION`
   - `BUILD_NUMBER`

### Отображение версии
В главном окне приложения отображается:
```
Версия 1.83 (build 247)
```

## Преимущества нового решения

✅ **Простота**: Нет зависимости от `BuildConfig`  
✅ **Надежность**: Работает во всех средах (локальная, CI/CD)  
✅ **Автоматизация**: Скрипт для обновления версии  
✅ **Прозрачность**: Версия отображается в главном окне  
✅ **Гибкость**: Легко изменить формат отображения  

## Использование в workflow

В GitHub Actions можно добавить:

```yaml
- name: Update version
  run: |
    ./update_version.sh ${{ github.event.number }} ${{ github.run_number }}
```

## Заключение

Новое решение полностью устраняет проблему с `BuildConfig` и предоставляет простую, надежную систему версионирования с автоматическим обновлением.