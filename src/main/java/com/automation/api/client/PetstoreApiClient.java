package com.automation.api.client;

import com.automation.config.PlaywrightManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

/**
 * Client wrapping Playwright's APIRequestContext for the Petstore API.
 * All methods return the raw APIResponse so step definitions can assert
 * both status codes and response bodies.
 */
public class PetstoreApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private APIRequestContext requestContext() {
        return PlaywrightManager.getApiContext();
    }

    /**
     * POST /pet – add a new pet to the store.
     */
    public APIResponse addPet(Object petPayload) {
        return requestContext().post("pet",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
                        .setData(toJson(petPayload)));
    }

    /**
     * GET /pet/{petId} – retrieve a pet by ID.
     */
    public APIResponse getPetById(long petId) {
        return requestContext().get("pet/" + petId,
                RequestOptions.create()
                        .setHeader("Accept", "application/json"));
    }

    /**
     * PUT /pet – update an existing pet.
     */
    public APIResponse updatePet(Object petPayload) {
        return requestContext().put("pet",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
                        .setData(toJson(petPayload)));
    }

    /**
     * DELETE /pet/{petId} – delete a pet by ID.
     */
    public APIResponse deletePet(long petId) {
        return requestContext().delete("pet/" + petId,
                RequestOptions.create()
                        .setHeader("Accept", "application/json"));
    }

    /**
     * GET /pet/findByStatus – find pets by status.
     * Valid status values: available, pending, sold
     */
    public APIResponse findPetsByStatus(String status) {
        return requestContext().get("pet/findByStatus",
                RequestOptions.create()
                        .setQueryParam("status", status)
                        .setHeader("Accept", "application/json"));
    }

    /**
     * POST /pet using a raw JSON string – for negative testing with malformed payloads.
     */
    public APIResponse addPetRaw(String rawJson) {
        return requestContext().post("pet",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
                        .setData(rawJson));
    }

    // ── Helper ────────────────────────────────────────────────────────────────────

    private String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialise payload to JSON", e);
        }
    }

    public Map<String, Object> parseResponseAsMap(APIResponse response) {
        try {
            return MAPPER.readValue(response.body(),
                    MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response body as map: " + response.text(), e);
        }
    }
}
