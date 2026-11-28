# Русские релизы

Ветка `feat/russian-localization` содержит русскую версию. `main` отслеживает оригинал; в ней расположен только дополнительный workflow ручного запуска RU Release.

## Однократная настройка подписи

Используйте существующий постоянный ключ, если он уже есть. Иначе на доверенной машине с JDK 17 создайте новый. Не добавляйте ключ или пароли в Git и не публикуйте их в логах.

```bash
umask 077
keytool -genkeypair -keystore lagrange-ru.jks -storetype JKS \
  -alias lagrange-ru -keyalg RSA -keysize 4096 -validity 10000
keytool -list -v -keystore lagrange-ru.jks -alias lagrange-ru
```

Сохраните резервную копию ключа и паролей вне репозитория. Используйте пароли без обратных слешей и переводов строк (формат Java Properties).

В Settings → Secrets and variables → Actions добавьте четыре repository secrets:

- RU_RELEASE_KEYSTORE_BASE64 — содержимое `base64 -w0 lagrange-ru.jks` (Linux).
- RU_RELEASE_STORE_PASSWORD
- RU_RELEASE_KEY_ALIAS — например, lagrange-ru.
- RU_RELEASE_KEY_PASSWORD

В Variables добавьте RU_RELEASE_SIGNER_SHA256 — SHA-256 сертификата из keytool (двоеточия допустимы). Не заменяйте отпечаток при последующих релизах без процедуры миграции подписи.

## Выпуск

После прохождения Debug CI откройте Actions → Android RU Release → Run workflow (main).
Он извлекает исходники из feat/russian-localization, проверяет тесты и подпись и публикует релиз по версии из Gradle.
Альтернативно из проверенного коммита русской ветки:

```bash
git tag v2.2.0-ru.1
git push origin v2.2.0-ru.1
```

Для следующего выпуска увеличьте versionCode, задайте versionName (например, 2.2.0-ru.2), подготовьте docs/release-notes/v2.2.0-ru.2.md. Не переиспользуйте теги. Не смешивайте debug и release APK: их подписи отличаются.

Подписанный APK содержит отпечаток RU-сертификата, переданный из переменной Actions. Без настроенного ключа релиз не публикуется. Старый debug APK может потребовать однократного удаления перед первой подписанной установкой; это удаляет локальные данные.
