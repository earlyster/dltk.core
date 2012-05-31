package org.eclipse.dltk.ui.text.heredoc;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Abstract implementation of a rule that can be used to detect heredoc
 * partitions.
 * 
 * <p>
 * A note on tokens returned from this rule...
 * </p>
 * 
 * Heredoc partitions are unique because they have an identifier/terminator and
 * span multiple lines but may have characters that follow the heredoc
 * identifier but are not considered to be part of the identifier itself, ie:
 * </p>
 * 
 * <pre>
 *   &lt;&lt;EOF . "hello world";
 *     heredoc body
 *   EOF
 * </pre>
 * 
 * <p>
 * requiring the terminator to be preserved to allow the partitioner to know
 * when the partition has ended. To achieve this, the identifier/terminator is
 * appended to the partition types returned by this rule so future rule
 * evaluations against existing partitions can terminate correctly.
 * </p>
 * 
 * <p>
 * Therefore, the success tokens returned from this rule will never be comprised
 * of just the partition type, which is why the <code>HereDocEnabled*</code>
 * classes must be used in conjunction with this rule.
 * </p>
 * 
 * @see HereDocEnabledPartitioner
 * @see HereDocEnabledPartitionScanner
 * @see HereDocEnabledPresentationReconciler
 */
public abstract class HereDocPartitionRule {

	private String fPartition;

	private String fStart;

	/**
	 * Creates a new heredoc partition rule.
	 * 
	 * @param start
	 *            heredoc start sequence
	 * @param token
	 *            success token
	 */
	public HereDocPartitionRule(String start, IToken token) {
		this.fStart = start;
		// we only care about the content type...
		this.fPartition = (String) token.getData();
	}

	/**
	 * Evalute the rule for a possible heredoc partition.
	 * 
	 * @param scanner
	 *            heredoc character scanner
	 * 
	 * @return heredoc token
	 */
	public IToken evaluate(HereDocEnabledPartitionScanner scanner) {
		StringBuffer buffer = new StringBuffer();

		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			buffer.append((char) c);

			if (buffer.length() == fStart.length()) {
				break;
			}
		}

		if (buffer.toString().equals((fStart))) {
			return detectIdentifier(scanner);
		}

		unwind(scanner, buffer.length());

		return Token.UNDEFINED;
	}

	/**
	 * Evaluate the rule when a known heredoc partition has been seen.
	 * 
	 * @param scanner
	 *            heredoc character scanner
	 * 
	 * @param partition
	 *            known heredoc partition containing terminator
	 * 
	 * @return heredoc token
	 */
	public IToken evaluate(HereDocEnabledPartitionScanner scanner,
			String partition) {

		boolean readChar = false;

		StringBuffer buffer = new StringBuffer();
		String terminator = HereDocUtils.getTerminator(partition);

		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			readChar = true;

			if (!isNewline(scanner, c)) {
				buffer.append((char) c);
				continue;
			}

			if (buffer.toString().equals(terminator)) {
				break;
			}

			buffer.setLength(0);
		}

		// we only saw 'EOF'
		if (!readChar) {
			scanner.unread();
			return Token.UNDEFINED;
		}

		return createTerminator(terminator);
	}
	
	/**
	 * Create a token representing the heredoc identifier partition
	 * 
	 * @param identifier
	 *            heredoc identifier
	 * 
	 * @return token
	 */
	protected final IToken createIdentifier(String identifier) {
		return new Token(HereDocUtils.createIdentifier(fPartition, identifier));
	}

	/**
	 * Create a token representing the heredoc terminator partition
	 * 
	 * @param terminator
	 *            heredoc terminator
	 * 
	 * @return token
	 */
	protected final Token createTerminator(String terminator)
	{
		return new Token(HereDocUtils.createTerminator(fPartition, terminator));
	}

	/**
	 * Extract the heredoc identifier.
	 * 
	 * <p>
	 * The input string will not contain whatever start sequence was specified
	 * in the constructor. If the passed string does not contain a valid heredoc
	 * identifier, <code>null</code> may be returned.
	 * </p>
	 * 
	 * @param str
	 *            string containing potential heredoc identifier
	 * 
	 * @return heredoc identifier or <code>null</code> if no valid identifer
	 *         exists
	 */
	protected abstract String extractIdentifier(String str);

	/**
	 * Is the specified character a newline character.
	 */
	protected final boolean isNewline(ICharacterScanner scanner, int c) {
		char[][] lineDelimiters = scanner.getLegalLineDelimiters();

		for (int i = 0; i < lineDelimiters.length; i++) {
			if (c == lineDelimiters[i][0]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Parse the heredoc identifier to extract the heredoc terminator.
	 * 
	 * <p>
	 * Some dynamic languages allow the heredoc identifier to be quoted and/or
	 * escaped, however those characters are not considered to be part of the
	 * terminator and need to be removed from the string.
	 * </p>
	 * 
	 * @param identifier
	 *            heredoc identifier
	 * 
	 * @return heredoc terminator
	 */
	protected abstract String parseIdentifier(String identifier);

	/**
	 * Unwind the scanner a specified length.
	 */
	protected final void unwind(ICharacterScanner scanner, int length) {
		for (; length > 0; length--) {
			scanner.unread();
		}
	}

	private IToken detectIdentifier(ICharacterScanner scanner) {
		int c;
		StringBuffer buffer = new StringBuffer();

		/*
		 * rather then attempt to detect the heredoc identifier char by char, we
		 * buffer everything in up until the newline character and then hand-off
		 * to the sub-class to first extract the identifier so the scanner can
		 * be unwound for the chars not consumed and then we reparse the
		 * identifier to extract the heredoc terminator, as the identifier may
		 * be quoted, etc if the language supports it.
		 */
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (isNewline(scanner, c)) {
				break;
			}

			buffer.append((char) c);
		}

		// unread the '\n' or EOF
		scanner.unread();

		String identifier = extractIdentifier(buffer.toString());

		if (identifier == null) {
			unwind(scanner, buffer.length());
			return Token.UNDEFINED;
		}

		unwind(scanner, buffer.length() - identifier.length());
		identifier = parseIdentifier(identifier);

		return createIdentifier(identifier);
	}
}
