package org.eclipse.dltk.ui.text.heredoc;

public final class HereDocUtils
{
    private static String IDENTIFIER = "identifier";

    private static String SEPARATOR = "::DLTK_HD::";

    private static String TERMINATOR = "terminator";

    public static String getPartition(String contentType)
    {
        return contentType.split(SEPARATOR)[0];
    }

    public static String getTerminator(String contentType)
    {
        return contentType.split(SEPARATOR)[1].split("\\|")[0];
    }

    public static boolean isHereDocContent(String contentType)
    {
        return ((contentType != null) && contentType.contains(SEPARATOR));
    }

    public static boolean isIdentForTerm(String termContentType, String identContentType)
    {
        if (isIdentifier(identContentType) && isTerminator(termContentType))
        {
            return getTerminator(termContentType).equals(getTerminator(identContentType));
        }

        return false;
    }

    public static boolean isIdentifier(String contentType)
    {
        return (isHereDocContent(contentType) && contentType.endsWith(IDENTIFIER));
    }

    public static boolean isTerminator(String contentType)
    {
        return (isHereDocContent(contentType) && contentType.endsWith(TERMINATOR));
    }

    static String createIdentifier(String contentType, String identifier)
    {
        return contentType + SEPARATOR + identifier + "|" + IDENTIFIER;
    }

    static String createTerminator(String contentType, String terminator)
    {
        return contentType + SEPARATOR + terminator + "|" + TERMINATOR;
    }
}
