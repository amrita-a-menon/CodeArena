package com.codearena.ai;

public class MockAIAdapter implements AIAdapter {
    @Override
    public String generate(String prompt) {
        String lower = prompt == null ? "" : prompt.toLowerCase();

        if (lower.contains("incompatible types")
                || lower.contains("cannot be converted")
                || lower.contains("type mismatch")
                || lower.contains("compilation error")) {
            return """
                {
                    "question": "What is the most likely cause of this compilation error? (Mock Challenge)",
                    "options": [
                        "Assigning a value of the wrong type to a variable",
                        "Forgetting a semicolon at the end of a line",
                        "Using the wrong loop type",
                        "A method is declared inside another method"
                    ],
                    "correctIndex": 0,
                    "explanation": "The compiler reports a type mismatch when a value of one type is assigned to a variable of another type.",
                    "hints": ["Check variable types on both sides of the assignment", "Look for String-to-int assignments"],
                    "xpValue": 15
                }
                """;
        }

        if (lower.contains("arrayindexoutofbounds")) {
            return """
                {
                    "question": "What most likely caused this ArrayIndexOutOfBoundsException? (Mock Challenge)",
                    "options": [
                        "Accessing an array index outside its valid range",
                        "Using the wrong import statement",
                        "Calling a method on a null object",
                        "Forgetting to close a file handle"
                    ],
                    "correctIndex": 0,
                    "explanation": "This happens when you access an index that is less than 0 or greater than or equal to the array length.",
                    "hints": ["Check loop bounds", "Compare index with array length"],
                    "xpValue": 15
                }
                """;
        }

        if (lower.contains("classcastexception")) {
            return """
                {
                    "question": "What most likely caused this ClassCastException? (Mock Challenge)",
                    "options": [
                        "Casting an object to an incompatible type",
                        "Using the wrong loop condition",
                        "Missing an import statement",
                        "Forgetting to initialize a variable"
                    ],
                    "correctIndex": 0,
                    "explanation": "A ClassCastException occurs when you try to cast an object to a class it is not an instance of.",
                    "hints": ["Check instanceof before casting", "Confirm the actual runtime type"],
                    "xpValue": 15
                }
                """;
        }

        if (lower.contains("arithmeticexception") || lower.contains("/ by zero")) {
            return """
                {
                    "question": "What caused this ArithmeticException? (Mock Challenge)",
                    "options": [
                        "Division by zero",
                        "Using a number that is too large for an int",
                        "Adding a string to a number",
                        "Calling a math function on a null object"
                    ],
                    "correctIndex": 0,
                    "explanation": "ArithmeticException is thrown when an exceptional arithmetic condition has occurred, such as integer division by zero.",
                    "hints": ["Check your divisors", "Look for / 0 in your code"],
                    "xpValue": 15
                }
                """;
        }

        if (lower.contains("numberformatexception")) {
            return """
                {
                    "question": "What most likely caused this NumberFormatException? (Mock Challenge)",
                    "options": [
                        "Trying to parse a non-numeric string as a number",
                        "Using the wrong variable name",
                        "Forgetting to import java.lang.Integer",
                        "Defining a number that is too long"
                    ],
                    "correctIndex": 0,
                    "explanation": "This exception is thrown when you try to convert a string to a numeric type, but the string does not have the appropriate format.",
                    "hints": ["Check the string value being parsed", "Ensure the string only contains digits"],
                    "xpValue": 15
                }
                """;
        }

        // Default generic challenge if it doesn't match specific ones
        return String.format("""
            {
                "question": "An error occurred: %s. How should you start debugging this? (Mock Challenge)",
                "options": [
                    "Read the error message and identify the line number",
                    "Restart the IDE immediately",
                    "Rewrite the entire class from scratch",
                    "Ignore it and hope it goes away"
                ],
                "correctIndex": 0,
                "explanation": "The first step in debugging is always to understand what the error is and where it happened.",
                "hints": ["Look at the stack trace", "Identify the exception type"],
                "xpValue": 10
            }
            """, lower.contains("exception") ? lower.substring(Math.max(0, lower.indexOf("exception") - 20), Math.min(lower.length(), lower.indexOf("exception") + 9)) : "Runtime Error");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getName() {
        return "Mock AI";
    }
}
