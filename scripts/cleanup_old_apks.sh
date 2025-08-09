#!/bin/bash

# Скрипт для очистки старых APK файлов
# Оставляет только указанное количество последних версий

set -e

# Параметры
KEEP_COUNT=${1:-3}
DEBUG_DIR="releases/debug"
APK_PATTERN="FinancialSuccess-v*.apk"

echo "🧹 ==== ОЧИСТКА СТАРЫХ APK ФАЙЛОВ ==="
echo "🧹 Директория: $DEBUG_DIR"
echo "🧹 Оставляем последние $KEEP_COUNT APK файлов"
echo "🧹 Паттерн: $APK_PATTERN"

# Проверяем существование директории
if [ ! -d "$DEBUG_DIR" ]; then
    echo "❌ Директория $DEBUG_DIR не существует"
    exit 1
fi

# Подсчитываем количество APK файлов
APK_COUNT=$(find "$DEBUG_DIR" -name "$APK_PATTERN" | wc -l)
echo "🧹 Найдено APK файлов: $APK_COUNT"

if [ $APK_COUNT -le $KEEP_COUNT ]; then
    echo "🧹 Количество APK файлов в пределах нормы (≤$KEEP_COUNT), очистка не требуется"
    echo "🧹 Текущие APK файлы:"
    ls -la "$DEBUG_DIR"/$APK_PATTERN 2>/dev/null || echo "Нет APK файлов"
    exit 0
fi

echo "🧹 Удаляем старые версии (оставляем только $KEEP_COUNT последние)..."

# Получаем список всех APK файлов, сортированных по времени модификации (новые первыми)
# И удаляем все, кроме первых KEEP_COUNT
OLD_FILES=$(find "$DEBUG_DIR" -name "$APK_PATTERN" -printf '%T@ %p\n' | \
sort -nr | \
tail -n +$((KEEP_COUNT + 1)) | \
cut -d' ' -f2-)

if [ ! -z "$OLD_FILES" ]; then
    echo "🧹 Файлы для удаления:"
    echo "$OLD_FILES" | while read file; do
        echo "🧹 Удаляем: $file"
        rm -f "$file"
    done
    
    echo "🧹 Очистка завершена"
else
    echo "🧹 Нет файлов для удаления"
fi

echo "🧹 Оставшиеся APK файлы:"
ls -la "$DEBUG_DIR"/$APK_PATTERN 2>/dev/null || echo "Нет APK файлов"

echo "✅ Очистка завершена успешно!"
