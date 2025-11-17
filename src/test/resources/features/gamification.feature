Feature: Gamification for Course Completion

  Background:
    Given a student named "Ana" with 2 initial credits

  Scenario: Student earns credits for completing a course with an average above 7.0
    When the student finishes a course with an average of 9.5
    Then the student should have 1 completed course
    And the student's credit balance should be 5

  Scenario: Student does not earn credits for completing a course with an average equal to 7.0
    When the student finishes a course with an average of 7.0
    Then the student should have 1 completed course
    And the student's credit balance should be 2

  Scenario: Student does not earn credits for completing a course with an average below 7.0
    When the student finishes a course with an average of 6.9
    Then the student should have 1 completed course
    And the student's credit balance should be 2

  Scenario Outline: Should throw an exception for invalid averages
    When the student tries to finish a course with the invalid average <average>
    Then the system should throw an exception with the message "<expected_message>"

    Examples:
      | average | expected_message                           |
      | -0.1    | Average must be a value between 0.0 and 10.0. |
      | 10.1    | Average must be a value between 0.0 and 10.0. |
