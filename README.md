Необходимо реализовать на языке Java (можно использовать 17
версию) класс для работы с API Честного знака. Класс должен быть
thread-safe и поддерживать ограничение на количество запросов к
API. Ограничение указывается в конструкторе в виде количества
запросов в определенный интервал времени. Например:
public CrptApi(TimeUnit timeUnit, int requestLimit)
timeUnit – указывает промежуток времени – секунда, минута и пр.
requestLimit – положительное значение, которое определяет
максимальное количество запросов в этом промежутке времени.
При превышении лимита запрос вызов должен блокироваться,
чтобы не превысить максимальное количество запросов к API и
продолжить выполнение, без выбрасывания исключения, когда
ограничение на количество вызов API не будет превышено в
результате этого вызова. В любой ситуации превышать лимит на
количество запросов запрещено для метода.
Реализовать нужно единственный метод – Создание документа для
ввода в оборот товара, произведенного в РФ. Документ и подпись
должны передаваться в метод в виде Java объекта и строки
соответственно.
Вызывается по HTTPS метод POST следующий URL:
https://ismp.crpt.ru/api/v3/lk/documents/create
В теле запроса передается в формате JSON документ: {"description":
{ "participantInn": "string" }, "doc_id": "string", "doc_status": "string",
"doc_type": "LP_INTRODUCE_GOODS", 109 "importRequest": true,
"owner_inn": "string", "participant_inn": "string", "producer_inn":
"string", "production_date": "2020-01-23", "production_type": "string",
"products": [ { "certificate_document": "string",
"certificate_document_date": "2020-01-23",
"certificate_document_number": "string", "owner_inn": "string",
"producer_inn": "string", "production_date": "2020-01-23",
"tnved_code": "string", "uit_code": "string", "uitu_code": "string" } ],
"reg_date": "2020-01-23", "reg_number": "string"}
