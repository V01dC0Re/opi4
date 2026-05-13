# Отчет по профилированию HttpUnit

## Объект исследования

Исследовалась программа из архива `HttpUnit.tar.gz`, распакованная в каталог `HttpUnit`.

Проект является библиотекой для тестирования HTTP- и servlet-приложений. Поэтому исправление должно ускорять не только демонстрационный `Main`, а любые проекты, которые получают ответы через `WebClient`, `ServletUnitClient` и `WebResponse`.

## Описание выявленных проблем

### 1. Лишняя предварительная загрузка всех `text/*` ответов

Проблема была найдена в классах:

- `com.meterware.httpunit.HttpWebResponse`;
- `com.meterware.servletunit.ServletUnitWebResponse`.

До исправления библиотека заранее читала тело любого текстового ответа:

```java
if (getContentType().startsWith( "text" )) loadResponseText();
```

Это универсальная проблема: проект-потребитель мог получить `text/plain`, `text/css`, `text/javascript` или другой текстовый ответ и использовать только статус или заголовки, но HttpUnit все равно читал тело ответа, сканировал его на служебные HTML-теги и создавал строковое представление.

В профилировщике это проявлялось как лишняя работа в стеке:

- `HttpWebResponse.<init>` или `ServletUnitWebResponse.<init>`;
- `WebResponse.loadResponseText`;
- `WebResponse.readFromStream`;
- `WebResponse.readTags`.

### 2. Искусственное ожидание при чтении потоков без известной длины

Проблема была найдена в `com.meterware.httpunit.WebResponse`.

В исходном коде для потока с неизвестной длиной использовался `InputStream.available()` и ожидание:

```java
try { Thread.sleep( UNKNOWN_LENGTH_RETRY_INTERVAL ); } catch (InterruptedException e) {}
available = inputStream.available();
```

Такой подход плохо подходит для универсальной библиотеки. `available()` не обязан показывать полный размер данных, а периодический `Thread.sleep` добавляет задержку в сценариях, где поток можно просто читать до EOF.

В профилировщике это локализуется по стеку:

- `WebResponse.readFromStream`;
- `WebResponse.getAvailableBytes`;
- `Thread.sleep`.

### 3. Дорогое строковое представление `DefaultWebResponse`

В `DefaultWebResponse.toString()` использовался `getText()`:

```java
return "DefaultWebResponse [" + getText() + "]";
```

Для библиотеки это опасный контракт: простой лог `response.toString()` может внезапно привести к чтению или формированию полного тела ответа. На маленьком примере это почти незаметно, но на больших ответах или массовых тестах такой вызов увеличивает время и объем создаваемых строк.

### 4. Демонстрационный сценарий не позволял корректно сравнивать результаты

В `src/Main.java` был бесконечный цикл:

```java
while (true) {
    WebResponse response = sc.getResponse(request);
    System.out.println("Count: " + number++ + response);
    java.lang.Thread.sleep(200);
}
```

Эта часть не является универсальным исправлением библиотеки, но мешала проверке: сценарий нельзя было повторяемо завершить и сравнить до/после на одинаковом числе запросов.

## Описание устранения проблем

### Ленивое чтение не-HTML текстовых ответов

В `HttpWebResponse` и `ServletUnitWebResponse` условие изменено:

```java
if (isHTML()) loadResponseText();
```

Теперь HTML-страницы по-прежнему загружаются заранее, потому что они нужны для обработки frames, meta refresh и JavaScript. Остальные `text/*` ответы читаются только тогда, когда пользователь библиотеки явно вызывает `getText()`, `getInputStream()` или методы DOM.

Это универсальное улучшение для любых проектов, где HttpUnit используется для проверки API, сервлетов, CSS, JS, plain text endpoints или заголовков ответа.

### Чтение потока до EOF без искусственного ожидания

В `WebResponse.readFromStream` удалена логика с `available()` и `Thread.sleep`. Поток с неизвестной длиной теперь читается стандартным способом:

```java
count = inputStream.read( buffer, 0, buffer.length );
while (count != -1) {
    outputStream.write( buffer, 0, count );
    count = inputStream.read( buffer, 0, buffer.length );
}
```

Это убирает искусственную задержку и делает поведение ближе к обычному чтению `InputStream`.

### Безопасный `toString()` для `DefaultWebResponse`

`DefaultWebResponse.toString()` больше не возвращает весь текст ответа. Теперь он печатает краткую диагностическую информацию:

```java
return "DefaultWebResponse [url=" + getURL() +
       "; responseCode=" + getResponseCode() +
       "; contentType=" + getContentType() +
       "; contentLength=" + getContentLength() + "]";
```

Если проект-потребитель логирует объект ответа, логирование больше не должно превращаться в скрытую обработку всего тела.

### Повторяемый демонстрационный сценарий

`Main` переведен на конечный benchmark-сценарий:

```java
for (int number = 1; number <= requestCount; number++) {
    WebResponse response = sc.getResponse(request);
    if (number == 1 || number == requestCount) {
        System.out.println("Count: " + number + ", status: " + response.getResponseCode());
    }
}
```

Количество запросов можно передать аргументом командной строки. По умолчанию выполняется 100 запросов.

В `HelloWorld` также исправлен некорректный JavaScript:

```javascript
document.write('Hello Document')
```

## Алгоритм локализации в VisualVM

1. Открыть проект `HttpUnit`.
2. Собрать проект.
3. Запустить старую версию `Main` с бесконечным циклом.
4. Открыть VisualVM.
5. В `Local Applications` выбрать процесс `Main`.
6. На вкладке `Monitor` зафиксировать, что процесс работает постоянно и не завершает сценарий сам.
7. Перейти на вкладку `Sampler`.
8. Нажать `CPU`.
9. Подождать 20-30 секунд.
10. Остановить семплирование.
11. В `Hot Spots` и `Call Tree` найти стек `Main.main -> ServletUnitClient.getResponse -> ServletUnitWebResponse.<init> -> WebResponse.loadResponseText`.
12. Для сценариев с ответами без известной длины найти `WebResponse.readFromStream -> getAvailableBytes -> Thread.sleep`.
13. Повторить те же действия после исправления.
14. Сравнить CPU sample count и wall-clock время одинакового числа запросов.

Скриншоты для отчета:

![VisualVM Monitor](screenshots/visualvm-monitor.png)

![VisualVM CPU до исправления](screenshots/visualvm-cpu-before.png)

![VisualVM CPU после исправления](screenshots/visualvm-cpu-after.png)

## Алгоритм локализации в профилировщике IDE

Можно использовать NetBeans Profiler, IntelliJ IDEA Profiler или Eclipse TPTP/профилировщик JVM.

1. Открыть проект `HttpUnit` в IDE.
2. Настроить запуск главного класса `Main`.
3. Для версии до исправления запустить CPU profiling.
4. Дать программе выполнить нагрузку 20-30 секунд.
5. Остановить профилирование.
6. В `Call Tree` или `Flame Graph` найти `WebResponse.loadResponseText`, `WebResponse.readTags`, `PrintStream.println`.
7. Для проверки универсальной части добавить отдельный тестовый servlet/endpoint, который возвращает `text/plain` или `text/css`, и в клиенте читать только `getResponseCode()`.
8. До исправления в профиле будет видна предварительная загрузка тела ответа.
9. После исправления при чтении только статуса не должно быть вызова `loadResponseText` для не-HTML ответа.

Скриншоты для отчета:

![IDE Profiler до исправления](screenshots/ide-profiler-before.png)

![IDE Profiler после исправления](screenshots/ide-profiler-after.png)

## Результат

Исправления перенесены в общие классы библиотеки:

- `WebResponse`;
- `HttpWebResponse`;
- `ServletUnitWebResponse`.

За счет этого улучшение применяется не только к демонстрационному `Main`, но и к любому проекту, который использует HttpUnit для получения HTTP-ответов.

Ожидаемый эффект:

- меньше предварительной работы для `text/plain`, `text/css`, `text/javascript` и других не-HTML ответов;
- отсутствие искусственных задержек `Thread.sleep` при чтении потоков неизвестной длины;
- более безопасное логирование `DefaultWebResponse`;
- повторяемый benchmark-сценарий для сравнения до/после.

## Вывод

Проблемы производительности были локализованы в универсальном слое обработки ответов HttpUnit. Основная причина заключалась в том, что библиотека выполняла лишнюю работу заранее: читала текстовые ответы, сканировала их как потенциальный HTML и могла задерживаться на чтении потоков неизвестной длины.

После исправления обработка стала ленивой для не-HTML ответов, чтение потоков стало прямым, а демонстрационная программа получила конечный сценарий для воспроизводимых замеров.
