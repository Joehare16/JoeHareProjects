/**
 * Parser for DSL programs.
 * TODO: Complete this class to implement a parser for DSL.
 */
public class Parser
{
    // The lexical analyser.
    private final Tokenizer lex;
    // The global symbol table.
    private final SymbolTable st;
    // The current token driving the parse.
    // This is set by a call to getNextToken();
    private Token currentToken;
    // A debugging flag.
    // Set this to false when not required.
    private boolean debug = true;

    /**
     * Create a parser.
     *
     * @param lex The lexical analyser.
     * @param st  Global symbol table for the program.
     */
    public Parser(Tokenizer lex, SymbolTable st)
    {
        this.lex = lex;
        this.st = st;
        currentToken = null;
        // Set currentToken to the first token of the input.
        getNextToken();
        if(debug && currentToken != null) {
            // Show details of the first token.
            System.out.print("The first token is: ");
            System.out.println(getTokenDetails());
        }
    }

    /**
     * Parse the full input.
     * @return true if the parse is successful, false otherwise.
     */
    public boolean parseProgram()
    {
        // The first token is already available in the currentToken variable.
        if(parseDeclarations()&&parseStatements()){
            return true;
        }


        return false;
    }
    private boolean parseDeclarations(){
        while(parseDeclaration()){
        }
        return true;
    }
    private boolean parseDeclaration(){
        if(expectKeyword(Keyword.INT)){
            if(parseIdentifiers()){
                return true;
            }
        }
        return false;
    }
    private boolean parseIdentifiers() {
        if (parseIdentifier()) {
            while (expectSymbol(",")) {
                if (!parseIdentifier()) {
                    return false;
                }
            }
            return true; // Return true when at least one identifier is parsed.
        }
        return false;
    }

    private boolean parseIdentifier() {
        if (currentToken == Token.IDENTIFIER) {
            if (!st.isDeclared(lex.getTokenDetails())) {
                st.declare(lex.getTokenDetails());
            }
            getNextToken();
            return true; // Identifier is parsed successfully.
        }
        return false;
    }
    private boolean parseStatements(){
        while(parseDeclaration() && expectSymbol(";")){
        }
        getNextToken();
        return true;
    }
    private boolean parseStatement(){
        return parseAssignment() || parseConditional() || parseLoop() || parsePrint();
    }

    private boolean parseAssignment(){
        if(currentToken == Token.IDENTIFIER){
            getNextToken();
            return expectSymbol(":=") && parseExpression();
        }
        return false;
    }

    private boolean parseConditional(){
        if(expectKeyword(Keyword.IF)){
            if(parseExpression()){
                if(expectKeyword(Keyword.THEN)){
                    if(parseStatements()) {
                        while(expectKeyword(Keyword.ELSE)){
                            if(!(parseStatements())){
                                return false;
                            }
                        }
                        if(expectKeyword(Keyword.FI)) {
                            getNextToken();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean parseLoop(){
        if (expectKeyword(Keyword.WHILE)){
            if(parseExpression()){
                if(expectKeyword(Keyword.DO)){
                    if(parseStatements()){
                        if(expectKeyword(Keyword.OD)){
                            getNextToken();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Parse a print statement:
     *     print ::= PRINT expression ;
     * This method is complete.
     * @return true if a print statement is found, false otherwise.
     */
    private boolean parsePrint()
    {
        if (expectKeyword(Keyword.PRINT)) {
            if (parseExpression()) {
                getNextToken();
                return true;
            } else {
                throw new SyntaxException("Missing expression");
            }
        }
        else {
            // Not a print statement, but could be something else.
            // So this is not an error.
            return false;
        }
    }

    /**
     * Parse an expression:
     *     expression ::= term ( binaryOp term ) ? ;
     * TODO: Complete this method.
     * @return true if an expression is found, false otherwise.
     */
    private boolean parseExpression()
    {
      if(parseTerm()){
          while (parseBinaryOp()){
              if(!parseTerm()){
                  return false;
              }
          }
          getNextToken();
          return true;
    }
      return false;
    }

    private boolean parseBinaryOp(){
        //if the next token contains one of these BinaryOP symbols its correct
        //either method will return true if the symbol matches;
        getNextToken();
        return parseRelationalOp() || parseArithmeticOp();
    }
    private boolean parseArithmeticOp(){
        //parameters are ones set out in grammar
        getNextToken();
        return expectSymbol("+","-");
    }
    private boolean parseRelationalOp(){
        //parameters are symbols set out in grammar
        getNextToken();
        return expectSymbol("=","!=","<",">","<=",">=");
    }
    private boolean parseTerm(){
        //checks all the grammar rules on terms
        //can either be an identifer or int const
        if(currentToken == Token.IDENTIFIER|| currentToken == Token.INT_CONST){
            getNextToken();
            return true;
        }
        //can also be an expression in brackets
        //checks if the next symbol is a openbracket
        if(expectSymbol("(")){
            //then checks this is followed by an expression and a closed bracket
            if (parseExpression() && expectSymbol(")")){
                getNextToken();
                return true;
            }
            }
        //can also be a negated term checks if there is minus symbol followed by term
        if(expectSymbol("-")){
            if (parseTerm()){
                getNextToken();
                return true;
            }
    }
        return false;
    }

    /**
     * Check whether the given Keyword is the current token.
     * If it is then <b>get the next token</b> and return true.
     * Otherwise, return false.
     * @param aKeyword The keyword to check for.
     * @return true if the keyword is the current token, false otherwise.
     */
    private boolean expectKeyword(Keyword aKeyword)
    {
        if(currentToken == Token.KEYWORD && lex.getKeyword() == aKeyword) {
            getNextToken();
            return true;
        }
        else {
            return false;
        }
    }
    private boolean expectSymbol(String... symbols) {
        if (currentToken == Token.SYMBOL) {
            String Symbol = lex.getSymbol();
            for (String expectedSymbol : symbols) {
                if (Symbol.equals(expectedSymbol)) {
                    getNextToken();
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * A debugging method.
     * Access details of the current token from the tokenizer and
     * format a String with the details.
     * @return a formatted version of the current token.
     */
    private String getTokenDetails()
    {
        StringBuilder s = new StringBuilder();
        s.append(currentToken).append(' ');
        switch(currentToken) {
            case KEYWORD:
                s.append(lex.getKeyword()); break;
            case IDENTIFIER:
                s.append(lex.getIdentifier()); break;
            case SYMBOL:
                s.append(lex.getSymbol()); break;
            case INT_CONST:
                s.append(lex.getIntval()); break;
            default:
                s.append("???"); break;

        }
        s.append(' ');
        return s.toString();
    }
    /**
     * Advance to the next token.
     * Sets currentToken.
     */
    private void getNextToken()
    {
        if (lex.hasMoreTokens()) {
            lex.advance();
            currentToken = lex.getTokenType();
        } else {
            currentToken = null;
        }
    }
}