# Pact
* Specification by example
* Testing framework focused on keeping synchronized the consumers and the provider.


## Pact workshop JVM
* https://github.com/DiUS/pact-workshop-jvm
* **Step 1**:
  * start with one consumer (simple HTTP client) and 2 providers (Dropwizard and Spring Boot)
* **Step 2**:
  * Create a client test using Wiremock (not calling the real providers). The test passes...
but
running the client against the real provider fails. Pact to the rescue!
* **Step 3**: write a consumer pact test.
  * Example with Groovy.
  * It uses PactBuilder()
  * It mocks
  * It runs a mock server and generates a Pact file, which includes both the expected request and
   the expected response.
* **Step 4**: verify Pact against provider
  * There are two ways of validating a pact file against a provider.
    * The first is using a build tool (like Gradle) to execute the pact against the running service.
    * The second is to write a pact verification test.
  * First, we need to **publish the pact file** from the consumer project: we run a command to
  copy the .json Pact file to both provider repos.
  * Then, at the provider, we configure some Gradle plugins and run a pactVerify task.
  * At this point, the pactVerify failed: the actual response didn't match the expected response
  included at the Pact file
* **Step 5**: Verify the provider with a test
  * Create a test that starts the provider server (using the class rule), and then execute the
  pact
  requests (defined by the @PactFolder annotation) against the test target.
  * The test fails for the same reason than in step 5: the count does not match and the date is
  wrong.
* **Step 6**: Back to the client we go
  * We fix in the consumer how the date is handled. The test passes now, but not running against
  the provider: we need to fix the provider!
* **Step 7**: Verify the providers again
* **Step 8**: Test for the missing query parameter
  * We add a consumer test for the case where we don't send the date parameter and the case where
   it is invalid.
  * Running the test adds these two new interactions to the Pact file.
* **Step 9**: Verify the provider with the missing/invalid date query parameter
  * We copy the pact to the providers and rerun the provider tests.
  * The tests fail.
* **Step 10**: Update the providers to handle the missing/invalid query parameters
  * We modify the provider to handle the cases where the parameter is missing or invalid. After
  fixing that, we rerun the provider tests and they pass!
* **Step 11**: Provider states
  * If the provider ever returns a count of zero, we will get a division by zero error in our client.
  * This is an important bit of information to add to our contract. Let us start with a consumer
  test for this.
* **Step 12**: provider states for the providers
  * To be able to verify our providers, we need to be able to change the data that the provider returns.
* **Step 13**: Using a Pact Broker
  * We've been publishing our pacts from the consumer project by coping the files over to the provider project, but we can use a Pact Broker to do this instead.
  * Run the pactPublish task, which will publish the pact to the broker
   * Afterwards, you can navigate to the Pact Broker URL and see the published pact against the
   consumer and provider names setup in our consumer test.
  * https://github.com/pact-foundation/pact_broker
  * The Pact Broker is an application for sharing for consumer driven contracts and verification
  results.
  * You can locally install and run a Pact Broker (it requires Ruby/Rails and bundler).
  * You can run it from Docker as well (it requires a database): https://hub.docker
  .com/r/dius/pact-broker/
  * Public test Pact Broker: https://test.pact.dius.com.au/
  * We configure the pact broker for the provider tests as well, updating the reports with the
  results.
  * When a pact is published, a webhook in the Pact Broker kicks off a build of the provider project if the pact content has changed since the previous version.


## Pacts to the rescue!
* https://vimeo.com/185849980
* Test symmetry: between A and B, we use a mock for getting both a collaboration test (A - Mock)
and a
contract test (Mock - B)
* Consumer - Provider
* We write tests for the **Consumer** using a **Mock provider** that the Pact library provides.
  * We configure the mock response to be returned for a specific request.
* When running that Consumer test, Pact creates a Pact file with the expected requests and
expected responses.
* We take that Pact file to the provider (manually or Pact Broker) and we replay the requests
against the provider to see if
 the responses match the expected ones existing in the Pact file.
 * First it will fail. Then, you implement the Provider side, and it should pass (TDD FTW!)
 * Pact Broker offers you auto-generated documentation.
 * Looks like they used it for non-HTTP contracts as well (e.g. messaging).
 * They got:
   * Fast feedback in isolation
   * Early detection
   * Rapid recovery
* When to use Pact
  * Internal APIs
  * Control over both Consumer and Provider (even you are not the one who develops the Consumer,
  but you talk with them in a fluid way)
* When NOT to use Pact
  * Public APIs


## Pact recap:
1. Write your consumer test. Use the Pact mocking server.
2. Pass the test implementing the Consumer.
3. Use the generated Pact file to replay the request against the real provider.
4. You might need to configure Provider states (e.g. a Playlist should exist beforehand) =
arrange = fixture
5. Pass the test implementing the Provider.
6. Integrate Pact Broker in your CI/CD pipeline: publish Pact changes and if they are still
respected by both parts.
  * A pact is published with every consumer build
  * The latest version of that pact is verified with every provider build
  * The verification results are published back to the broker
  * Any change to the pact triggers a provider build
  * The consumer CI generates pact files during the execution of its isolated tests. The provider CI generates verification results during the execution of its isolated tests.
7. You can run a **local API stub** from your Pact file: https://docs.pact.io/getting_started/stubs
  * Successfully tried with `curl -v "http://localhost:8080/provider.json?validDate=2018-10-23T13:07:40.690"`
8. There are CLI tools (e.g. to run a mock server with npm)
  * Interesting can-i-deploy feature
9. What is good/not so good for: https://docs.pact.io/getting_started/what_is_pact_good_for
10. Pact Nirvana: https://docs.pact.io/best_practices/pact_nirvana
11. It allows versioning.
12. Non-HTTP pacts: https://gist.github.com/bethesque/0ee446a9f93db4dd0697
13. You can test GraphQL
14. Protobuf: https://docs.pact.io/faq#i-use-graphql-soap-protobufs-do-i-need-contract-tests


## Pact & Swagger
* https://docs.pact.io/faq#can-i-generate-my-pact-file-from-something-like-swagger
* https://docs.pact.io/faq/convinceme#but-i-use-swagger-openapi
* https://bitbucket.org/atlassian/swagger-mock-validator
* https://bitbucket.org/atlassian/swagger-request-validator/src/29d5354642b8?at=master
    - You can create tests that validate that the Pact file/mock server is in sync with an OAS file.
* https://wilsonmar.github.io/pact/
* https://github.com/pact-foundation/pact-specification/issues/28


## OAuth and Pact
* https://docs.pact.io/faq#how-do-i-test-oauth-or-other-security-headers

## More readings
* https://martinfowler.com/articles/consumerDrivenContracts.html

## Pact / consumer-driven contracts DOUBTS
* What if you have a non-deterministic system? E.g. get the weather forecast for tomorrow.
  * Possible answer: what you verify in your tests maybe does not have to be literally getting
  the same response, but just the presence/absence of something, formats, ranges, etc. (more
  generic assertions)
  * Real answer: https://docs.pact.io/getting_started/matching
* OAuth and Pact?
* Swagger and Pact?


## Talks
* Atlassian: https://www.youtube.com/watch?v=-6x6XBDf9sQ
* https://www.youtube.com/watch?v=nQ0UGY2-YYI
* CorkDev: https://www.youtube.com/watch?v=rHDyvnp5x3w
