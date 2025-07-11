#!/bin/bash

# Скрипт для очистки старых GitHub Actions workflows
# Запускать только после успешного тестирования нового stable-build.yml

echo "🧹 Очистка старых GitHub Actions workflows"
echo "=========================================="

# Список старых workflows для удаления
OLD_WORKFLOWS=(
    "build-with-screenshots.yml"
    "build-with-screenshots-alternative.yml"
    "build-with-screenshots-fixed.yml"
    "build-with-screenshots-optimized.yml"
)

# Проверяем, что новый workflow существует
if [ ! -f ".github/workflows/stable-build.yml" ]; then
    echo "❌ Ошибка: Новый workflow stable-build.yml не найден!"
    echo "Убедитесь, что новый workflow создан и протестирован."
    exit 1
fi

echo "✅ Новый workflow stable-build.yml найден"
echo ""

# Показываем список файлов для удаления
echo "📋 Следующие файлы будут удалены:"
for workflow in "${OLD_WORKFLOWS[@]}"; do
    if [ -f ".github/workflows/$workflow" ]; then
        echo "  - $workflow"
    else
        echo "  - $workflow (не найден)"
    fi
done
echo ""

# Запрашиваем подтверждение
read -p "🤔 Продолжить удаление? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Операция отменена"
    exit 0
fi

# Удаляем старые workflows
echo ""
echo "🗑️  Удаление старых workflows..."
for workflow in "${OLD_WORKFLOWS[@]}"; do
    if [ -f ".github/workflows/$workflow" ]; then
        rm ".github/workflows/$workflow"
        echo "  ✅ Удален: $workflow"
    else
        echo "  ⚠️  Пропущен: $workflow (не найден)"
    fi
done

echo ""
echo "🎉 Очистка завершена!"
echo ""
echo "📋 Оставшиеся workflows:"
ls -la .github/workflows/
echo ""
echo "💡 Теперь у вас есть стабильный workflow:"
echo "   - stable-build.yml (основной)"
echo "   - quick-test.yml (быстрое тестирование)"
echo ""
echo "📚 Документация: .github/README.md"