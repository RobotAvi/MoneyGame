#!/bin/bash

# Скрипт для автоматического обновления версии в MainActivity.kt
# Использование: ./update_version.sh <version> <build_number>

if [ $# -ne 2 ]; then
    echo "Использование: $0 <version> <build_number>"
    echo "Пример: $0 1.83 247"
    exit 1
fi

VERSION=$1
BUILD_NUMBER=$2

echo "Обновление версии на $VERSION (build $BUILD_NUMBER)..."

# Обновляем build.gradle
sed -i "s/versionCode [0-9]*/versionCode $(($(grep -o 'versionCode [0-9]*' app/build.gradle | grep -o '[0-9]*') + 1))/" app/build.gradle
sed -i "s/versionName \"[^\"]*\"/versionName \"$VERSION\"/" app/build.gradle

# Обновляем MainActivity.kt
sed -i "s/private const val APP_VERSION = \"[^\"]*\"/private const val APP_VERSION = \"$VERSION\"/" app/src/main/java/com/financialsuccess/game/MainActivity.kt
sed -i "s/private const val BUILD_NUMBER = \"[^\"]*\"/private const val BUILD_NUMBER = \"$BUILD_NUMBER\"/" app/src/main/java/com/financialsuccess/game/MainActivity.kt

echo "✅ Версия обновлена успешно!"
echo "📱 APP_VERSION: $VERSION"
echo "🔢 BUILD_NUMBER: $BUILD_NUMBER"