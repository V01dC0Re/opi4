# План тестирования производительности

## 1. Подготовка окружения

1. Установить JDK 8 или новее. Нужен именно JDK, не только JRE, потому что проект надо пересобрать.
2. Установить Apache Ant или открыть проект в NetBeans как Ant-проект.
3. Установить VisualVM.
4. Убедиться, что команды доступны:

```bash
java -version
javac -version
ant -version
```

5. Открыть каталог проекта:

```bash
cd C:\Users\John\ITMO\opi4\HttpUnit
```

## 2. Сборка

Вариант через Ant:

```bash
ant clean jar
```

Вариант через NetBeans:

1. `File -> Open Project`.
2. Выбрать папку `HttpUnit`.
3. `Clean and Build Project`.

Ожидаемый результат: в папке `dist` появляется `HttpUnit.jar`.

## 3. Функциональная проверка

Запустить демонстрационный сценарий:

```bash
java -Xmx12m -cp "build/classes;lib/*" Main 100
```

Ожидаемый вывод:

```text
Count: 1, status: 200
Count: 100, status: 200
Processed 100 requests in <N> ms
```

Проверить сценарий на большем числе запросов:

```bash
java -Xmx12m -cp "build/classes;lib/*" Main 1000
```

Программа должна завершиться сама.

## 4. Проверка универсального исправления для не-HTML ответов

Создать временный servlet или изменить `HelloWorld` для отдельного запуска так, чтобы он возвращал `text/plain`:

```java
response.setContentType("text/plain");
out.println("plain response");
```

В клиентском коде читать только статус:

```java
WebResponse response = sc.getResponse(request);
response.getResponseCode();
```

Профиль до исправления:

- в стеке будет `ServletUnitWebResponse.<init>`;
- ниже будет `WebResponse.loadResponseText`;
- ниже будет `WebResponse.readTags`.

Профиль после исправления:

- при чтении только статуса для `text/plain` не должно быть `loadResponseText`;
- тело должно читаться только при явном вызове `response.getText()`.

## 5. Проверка чтения потоков неизвестной длины

Подготовить endpoint или тестовый `URLConnection`, который возвращает gzip-ответ без `Content-Length`.

Профиль до исправления:

- `WebResponse.readFromStream`;
- `WebResponse.getAvailableBytes`;
- `Thread.sleep`.

Профиль после исправления:

- `getAvailableBytes` отсутствует;
- `Thread.sleep` из чтения ответа отсутствует;
- поток читается через повторяющиеся вызовы `InputStream.read`.

## 6. VisualVM: замер до и после

1. Собрать старую версию проекта.
2. Запустить:

```bash
java -Xmx12m -cp "build/classes;lib/*" Main 1000
```

3. Открыть VisualVM.
4. Выбрать процесс `Main`.
5. Открыть вкладку `Monitor`.
6. Сделать скриншот `docs/screenshots/visualvm-monitor.png`.
7. Открыть вкладку `Sampler`.
8. Нажать `CPU`.
9. Дождаться окончания сценария или остановить через 20-30 секунд для старой бесконечной версии.
10. Сделать скриншот `docs/screenshots/visualvm-cpu-before.png`.
11. Повторить пункты 1-9 после исправления.
12. Сделать скриншот `docs/screenshots/visualvm-cpu-after.png`.

Что сравнивать:

- наличие `WebResponse.loadResponseText` для не-HTML ответов;
- наличие `Thread.sleep` в чтении ответа;
- долю `PrintStream.println`;
- общее время выполнения `Processed 1000 requests in <N> ms`.

## 7. IDE Profiler: замер до и после

### NetBeans

1. Открыть `HttpUnit` как проект.
2. Выбрать `Profile Project`.
3. Выбрать CPU profiling.
4. Запустить старую версию.
5. Остановить профилирование после нагрузки.
6. Сохранить скриншот `docs/screenshots/ide-profiler-before.png`.
7. Повторить после исправления.
8. Сохранить скриншот `docs/screenshots/ide-profiler-after.png`.

### IntelliJ IDEA

1. Импортировать проект из папки `HttpUnit`.
2. Создать Run Configuration для `Main`.
3. Указать classpath: `build/classes` и все jar из `lib`.
4. Запустить `Run with Profiler`.
5. Выбрать CPU profiling.
6. После завершения сценария открыть `Call Tree` или `Flame Graph`.
7. Сравнить старую и новую версии по тем же признакам.

## 8. Критерии успешности

Исправление считается подтвержденным, если:

- проект собирается;
- `Main 100` и `Main 1000` завершаются сами;
- HTML-ответы продолжают обрабатываться корректно;
- для `text/plain` при чтении только статуса не вызывается `WebResponse.loadResponseText`;
- в профиле чтения ответа исчезает `WebResponse.getAvailableBytes` и связанный с ним `Thread.sleep`;
- в отчете приложены скриншоты VisualVM и IDE Profiler до/после.
