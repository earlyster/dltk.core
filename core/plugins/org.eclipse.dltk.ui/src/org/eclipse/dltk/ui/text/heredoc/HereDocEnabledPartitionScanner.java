package org.eclipse.dltk.ui.text.heredoc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * A slightly modified version of a <code>RuleBasedPartitionScanner</code> that
 * knows how to properly scan for heredoc partitions.
 * 
 * <p>
 * There is no need to use this partition scanner if heredoc is not supported by
 * the underlying dynamic language.
 * </p>
 * 
 * <p>
 * If you do use this partitioner, you <b>must</b> also use the
 * <code>HereDocEnabledPartitioner</code> partitioner as it knows how to
 * properly manage heredoc partitions.
 * </p>
 * 
 * @see HereDocPartitionRule
 * @see HereDocEnabledPartitioner
 * @see HereDocEnabledPresentationReconciler
 */
public class HereDocEnabledPartitionScanner extends RuleBasedPartitionScanner
{
    private List<TokenContainer> buffer = new ArrayList<TokenContainer>();

    private HereDocPartitionRule hereDocRule;

    /**
     * Creates a new heredoc partition scanner
     *  
     * @param rules list of predicate rules that should be used to create document partitions 
     * @param hereDocRule heredoc partitioning rule
     */
    public HereDocEnabledPartitionScanner(List<IPredicateRule> rules, HereDocPartitionRule hereDocRule)
    {
        this.hereDocRule = hereDocRule;
        
        IPredicateRule[] result = new IPredicateRule[rules.size()];
        setPredicateRules(rules.toArray(result));
    }
    
    @Override public IToken nextToken()
    {
		if (HereDocUtils.isHereDocContent(fContentType))
        {
            return handleHereDoc();
        }

        if (!buffer.isEmpty())
        {
            return buffer.remove(0).getToken();
        }

        return getNextToken(false);
    }

    @Override public void setPartialRange(IDocument document, int offset, int length, String contentType,
        int partitionOffset)
    {
        /*
         * clear out any tokens that may be sitting in the buffer before the 'nextToken()' is requested. this method is
         * only called if the document changes, which means the 'manual' rule should be passed all the information it
         * needs to finish it's evaluation piggy-backed off the passed content type.
         */
        buffer.clear();
        super.setPartialRange(document, offset, length, contentType, partitionOffset);
    }

    private IToken evalPossibleHereDoc(boolean inScan)
    {
        // our token is going to start wherever the scanner is
        fTokenOffset = fOffset;

        IToken token = hereDocRule.evaluate(this);

        if (token.isUndefined() || inScan)
        {
            return token;
        }
       
        buffer.add(new TokenContainer(token));
                
        int c;
        while ((c = read()) != ICharacterScanner.EOF)
        {
            unread();
            
            IToken next = getNextToken(true);
            buffer.add(new TokenContainer(next));
            
            if (c == '\n')
            {
                break;
            }
        }
       
        if (c != ICharacterScanner.EOF)
        {
            consumeHereDoc();
        }
        
        return buffer.remove(0).getToken();
    }
    
    private void consumeHereDoc()
    {
        // need to work w/ a copy otherwise we get concurrent modification exception
        for (TokenContainer container : new ArrayList<TokenContainer>(buffer))
        {
			if (HereDocUtils.isHereDocContent(container.getContentType()))
            {
                fTokenOffset = fOffset;
                fColumn = UNDEFINED;

                IToken body = hereDocRule.evaluate(this, container.getContentType());
                buffer.add(new TokenContainer(body));
            }
        }
    }

    private IToken getNextToken(boolean inScan)
    {
        IToken token = evalPossibleHereDoc(inScan);
        if (token.isUndefined())
        {
            token = super.nextToken();
        }

        return token;
    }

    private IToken handleHereDoc()
    {
        // reset to partition start so we get all the characters
        fTokenOffset = fPartitionOffset;

        IToken token = hereDocRule.evaluate(this, fContentType);

        // we found our rule, reset the contentType just like our parent would
        fContentType = null;
        return token;
    }
    
    private class TokenContainer
    {
        private int offset;
        private IToken token;
        private int tokenOffset;

        TokenContainer(IToken token)
        {
            this.offset = fOffset;
            this.tokenOffset = fTokenOffset;

            this.token = token;
        }

        String getContentType()
        {
            return (String) token.getData();
        }
        
        IToken getToken()
        {
            fOffset = offset;
            fTokenOffset = tokenOffset;

            return token;
        }
    }
}
