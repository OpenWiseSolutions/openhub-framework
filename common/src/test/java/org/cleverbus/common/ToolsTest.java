/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * 
 * Tests for Tools class
 * 
 * @author <a href="mailto:pavel.hora@cleverlance.com">Pavel Hora</a>
 */
@RunWith(JUnit4.class)
public class ToolsTest{

    /**
     * Test toString method.
     * 
     * When input is null, it should return null, otherwise the input.toString method
     */
    @Test
    public void testToString() {
        
        Integer i = null;
        assertNull(Tools.toString(i));
        
        i = 2;
        
        assertEquals("2", Tools.toString(i));
    }
    
    /**
     * Test joinNonEmpty method.
     * 
     * Method merges the input strings and each string should be separated by enrich character. If string before, after
     * next one is null or empty, the enrich character is not inserted.
     */
    @Test
    public void testJoinNonEmpty() {
        assertEquals("Separate this character",
                Tools.joinNonEmpty(new String[]{"", "Separate", "this", null, "character", "", null}, ' '));

        assertEquals("",
                Tools.joinNonEmpty(new String[]{}, 'x'));

        assertEquals("//",
                Tools.joinNonEmpty(new String[]{"//"}, 'x'));

        assertEquals("//",
                Tools.joinNonEmpty(new String[]{null, "//", null}, 'x'));

        assertEquals("///",
                Tools.joinNonEmpty(new String[]{"/", "", "/", null}, '/'));

        assertEquals("/,2",
                Tools.joinNonEmpty(new String[]{"/", "   ", "2", null}, ','));
    }
}
