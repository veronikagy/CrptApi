package src.java;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrptApi {
  private final HttpClient httpClient;
  private final String apiUrl = "https://ismp.crpt.ru/api/v3/lk/documents/create";
  private final TimeUnit timeUnit;
  private final int requestLimit;
  private final BlockingQueue<Long> requestTimes;
  private final ReentrantLock lock = new ReentrantLock();

  public CrptApi(TimeUnit timeUnit, int requestLimit) {
    this.timeUnit = timeUnit;
    this.requestLimit = requestLimit;
    this.requestTimes = new LinkedBlockingQueue<>(requestLimit);
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  public HttpResponse<String> createDocument(Document document, String signature) throws IOException, InterruptedException {
    lock.lock();
    try {
      long now = System.currentTimeMillis();
      long intervalMillis = timeUnit.toMillis(1);

      while (requestTimes.size() >= requestLimit) {
        long oldestRequestTime = requestTimes.peek();
        if (now - oldestRequestTime > intervalMillis) {
          requestTimes.poll();
        } else {
          long waitTime = intervalMillis - (now - oldestRequestTime);
          Thread.sleep(waitTime);
        }
      }

      requestTimes.add(now);
    } finally {
      lock.unlock();
    }

    String jsonDocument = toJson(document);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .header("Content-Type", "application/json")
        .header("Signature", signature)
        .POST(HttpRequest.BodyPublishers.ofString(jsonDocument))
        .build();

    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private String toJson(Document document) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(document);
  }

  public static class Document {
    // Поля и методы для документа
    public String description;
    public String doc_id;
    public String doc_status;
    public String doc_type = "LP_INTRODUCE_GOODS";
    public boolean importRequest;
    public String owner_inn;
    public String participant_inn;
    public String producer_inn;
    public String production_date;
    public String production_type;
    public Product[] products;
    public String reg_date;
    public String reg_number;

    public static class Product {
      public String certificate_document;
      public String certificate_document_date;
      public String certificate_document_number;
      public String owner_inn;
      public String producer_inn;
      public String production_date;
      public String tnved_code;
      public String uit_code;
      public String uitu_code;
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    CrptApi api = new CrptApi(TimeUnit.SECONDS, 5);
    Document document = new Document();

    HttpResponse<String> response = api.createDocument(document, "signature");
    System.out.println(response.body());
  }
}
