package org.jboss.pressgang.ccms.utils.common;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class DocBookUtilitiesTest {

    @Test
    public void shouldEscapeTitleString() {
        // Given a string testing replacing < with &lt;
        final String test = "<title>Product A > Product B<phrase condition=\"beta\">-Beta</phrase></title>";
        // and a string testing replacing > with &gt;
        final String test2 = "<title>Product A < Product B<phrase condition=\"beta\">-Beta</phrase></title>";
        // and a string testing replacing & with &amp;
        final String test3 = "<title>Product A & Product B<phrase condition=\"beta\">-Beta</phrase></title>";
        // and a string to test entities aren't altered
        final String test4 = "<title>Product A &amp; Product B<phrase condition=\"beta\">-Beta</phrase></title>";

        // When escaping the title string
        final String output = DocBookUtilities.escapeTitleString(test);
        final String output2 = DocBookUtilities.escapeTitleString(test2);
        final String output3 = DocBookUtilities.escapeTitleString(test3);
        final String output4 = DocBookUtilities.escapeTitleString(test4);

        // Then make sure the relevant characters have been replaced
        assertEquals(output, "<title>Product A &gt; Product B<phrase condition=\"beta\">-Beta</phrase></title>");
        assertEquals(output2, "<title>Product A &lt; Product B<phrase condition=\"beta\">-Beta</phrase></title>");
        assertEquals(output3, "<title>Product A &amp; Product B<phrase condition=\"beta\">-Beta</phrase></title>");
        assertEquals(output4, test4);
    }
}
