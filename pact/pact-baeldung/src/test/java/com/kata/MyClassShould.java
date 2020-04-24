package com.kata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;

import au.com.dius.pact.consumer.dsl.PactDslResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.testng.annotations.Test;

@Test
public class MyClassShould {

  @Rule
  public PactProviderRuleMk2 mockProvider =
      new PactProviderRuleMk2("test_provider", "localhost", 8080, this);

  @Pact(consumer = "test_consumer")
  public PactDslResponse createPact(PactDslWithProvider builder) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return builder
        .given("test GET")
        .uponReceiving("GET REQUEST")
        .path("/pact")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(headers)
        .body("{\"condition\": true, \"name\": \"tom\"}");
  }

    public void assert_true() {

        assertThat(true, is(true));
    }
}
