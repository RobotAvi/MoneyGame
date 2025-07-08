# 🚀 GitHub Actions для автоматической сборки APK

## 📋 Обзор

Настроены 3 автоматических workflow для проекта:

### 🔨 1. Build APK (`build-apk.yml`)
- **Запускается:** При каждом push в `master`/`main`/`develop` и при PR
- **Что делает:** Собирает debug APK и загружает как артефакт
- **Результат:** Готовый APK для тестирования

### 🚀 2. Release (`release.yml`) 
- **Запускается:** При создании тега `v*` (например `v1.0.4`)
- **Что делает:** Собирает release APK и создаёт GitHub Release
- **Результат:** Автоматический релиз с APK файлом

### 🔍 3. Code Quality (`code-quality.yml`)
- **Запускается:** При каждом push и PR
- **Что делает:** Проверяет качество кода, безопасность, зависимости
- **Результат:** Отчёты о качестве кода

## 🎯 Как использовать

### 📦 Получить APK после коммита

1. **Сделайте push в master:**
   ```bash
   git add .
   git commit -m "Мои изменения"
   git push origin master
   ```

2. **Дождитесь завершения сборки** (2-5 минут)

3. **Скачайте APK:**
   - Идите в **Actions** → **Build Android APK** → выберите последний run
   - В разделе **Artifacts** скачайте `FinancialSuccess-Debug-APK`

### 🚀 Создать новый релиз

1. **Создайте и запушьте тег:**
   ```bash
   git tag -a v1.0.4 -m "Описание релиза v1.0.4"
   git push origin v1.0.4
   ```

2. **Автоматически произойдёт:**
   - Сборка release APK
   - Создание GitHub Release
   - Прикрепление APK к релизу
   - Генерация changelog

3. **Результат:** Новый релиз на https://github.com/YOUR_USERNAME/MoneyGame/releases

## 📊 Мониторинг сборок

### ✅ Статусы Actions

Проверяйте статус в нескольких местах:

1. **В репозитории:** Бейджи статуса возле коммитов
2. **Во вкладке Actions:** Подробная информация о сборках  
3. **В PR:** Автоматические комментарии с информацией об APK

### 📱 Получение APK

#### Из Actions (Debug APK):
```
GitHub Repo → Actions → Build Android APK → Последний run → Artifacts → FinancialSuccess-Debug-APK
```

#### Из Releases (Release APK):
```
GitHub Repo → Releases → Последний релиз → Assets → FinancialSuccess-v1.x.x.apk
```

## 🛠️ Конфигурация workflows

### 📁 Структура файлов

```
.github/workflows/
├── build-apk.yml      # Основная сборка APK
├── release.yml        # Создание релизов  
└── code-quality.yml   # Проверка качества кода
```

### ⚙️ Основные настройки

#### Java версия:
```yaml
java-version: '21'
distribution: 'temurin'
```

#### Android SDK:
```yaml
api-level: 34
build-tools: 34.0.0
```

#### Кэширование:
```yaml
path: |
  ~/.gradle/caches
  ~/.gradle/wrapper
```

## 🔧 Настройка в новом репозитории

### 1. Скопируйте файлы workflows:
```bash
mkdir -p .github/workflows
cp .github/workflows/*.yml NEW_REPO/.github/workflows/
```

### 2. Убедитесь в настройках репозитория:
- **Settings** → **Actions** → **General** → **Workflow permissions** → Read and write permissions
- **Settings** → **Actions** → **General** → **Allow GitHub Actions to create and approve pull requests** ✅

### 3. Первый запуск:
```bash
git add .github/
git commit -m "Добавлены GitHub Actions workflows"
git push origin master
```

## 📈 Оптимизация производительности

### ⚡ Ускорение сборки

1. **Кэширование Gradle:**
   - Автоматически настроено во всех workflows
   - Сокращает время сборки с 5 минут до 2-3 минут

2. **Параллельные задачи:**
   - Code quality проверки запускаются параллельно
   - Независимые jobs выполняются одновременно

3. **Условное выполнение:**
   - Некоторые шаги выполняются только при определённых условиях
   - `continue-on-error: true` для неблокирующих проверок

### 📊 Мониторинг размера APK

В каждом workflow автоматически отслеживается:
- Размер собранного APK
- Информация выводится в логи
- APK автоматически прикрепляется к релизам

## 🎛️ Дополнительные возможности

### 🔔 Уведомления

#### Настройка уведомлений о сборках:
1. **Settings** → **Notifications** → **Actions** 
2. Выберите способы уведомлений (email, mobile)
3. Настройте фильтры для важных событий

#### Интеграция с мессенджерами:
Можно добавить уведомления в Slack/Discord:
```yaml
- name: Notify Slack
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### 🏷️ Автоматическое версионирование

#### Семантическое версионирование:
```bash
# Patch версия (1.0.3 → 1.0.4)  
git tag v1.0.4

# Minor версия (1.0.4 → 1.1.0)
git tag v1.1.0  

# Major версия (1.1.0 → 2.0.0)
git tag v2.0.0
```

#### Создание Pre-release:
```bash
git tag v1.1.0-beta.1 
# Создаст pre-release в GitHub
```

### 📱 Подписание APK (опционально)

Для production релизов добавьте подписание:

```yaml
- name: Sign APK
  uses: r0adkll/sign-android-release@v1
  with:
    releaseDirectory: app/build/outputs/apk/release
    signingKeyBase64: ${{ secrets.SIGNING_KEY }}
    alias: ${{ secrets.ALIAS }}
    keyStorePassword: ${{ secrets.KEY_STORE_PASSWORD }}
    keyPassword: ${{ secrets.KEY_PASSWORD }}
```

## 🐛 Troubleshooting

### ❌ Частые проблемы

#### 1. "Permission denied" для gradlew:
**Решение:** Убедитесь что есть шаг `chmod +x gradlew`

#### 2. "SDK not found":
**Решение:** Проверьте версию Android SDK в workflow (должна быть 34)

#### 3. "Out of memory":
**Решение:** Добавьте в `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError
```

#### 4. Workflow не запускается:
**Проверьте:**
- Файлы находятся в `.github/workflows/`
- YAML синтаксис корректен
- У репозитория включены Actions

### 📋 Проверочный список

- [ ] Actions включены в репозитории
- [ ] Файлы workflow в правильной директории  
- [ ] `gradlew` имеет права на выполнение
- [ ] Android SDK версии совпадают с проектом
- [ ] Java версия соответствует проекту (21)
- [ ] Secrets настроены (если используется подписание)

## 📚 Дополнительные ресурсы

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android CI/CD Best Practices](https://developer.android.com/studio/projects/continuous-integration)
- [Gradle Build Cache](https://docs.gradle.org/current/userguide/build_cache.html)

---

## 🎉 Результат

После настройки GitHub Actions у вас будет:

✅ **Автоматическая сборка** APK при каждом коммите  
✅ **Автоматические релизы** при создании тегов  
✅ **Проверка качества кода** в каждом PR  
✅ **Готовые APK файлы** без локальной сборки  
✅ **Профессиональный CI/CD** pipeline

**Время экономии:** 5-10 минут на каждую сборку! 🚀