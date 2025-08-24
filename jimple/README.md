# ANTLR4 Java Starter Project

This project is a simple ANTLR4 starter for Java. It includes a basic grammar for parsing declarations and expressions.

## Prerequisites

- Java JDK 17 or higher
- Maven 3.6+ 
- ANTLR4 Plugin for your IDE (recommended)

## Project Structure

- `Example.g4` - The ANTLR4 grammar file that defines the language syntax
- `pom.xml` - Maven project configuration with ANTLR4 plugin and dependencies


## Building the Project

1. Generate the ANTLR4 classes:

```bash
mvn generate-resources
```

2. Build the project:

```bash
mvn clean package
```

3. Build single JAR with dependencies:
```bash
mvn clean package assembly:single
```
