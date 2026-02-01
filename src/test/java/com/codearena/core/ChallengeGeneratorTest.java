package com.codearena.core;

import com.codearena.models.Challenge;
import com.codearena.models.ErrorContext;
import org.junit.Test;
import static org.junit.Assert.*;

public class ChallengeGeneratorTest {

    @Test
    public void testGenerateChallenge() {
        ErrorContext context = new ErrorContext(
            "NullPointerException at line 10",
            "Runtime Error",
            "String s = null; s.length();",
            "Calculating string length",
            "Beginner"
        );

        Challenge challenge = ChallengeGenerator.generate(context);

        assertNotNull(challenge);
        assertNotNull(challenge.getQuestion());
        assertEquals(4, challenge.getOptions().size());
        assertTrue(challenge.getCorrectIndex() >= 0 && challenge.getCorrectIndex() < 4);
        assertNotNull(challenge.getExplanation());
        assertFalse(challenge.getHints().isEmpty());
        assertTrue(challenge.getXpValue() > 0);
        
        System.out.println("[DEBUG_LOG] Generated Question: " + challenge.getQuestion());
        System.out.println("[DEBUG_LOG] Options: " + challenge.getOptions());
    }
}
