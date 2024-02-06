import java.util.*;
import java.text.ParseException;
import java.util.regex.Pattern;

class CompilerParser {
    LinkedList<Token> tokens;

    /**
     * Constructor for the CompilerParser
     *
     * @param tokens A linked list of tokens to be parsed
     */
    public CompilerParser(LinkedList<Token> tokens) {
        //this.tokens = new Token(tokens.getType());
        this.tokens = tokens;
    }

    /**
     * Generates a parse tree for a single program
     *
     * @return a ParseTree
     */
    public ParseTree compileProgram() throws ParseException {
        //ParseTree res = null;
        //ParseTree res = compileClass();
        if (have("keyword", "class")) {
            ParseTree res = compileClass();
            if (res != null) {
                return res;
            } else {
                throw new ParseException("the program doesn't begin with a class", 0);
            }
        } else {
            throw new ParseException("the program doesn't begin with a class", 0);
        }
//        if(Objects.equals(tokens.get(0).getType(), "keyword") && Objects.equals(tokens.get(0).getValue(), "class")) {
//            res = compileClass();
//            return res;
//        }
//        else {
//            throw new ParseException("the program doesn't begin with a class",0);
//        }
        //return res;
    }

    /**
     * Check whether the identifier is valid
     * @param identifier
     * @return
     */
    private boolean isValidIdentifier(Token token) {
        if(Objects.equals(token.getType(), "identifier")) {
            String pattern = "^[a-zA-Z_]+[a-zA-Z_0-9]*";
            return Pattern.matches(pattern,token.getValue());
        }
        return false;
    }

    /**
     * check whether it's a valid type declaration
     * @param token
     * @return
     */
    private boolean isValidType(Token token) {
        return have("keyword", "int") ||
                have("keyword", "char") ||
                have("keyword", "boolean") ||
                isValidIdentifier(token);
    }
    /**
     * Generates a parse tree for a single class
     *
     * @return a ParseTree
     */
    public ParseTree compileClass() throws ParseException {
        if (have("keyword", "class")) {
            ParseTree res = new ParseTree("class", "");
            res.addChild(current());
            next();
            if (isValidIdentifier(current())) {
                res.addChild(current());
                next();
                if (have("symbol", "{")) {
                    res.addChild(current());
                    next();
                    //insert the title here
//                    if(have("keyword","static") || have("keyword", "field")) {
//                        res.addChild(new Token("classVarDec",""));
//                    }
                    //can have zero or more class variable declarations
                    while (have("keyword", "static") || have("keyword", "field")) {
                        ParseTree temp = compileClassVarDec();
                        if (temp != null) {
                            res.addChild(temp);
                        }
                    }
                    //can have zero or more subroutine declarations
                    while (have("keyword", "function") || have("keyword", "method")) {
                        ParseTree temp1 = compileSubroutine();
                        if (temp1 != null) {
                            res.addChild(temp1);
                        }
                    }
                    if (have("symbol", "}")) {
                        res.addChild(current());
                        return res;
                    } else {
                        throw new ParseException("the program doesn't begin with a class", 0);
                    }
                } else {
                    throw new ParseException("the program doesn't begin with a class", 0);
                }
            } else {
                throw new ParseException("the program doesn't begin with a class", 0);
            }
        } else {
            throw new ParseException("the program doesn't begin with a class", 0);
            //return null;
        }
        //return null;
    }

        //return null;



    /**
     * Generates a parse tree for a static variable declaration or field declaration
     *
     * @return a ParseTree
     */
    public ParseTree compileClassVarDec() throws ParseException {
        if (have("keyword", "static") || have("keyword", "field")) {
            //classVarDec
            ParseTree res = new ParseTree("classVarDec", "");
            //ParseTree res = new ParseTree(current().getType(), current().getValue());
            res.addChild(current());
            next();
            if (isValidType(current())) { //type:int||char||boolean||identifier
                res.addChild(current());
                next();
                if (isValidIdentifier(current())) { //check validation of identifier
                    res.addChild(current());
                    next();
                    while (have("symbol", ",")) { //check for more class variables
                        res.addChild(current());
                        next();
                        if (isValidIdentifier(current())) {
                            res.addChild(current());
                            next();
                        } else {
                            throw new ParseException("Not a valid class variable declaration", 0);
                        }
                    }
                    if (have("symbol", ";")) {
                        res.addChild(current());
                        next();
                    }
                } else {
                    throw new ParseException("Not a valid class variable declaration", 0);
                }
            } else {
                throw new ParseException("Not a valid class variable declaration", 0);
            }
//            while(have("keyword", "static") || have("keyword", "field")) {
//                res.addChild(current());
//                next();
//                if(isValidType(current())) {
//                    res.addChild(current());
//                    next();
//                    if (isValidIdentifier(current())) { //check validation of identifier
//                        res.addChild(current());
//                        next();
//                        while (have("symbol", ",")) { //check for more class variables
//                            res.addChild(current());
//                            next();
//                            if (isValidIdentifier(current())) {
//                                res.addChild(current());
//                                next();
//                            }
//                        }
//                        if (have("symbol", ";")) {
//                            res.addChild(current());
//                            next();
//                        }
//                    }
//                } else {
//                    throw new ParseException("Not a valid class variable declaration", 0);
//                }
//            }
            return res;
        } else {
            //return null;
            throw new ParseException("Not a valid class variable declaration", 0);
        }
        //return null;
    }
        //return null;



    /**
     * Generates a parse tree for a method, function, or constructor
     *
     * @return a ParseTree
     */
    public ParseTree compileSubroutine() throws ParseException {
        //function char char ( ) { }
        if (have("keyword", "function") ||
                have("keyword", "method") ||
                have("keyword", "constructor")) {
            ParseTree res = new ParseTree("subroutine", "");
            res.addChild(current());
            next();
            if (have("keyword","void") || isValidType(current())) { //void || type
                res.addChild(current());
                next();
                if (isValidIdentifier(current())) {
                    res.addChild(current());
                    next();
//                    if (have("symbol", "(")) {
//                        res.addChild(new Token("parameterList",""));
//                    }
                    //parameter list can be zero or one
                    if (have("symbol", "(")) {
                        res.addChild(current());
                        next();
                        //res.addChild(new Token("parameterList",""));
                        ParseTree parseTree = compileParameterList();
                        if (parseTree != null) {
                            res.addChild(parseTree);
                        }
                        if (have("symbol", ")")) {
                            res.addChild(current());
                            next();
                            ParseTree temp = compileSubroutineBody();
                            if (temp != null) {
                                res.addChild(temp);
                            }
                            return res;
                        }
                    }
                } else {
                    throw new ParseException("Not a valid subroutine", 0);
                }
            } else {
                throw new ParseException("Not a valid subroutine", 0);
            }
        } else {
            throw new ParseException("123",0);
        }
        return null;
    }

    /**
     * Generates a parse tree for a subroutine's parameters
     *
     * @return a ParseTree
     */
    public ParseTree compileParameterList() throws ParseException {
        //parameter list can have 0 or 1
        if (isValidType(current())) { //type
            ParseTree res = new ParseTree("parameterList", "");
            res.addChild(current());
            next();
            if (isValidIdentifier(current())) {
                res.addChild(current());
                next();
            } else {
                throw new ParseException("Not a valid parameter list", 0);
            }
            while (have("symbol", ",")) {
                res.addChild(current());
                next();
                if (isValidType(current())) { //type
                    res.addChild(current());
                    next();
                    if (isValidIdentifier(current())) {
                        res.addChild(current());
                        next();
                    }
                }
            }
            return res;
        }
        return null;
    }

    /**
     * Generates a parse tree for a subroutine's body
     *
     * @return a ParseTree
     */
    public ParseTree compileSubroutineBody() throws ParseException {
        if (have("symbol", "{")) {
            ParseTree res = new ParseTree("subroutineBody", "");
            res.addChild(current());
            next();
            //can have multiple var declarations
            //zero or more
            ParseTree temp = compileVarDec();
            while(temp != null) {
                res.addChild(temp);
                temp = compileVarDec();
            }
            //at least one statement
            //one or more
            ParseTree temp1 = compileStatements();
            if(temp1 != null) {
                res.addChild(temp1);
                while (have("keyword", "let") ||
                        have("keyword", "if") ||
                        have("keyword", "while") ||
                        have("keyword", "do") ||
                        have("keyword","return")) {
                    temp1 = compileStatements();
                    res.addChild(temp1);
                }
            }
            if (have("symbol", "}")) {
                res.addChild(new ParseTree(current().getType(), current().getValue()));
                next();
                return res;
            }
        } else {
            throw new ParseException("Parse Error", 0);
        }
        return null;
    }

    /**
     * Generates a parse tree for a local variable declaration in a subroutine
     *
     * @return a ParseTree
     */
    public ParseTree compileVarDec() throws ParseException { //can be zero or more
        if (have("keyword", "var")) {
            ParseTree res = new ParseTree("varDec", "");
            res.addChild(current());
            next();
            if (isValidType(current())) { // type
                res.addChild(current());
                next();
                if (isValidIdentifier(current())) {
                    res.addChild(current());
                    next();
                    while (have("symbol", ",")) {
                        res.addChild(current());
                        next();
                        if (isValidIdentifier(current())) {
                            res.addChild(current());
                            next();
                        } else {
                            throw new ParseException("Not a valid Var declaration", 0);
                        }
                    }
                    if (have("symbol", ";")) {
                        res.addChild(current());
                        next();
                        while(have("keyword", "var")) {
                            res.addChild(current());
                            next();
                            if (isValidType(current())) {
                                res.addChild(current());
                                next();
                                if (isValidIdentifier(current())) {
                                    res.addChild(current());
                                    next();
                                    while (have("symbol", ",")) {
                                        res.addChild(current());
                                        next();
                                        if (Objects.equals(current().getType(), "identifier")) {
                                            res.addChild(current());
                                            next();
                                        } else {
                                            throw new ParseException("Not a valid Var declaration", 0);
                                        }
                                    }
                                    if (have("symbol", ";")) {
                                        res.addChild(current());
                                        next();
                                    }
                                } else {
                                    throw new ParseException("Not a valid Var declaration", 0);
                                }
                            } else {
                                throw new ParseException("Not a valid Var declaration", 0);
                            }
                        }
                    } else {
                        throw new ParseException("Not a valid Var declaration", 0);
                    }
                } else {
                    throw new ParseException("123",0);
                }
                //return res;
                //detect more var s here

                return res;

            } else {
                throw new ParseException("Not a valid Var declaration", 0);
            }
        }

        return null;
    }

    /**
     * Generates a parse tree for a series of statements
     *
     * @return a ParseTree
     */
    public ParseTree compileStatements() throws ParseException { //must be at least one statement
        ParseTree res = new ParseTree("statements", "");
        if (have("keyword", "let") ||
                have("keyword", "if") ||
                have("keyword", "while") ||
                have("keyword", "do") ||
                have("keyword","return")) {

            while(have("keyword", "let") ||
                    have("keyword", "if") ||
                    have("keyword", "while") ||
                    have("keyword", "do") ||
                    have("keyword","return")) {
                String temp = current().getValue();
                switch (temp) {
                    case "let": {
                        res.addChild(compileLet());
                        break;
                    }
                    case "if": {
                        res.addChild(compileIf());
                        break;
                    }
                    case "while": {
                        res.addChild(compileWhile());
                        break;
                    }
                    case "do": {
                        res.addChild(compileDo());
                        break;
                    }
                    case "return": {
                        res.addChild(compileReturn());
                        break;
                    }
                    default:
                        throw new ParseException("At least one statement in Routine", 0);
                }
            }
            return res;
        }else {
            return res;
        }
        //return null;

    }

    /**
     * Generates a parse tree for a let statement
     *
     * @return a ParseTree
     */
    public ParseTree compileLet() throws ParseException {
        if (have("keyword", "let")) {
            ParseTree res = new ParseTree("letStatement", "");
            while(have("keyword", "let")) {
                res.addChild(current());
                next();
                if (isValidIdentifier(current())) {
                    res.addChild(current());
                    next();
                    if(have("symbol","[")) {
                        res.addChild(current());
                        next();
                        ParseTree temp = compileExpression();
                        if(temp != null) {
                            res.addChild(temp);
                        }
                        if(have("symbol","]")) {
                            res.addChild(current());
                            next();
                        }
                    }
                    if (have("symbol", "=")) {
                        res.addChild(current());
                        next();
                        ParseTree temp = compileExpression();
                        if(temp != null) {
                            res.addChild(temp);
                        }
                        if (have("symbol", ";")) {
                            res.addChild(current());
                            next();
                        }
                    }
                } else {
                    throw new ParseException("Not a valid Let statement", 0);
                }
            }
            return res;
        } else {
            throw new ParseException("123",0);
        }

        //return null;
    }

    /**
     * Generates a parse tree for an if statement
     *
     * @return a ParseTree
     */
    public ParseTree compileIf() throws ParseException {
        if (have("keyword", "if")) {
            ParseTree res = new ParseTree("ifStatement", "");
            res.addChild(current());
            next();
            if (have("symbol", "(")) {
                res.addChild(current());
                next();
                ParseTree temp = compileExpression();
                if (temp != null) {
                    res.addChild(temp);
                } else {
                    throw new ParseException("123",0);
                }
                if (have("symbol", ")")) { //within if there must be at least one statement
                    res.addChild(current());
                    next();
                    if (have("symbol", "{")) {
                        res.addChild(current());
                        next();
                        ParseTree temp1 = compileStatements();
                        if(temp1 != null) {
                            res.addChild(temp1);
                        }
//                        else {
//                            throw new ParseException("123",0);
//                        }
                        if (have("symbol", "}")) {
                            res.addChild(current());
                            next();
                            //can have zero or one else block
                            if (have("keyword", "else")) { //dealing with else block
                                res.addChild(current());
                                next();
                                if (have("symbol", "{")) {
                                    res.addChild(current());
                                    next();
                                    ParseTree temp2 = compileStatements();
                                    if (temp2 != null) {
                                        res.addChild(temp2);
                                    } else {
                                        throw new ParseException("123",0);
                                    }
                                    if (have("symbol", "}")) {
                                        res.addChild(current());
                                        next();
                                    } else {
                                        throw new ParseException("123",0);
                                    }
                                }
                                else {
                                    throw new ParseException("Not a valid if statement", 0);
                                }
                            }
                            return res;
                        } else {
                            throw new ParseException("Not a valid if statement", 0);
                        }
                    } else {
                        throw new ParseException("Not a valid if statement", 0);
                    }
                } else {
                    throw new ParseException("Not a valid if statement", 0);
                }
            } else {
                throw new ParseException("Not a valid if statement", 0);
            }
        } else {
            throw new ParseException("123",0);
        }

        //return null;
    }

    /**
     * Generates a parse tree for a while statement
     *
     * @return a ParseTree
     */
    public ParseTree compileWhile() throws ParseException {
        if (have("keyword", "while")) {
            ParseTree res = new ParseTree("whileStatement", "");
            res.addChild(current());
            next();
            if (have("symbol", "(")) {
                res.addChild(current());
                next();
                ParseTree temp = compileExpression();
                if(temp != null) {
                    res.addChild(temp);
                } else {
                    throw new ParseException("123",0);
                }
                if(have("symbol",")")) {
                    res.addChild(current());
                    next();
                    if(have("symbol","{")) {
                        res.addChild(current());
                        next();
                        ParseTree temp1 = compileStatements();
                        if(temp1 != null) {
                            res.addChild(temp1);
                        } else {
                            throw new ParseException("At least one statement in Routine", 0);
                        }
                        if(have("symbol","}")) {
                            res.addChild(current());
                            next();
                            return res;
                        } else {
                            throw new ParseException("At least one statement in Routine", 0);
                        }
                    } else {
                        throw new ParseException("At least one statement in Routine", 0);
                    }
                } else {
                    throw new ParseException("At least one statement in Routine", 0);
                }
            } else {
                throw new ParseException("At least one statement in Routine", 0);
            }
        } else {
            throw new ParseException("At least one statement in Routine", 0);
        }
        //return null; //indicates no while statement
    }

    /**
     * Generates a parse tree for a do statement
     *
     * @return a ParseTree
     */
    public ParseTree compileDo() throws ParseException {
        if (have("keyword", "do")) {
            ParseTree res = new ParseTree("doStatement", "");
            res.addChild(current());
            next();
            ParseTree temp = compileExpression();
            if(temp != null) {
                res.addChild(temp);
            }
            if(have("symbol",";")){
                res.addChild(current());
                next();
            }
            return res;
        }
        return null;
    }

    /**
     * Generates a parse tree for a return statement
     *
     * @return a ParseTree
     */
    public ParseTree compileReturn() throws ParseException {
        if (have("keyword", "return")) {
            ParseTree res = new ParseTree("returnStatement", "");
            res.addChild(current());
            next();
            ParseTree temp = compileExpression();
            if(temp != null) {
                res.addChild(temp);
            }
            if(have("symbol",";")) {
                res.addChild(current());
                next();
            }
            return res;
        } else {
            throw new ParseException("111",0);
        }
        //return null;
    }

    /**
     * Test whether the token is valid operator
     * valid operators include + - * / & | > < =
     * @param token
     * @return
     */
    private boolean isValidOp(Token token) {
        if(token != null) {
            return have("symbol", "+") ||
                    have("symbol", "-") ||
                    have("symbol", "*") ||
                    have("symbol", "/") ||
                    have("symbol", "&") ||
                    have("symbol", "|") ||
                    have("symbol", ">") ||
                    have("symbol", "<") ||
                    have("symbol", "=");
        }
        return false;
    }

    /**
     * Test whether current token is valid unary operator
     * @param token
     * @return
     */
    private boolean isValidUnaryOp(Token token) {
        if(token != null) {
            return have("symbol", "-") ||
                    have("symbol", "~");
        }
        return false;
    }

    /**
     * Test whether current token is valid keyword Constant
     * @param token
     * @return
     */
    private boolean isValidKeywordConstant(Token token) {
        if(token != null) {
            return have("keyword", "true") ||
                    have("keyword", "false") ||
                    have("keyword", "null") ||
                    have("keyword", "this");
        }
        return false;
    }

    /**
     * Test whether current token is valid string constant
     * @param token
     * @return
     */
    private boolean isValidStringConstant(Token token) {
        if(token != null) {
            if(Objects.equals(token.getType(), "stringConstant")) {

            }
        }
        return false;
    }

    /**
     * Test whether current token is valid integer constant
     * @param token
     * @return
     */
    private boolean isValidIntegerConstant(Token token) {
        if(token != null) {
            if(Objects.equals(token.getType(), "integerConstant")) {
                try {
                    int value = Integer.parseInt(token.getValue());
                    if(value >= 0 && value <= 32767  ) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    /**
     * Generates a parse tree for an expression
     *
     * @return a ParseTree
     */
    public ParseTree compileExpression() throws ParseException {
        //term (op term)*


        ParseTree temp = compileTerm();
        //System.out.println(temp);
        if(temp != null) {
            ParseTree res = new ParseTree("expression","");
            res.addChild(temp);
            while (isValidOp(current())) {
                res.addChild(current());
                next();
                ParseTree temp1 = compileTerm();
                res.addChild(temp1);
            }
            return res;
        }

        return null;
    }

    /**
     * Generates a parse tree for an expression term
     *
     * @return a ParseTree
     */
    public ParseTree compileTerm() throws ParseException {
        if (isValidIntegerConstant(current()) ||
                isValidStringConstant(current()) ||
                isValidKeywordConstant(current())) { //integer/string/keyword constant
            ParseTree res = new ParseTree("term", "");
            res.addChild(current());
            next();
            return res;
        } else if (isValidIdentifier(current())) { //valid varName or varName[expression]
            ParseTree res = new ParseTree("term", "");
            res.addChild(current());
            next();
            if (have("symbol", "[")) { //array call
                res.addChild(current());
                next();
                ParseTree temp = compileExpression();
                if (temp != null) {
                    res.addChild(temp);
                }
                if (have("symbol", "]")) {
                    res.addChild(current());
                    next();
                }

            } else if(have("symbol","(")) { //subroutine call like functionName(expression list)
                res.addChild(current());
                next();
                ParseTree temp1 = compileExpressionList();
                if(temp1 != null) {
                    res.addChild(temp1);
                }
                if(have("symbol",")")) {
                    res.addChild(current());
                    next();
                    return res;
                }
            } else if(have("symbol",".")) { //subroutine call like className.functionName(expression list)
                res.addChild(current());
                next();
                //Main . myFunc ( 1 , Hello )
                if(isValidIdentifier(current())) {
                    res.addChild(current());
                    next();
                    if(have("symbol","(")) {
                        res.addChild(current());
                        next();
                        ParseTree temp1 = compileExpressionList();
                        if(temp1 != null) {
                            res.addChild(temp1);
                        }
                        if(have("symbol",")")) {
                            res.addChild(current());
                            next();
                            return res;
                        }
                    }
                }
            }
            return res;
        } else if (have("symbol","(")) {
            ParseTree res = new ParseTree("term","");
            res.addChild(current());
            next();
            ParseTree temp = compileExpression();
            if (temp != null) {
                res.addChild(temp);
            }
            if(have("symbol",")")) {
                res.addChild(current());
                next();
            }
            return res;
        } else if (isValidUnaryOp(current())) {//unary op term
            ParseTree res = new ParseTree("term","");
            res.addChild(current());
            next();
            ParseTree temp = compileTerm();
            if(temp != null) {
                res.addChild(temp);
            }
            return res;
        } else if (have("keyword", "skip")) {
//            ParseTree res = new ParseTree("term","");
//            res.addChild(current());
            ParseTree res = new ParseTree(current().getType(), current().getValue());
            next();
            return res;
        }
        return null;
    }

    /**
     * Compile Subroutine call
     * @return
     * @throws ParseException
     */
    public ParseTree compileSubroutineCall() throws ParseException {
        if(isValidIdentifier(current())) {
            ParseTree res = new ParseTree("subroutineCall","");
            res.addChild(current());
            next();
            if(have("symbol",".")) {
                res.addChild(current());
                next();
                if(isValidIdentifier(current())) {
                    res.addChild(current());
                    next();
                    if(have("symbol","(")) {
                        res.addChild(current());
                        next();
                        ParseTree temp = compileExpressionList();
                        if(temp != null) {
                            res.addChild(temp);
                        }
                        if(have("symbol",")")) {
                            res.addChild(current());
                            next();
                        }
                    }
                }
            } else {
                if(have("symbol","(")) {
                    res.addChild(current());
                    next();
                    ParseTree temp = compileExpressionList();
                    if(temp != null) {
                        res.addChild(temp);
                    }
                    if(have("symbol",")")) {
                        res.addChild(current());
                        next();
                        return res;
                    }
                } else {
                    throw new ParseException("At least one statement in Routine", 0);
                }
                //return res;
            }
        }
        return null;
    }

    /**
     * Generates a parse tree for an expression list
     *
     * @return a ParseTree
     */
    public ParseTree compileExpressionList() throws ParseException {
        ParseTree res = new ParseTree("expressionList","");
        ParseTree temp = compileExpression();
        if(temp != null) {
            res.addChild(temp);
        }
        while (have("symbol", ",")) {
            res.addChild(current());
            next();
            ParseTree temp1 = compileExpression();
            if(temp1 != null) {
                res.addChild(temp1);
            }
        }
        return res;
    }

    /**
     * Advance to the next token
     */
    public void next() {
        if (tokens.isEmpty()) {
            return;
        }
        tokens.removeFirst();
//        Iterator <Token> tokenIterator = tokens.iterator();
//        if(tokenIterator.hasNext()) {
//            //tokens = tokenIterator.next();
//        }
//        return;
    }

    /**
     * Return the current token
     *
     * @return the Token
     */
    public Token current() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.peekFirst();
    }

    /**
     * Check if the current token matches the expected type and value.
     *
     * @return true if a match, false otherwise
     */
    public boolean have(String expectedType, String expectedValue) {
        Token temp = current();
        if(temp != null) {
            return Objects.equals(temp.getType(), expectedType) && Objects.equals(temp.getValue(), expectedValue);
        }
        return false;
    }

    /**
     * Check if the current token matches the expected type and value.
     * If so, advance to the next token, returning the current token, otherwise throw/raise a ParseException.
     *
     * @return the current token before advancing
     */
    public Token mustBe(String expectedType, String expectedValue) {
        Token temp = current();
        if (Objects.equals(temp.getType(), expectedType) && Objects.equals(temp.getValue(), expectedValue)) {
            next();
            return temp;
        } else {
            return null;
        }
    }


    public static void main(String[] args) {

        /* Tokens for:
         *     class MyClass {
         *
         *     }
         */
        LinkedList<Token> tokens = new LinkedList<Token>();
        tokens.add(new Token("keyword", "class"));
        tokens.add(new Token("identifier", "MyClass"));
        tokens.add(new Token("symbol", "{"));
        tokens.add(new Token("keyword", "static"));
        tokens.add(new Token("keyword", "int"));
        tokens.add(new Token("identifier", "a"));
        tokens.add(new Token("symbol", ","));
        tokens.add(new Token("identifier", "b"));
        tokens.add(new Token("symbol", ";"));
        tokens.add(new Token("keyword", "field"));
        tokens.add(new Token("keyword", "char"));
        tokens.add(new Token("identifier", "b"));
        tokens.add(new Token("symbol", ";"));
//        tokens.add(new Token("symbol","}"));
//        tokens.add(new Token("keyword","function"));
//        tokens.add(new Token("keyword","void"));
//        tokens.add(new Token("identifier","myFunc"));
//        tokens.add(new Token("symbol","("));
//        tokens.add(new Token("keyword","int"));
//        tokens.add(new Token("identifier","a"));
//        tokens.add(new Token("symbol",")"));
//        tokens.add(new Token("symbol","{"));
//        tokens.add(new Token("keyword","var"));
//        tokens.add(new Token("keyword","int"));
//        tokens.add(new Token("identifier","a"));
//        tokens.add(new Token("symbol",";"));
//        tokens.add(new Token("keyword","let"));
//        tokens.add(new Token("identifier","a"));
//        tokens.add(new Token("symbol","="));
//        tokens.add(new Token("keyword","skip"));
//        tokens.add(new Token("symbol",";"));
//        tokens.add(new Token("keyword","if"));
//        tokens.add(new Token("symbol","("));
//        tokens.add(new Token("keyword","skip"));
//        tokens.add(new Token("symbol",")"));
//        tokens.add(new Token("symbol","{"));
//        tokens.add(new Token("keyword","let"));
//        tokens.add(new Token("identifier","x"));
//        tokens.add(new Token("symbol","="));
//        tokens.add(new Token("keyword","skip"));
//        tokens.add(new Token("symbol",";"));
//        tokens.add(new Token("symbol","}"));
//        tokens.add(new Token("symbol","}"));
        tokens.add(new Token("symbol", "}"));
        //System.out.println(tokens.get(0));
        CompilerParser parser = new CompilerParser(tokens);
        try {
            ParseTree result = parser.compileProgram();
            System.out.println(result);
        } catch (ParseException e) {
            System.out.println("Error Parsing!");
        }

    }

} 
