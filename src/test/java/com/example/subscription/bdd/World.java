package com.example.subscription.bdd;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import com.example.subscription.domain.entity.Student;

@Component
@ScenarioScope
public class World {
    public Student student;
    public Exception error;
}
