package org.eclipse.dltk.core.tests.model;

import java.util.Arrays;

import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.internal.core.SourceNamespace;

import junit.framework.TestCase;

public class NamespaceTests extends TestCase {

    public void testCreate() {
        INamespace namespace = new SourceNamespace(new String[] { "java", "lang" });
        assertEquals("java.lang", namespace.getQualifiedName("."));
    }

    public void testStrings() {
        String[] input = new String[] { "java", "lang" };
        INamespace namespace = new SourceNamespace(input);
        assertTrue(Arrays.equals(input, namespace.getStrings()));
    }

    public void testStringsReturnCopy() {
        String[] input = new String[] { "java", "lang" };
        INamespace namespace = new SourceNamespace(input);
        Arrays.fill(namespace.getStrings(), "NO");
        assertTrue(Arrays.equals(input, namespace.getStrings()));
    }

    public void testEquals() {
        INamespace first = new SourceNamespace(new String[] { "java", "lang" });
        INamespace second = new SourceNamespace(new String[] { "java", "lang" });
        assertTrue(first.equals(second));
    }
}
