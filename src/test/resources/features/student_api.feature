Feature: Student API Management
  Como usuário do sistema, eu quero listar estudantes
  para ver quem está cadastrado.

  Scenario: List all students
    Given the database has a student named "Carlos"
    When the user sends a GET request to "/students"
    Then the response status should be 200
    And the response body should contain a list with "Carlos"