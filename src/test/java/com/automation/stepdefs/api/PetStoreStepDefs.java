package com.automation.stepdefs.api;

import com.automation.api.client.PetstoreApiClient;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class PetStoreStepDefs {

    private static final Logger log = LoggerFactory.getLogger(PetStoreStepDefs.class);

    private final PetstoreApiClient apiClient = new PetstoreApiClient();

    private Map<String, Object> currentPet;
    private long currentPetId;
    private APIResponse lastResponse;

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("I have a new pet with name {string} status {string} and category {string}")
    public void iHaveANewPetWithDetails(String name, String status, String categoryName) {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", categoryName);

        currentPet = new HashMap<>();
        currentPet.put("name", name);
        currentPet.put("status", status);
        currentPet.put("category", category);
        currentPet.put("photoUrls", List.of("https://example.com/photo.jpg"));
        log.info("Built pet payload: {}", currentPet);
    }

    @Given("I have a pet ID of {long}")
    public void iHaveAPetIdOf(long petId) {
        currentPetId = petId;
        log.info("Using pet ID: {}", petId);
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("I send a POST request to add the pet")
    public void iSendAPostRequestToAddThePet() {
        lastResponse = apiClient.addPet(currentPet);
        log.info("POST /pet → HTTP {}", lastResponse.status());
        log.debug("Response body: {}", lastResponse.text());
    }

    @When("I send a GET request to retrieve the pet")
    public void iSendAGetRequestToRetrieveThePet() {
        lastResponse = apiClient.getPetById(currentPetId);
        log.info("GET /pet/{} → HTTP {}", currentPetId, lastResponse.status());
        log.debug("Response body: {}", lastResponse.text());
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the API response status code should be {int}")
    public void theApiResponseStatusCodeShouldBe(int expectedStatus) {
        assertEquals(lastResponse.status(), expectedStatus,
                String.format("Expected HTTP %d but got %d. Body: %s",
                        expectedStatus, lastResponse.status(), lastResponse.text()));
    }

    @And("the response body should contain pet name {string}")
    public void theResponseBodyShouldContainPetName(String expectedName) {
        Map<String, Object> body = apiClient.parseResponseAsMap(lastResponse);
        String actualName = (String) body.get("name");
        assertEquals(actualName, expectedName,
                "Pet name mismatch in response body");
    }

    @And("the response body should contain status {string}")
    public void theResponseBodyShouldContainStatus(String expectedStatus) {
        Map<String, Object> body = apiClient.parseResponseAsMap(lastResponse);
        String actualStatus = (String) body.get("status");
        assertEquals(actualStatus, expectedStatus,
                "Pet status mismatch in response body");
    }

    @And("the response body should contain an error message {string}")
    public void theResponseBodyShouldContainAnErrorMessage(String expectedMessage) {
        String responseText = lastResponse.text();
        log.debug("Error response body: {}", responseText);
        assertTrue(responseText.contains(expectedMessage),
                String.format("Expected error message '%s' not found in: %s",
                        expectedMessage, responseText));
    }
}
