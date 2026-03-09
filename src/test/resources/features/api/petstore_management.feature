@api
Feature: Petstore Pet Management API
  As an API consumer
  I want to manage pets via the Petstore REST API
  So that I can verify CRUD operations behave correctly for valid and invalid inputs

  # ── Implemented scenarios ─────────────────────────────────────────────────────

  @smoke @positive
  Scenario: Successfully add a new pet with valid data
    Given I have a new pet with name "Buddy" status "available" and category "Dogs"
    When I send a POST request to add the pet
    Then the API response status code should be 200
    And the response body should contain pet name "Buddy"
    And the response body should contain status "available"

  @negative
  Scenario: Retrieve a pet with a non-existent ID returns 404
    Given I have a pet ID of 9999999991
    When I send a GET request to retrieve the pet
    Then the API response status code should be 404
    And the response body should contain an error message "Pet not found"

  # ── Additional scenarios (documented, not implemented) ──────────────────────
  # Priority 1 – POST /pet with missing required field (name) → 405 / 400
  # Priority 2 – POST /pet with invalid status value → 400
  # Priority 3 – GET /pet/findByStatus with status "available" → 200 and non-empty list
  # Priority 4 – PUT /pet to update an existing pet → 200 and updated fields
  # Priority 5 – DELETE /pet/{id} for an existing pet → 200 confirmation
  # Priority 6 – DELETE /pet/{id} for a non-existent pet → 404
  # Priority 7 – POST /pet with malformed JSON → 400 / 415
