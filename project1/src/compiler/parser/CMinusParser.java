package compiler.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import compiler.scanner.*;

public class CMinusParser implements Parser {

    private CMinusScanner_jflex scan;

    private Token nextToken;
    private Token.TokenType nextType;

    public CMinusParser(CMinusScanner_jflex scanner) {
        scan = scanner;
        try {
  			scan.setNextToken(scan.scanToken());
			nextToken = new Token(scan.getTokenType(), scan.getTokenData());
			nextType = nextToken.getTokenType();
			
		} catch (ScannerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e1) {
			System.out.println("Error: file not found");
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Error: Unsupported file encoding");
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("Scanner Exception: An IOException " + "occurred while reading the input file.");
			e.printStackTrace();
		}
    }

    private Token advanceToken() {
        nextToken = scan.getNextToken();
        nextType = nextToken.getTokenType();
        return nextToken;
    }
    
    private Token viewNextToken(){
    	return scan.viewNextToken();
    }
    
    private Token matchToken(Token.TokenType matchType) throws ParserException {
    	if(nextToken.getTokenType() != matchType){
    		throw new ParserException(matchType, nextToken.getTokenType());
    	}
    	else{
    		Token returnToken = nextToken;
    		advanceToken();
    		return returnToken;
    	}
    }
    
    private Token matchTokenSet(Token.TokenType[] matchArray) throws ParserException{
    	if( !tokenTypeInArray(nextToken.getTokenType(), matchArray) ){
    		throw new ParserException(matchArray, nextToken.getTokenType());
    	}
    	else{
    		Token returnToken = nextToken;
    		advanceToken();
    		return returnToken;
    	}
    }
    
    private Token matchTokenNoAdvance(Token.TokenType matchType) throws ParserException{
    	if(nextToken.getTokenType() != matchType){
    		throw new ParserException(matchType, nextToken.getTokenType());
    	}
    	else{
    		return nextToken;
    	}
    }
    
    private Token matchTokenSetNoAdvance(Token.TokenType[] matchArray) throws ParserException{
    	if( !tokenTypeInArray(nextToken.getTokenType(), matchArray) ){
    		throw new ParserException(matchArray, nextToken.getTokenType());
    	}
    	else{
    		return nextToken;
    	}
    }
    
    private Token matchFollowToken(Token.TokenType matchType) throws ParserException {
    	if(advanceToken().getTokenType() != matchType){
    		throw new ParserException(matchType, nextToken.getTokenType());
    	}
    	else{
    		return nextToken;
    	}
    }
    
    private Token matchFollowTokenNoAdvance(Token.TokenType matchType) throws ParserException {
    	Token returnToken = viewNextToken(); 
    	if(returnToken.getTokenType() != matchType){
    		throw new ParserException(matchType, nextToken.getTokenType());
    	}
    	else{
    		return returnToken;
    	}
    }
    
    private Token matchFollowSet(Token.TokenType[] matchArray) throws ParserException{
    	if( !tokenTypeInArray(viewNextToken().getTokenType(), matchArray) ){
    		throw new ParserException(matchArray, nextToken.getTokenType());
    	}
    	else{
    		return advanceToken();
    	}
    }
    
    private Token matchFollowSetNoAdvance(Token.TokenType[] matchArray) throws ParserException{
    	Token returnToken = viewNextToken();
    	if( !tokenTypeInArray(returnToken.getTokenType(), matchArray) ){
    		throw new ParserException(matchArray, nextToken.getTokenType());
    	}
    	else{
    		return returnToken;
    	}
    }
    
    private boolean tokenTypeInArray(Token.TokenType toCheck, Token.TokenType[] array){
    	for(Token.TokenType pattern: array){
    		if(toCheck == pattern){
    			return true;
    		}
    	}
    	return false;
    }

    @Override
    public Program parse() throws ParserException {
        ArrayList<Declarations> declList = new ArrayList<Declarations>();
        advanceToken();
        declList.add(parseDecl());
        while (nextType == Token.TokenType.INT_TOKEN | nextType == Token.TokenType.VOID_TOKEN) {
            declList.add(parseDecl());
        }
        if (nextType != Token.TokenType.EOF_TOKEN) {
            throw new ParserException("There are no more declarations, but the end of file was not reached.");
        }
        return new Program(declList);
    }

    private Declarations parseDecl() throws ParserException {
        String id;
        
        switch (nextType) {
            case INT_TOKEN:
            	matchFollowToken(Token.TokenType.ID_TOKEN);
                id = nextToken.getTokenData().toString();
                advanceToken();
                return parseDeclPrime(id);
            case VOID_TOKEN:
            	matchFollowToken(Token.TokenType.ID_TOKEN);
                id = nextToken.getTokenData().toString();
                advanceToken();
                return parseFunDecl(nextType, id);
            default:
            	throw new ParserException(new Token.TokenType[]{
                		Token.TokenType.VOID_TOKEN, 
                		Token.TokenType.INT_TOKEN}, nextType);
        }
    }

    private FunDeclaration parseFunDecl(Token.TokenType typespec, String id) throws ParserException {
        ArrayList<Param> ps;
        CompoundStatement cs;
        matchToken(Token.TokenType.LEFTPAREN_TOKEN);
        ps = parseParams();
        matchToken(Token.TokenType.RIGHTPAREN_TOKEN);

        cs = parseCompoundStmt();
        return new FunDeclaration(typespec, id, ps, cs);
    }

    private Declarations parseDeclPrime(String id) throws ParserException {
    	switch(nextType){
    		case LEFTSQBRACKET_TOKEN:
				int num = 0;
	            advanceToken();
	            if (nextToken.getTokenType() == Token.TokenType.NUM_TOKEN){
	            	num = (int) nextToken.getTokenData();
	            } else {
	            	throw new ParserException("Unable to parse declaration. Was expecting a num token");
	            }
	            advanceToken();
	            matchToken(Token.TokenType.RIGHTSQBRACKET_TOKEN);
	            matchToken(Token.TokenType.SEMICOLON_TOKEN);
	            
	            return new VarDeclaration(id, num);
            
        case SEMICOLON_TOKEN:
            advanceToken();
            return new VarDeclaration(id);
            
        case LEFTPAREN_TOKEN:
            return parseFunDeclPrime(Token.TokenType.INT_TOKEN, id);
            
        default:
            throw new ParserException(new Token.TokenType[]{
            		Token.TokenType.LEFTSQBRACKET_TOKEN, 
            		Token.TokenType.SEMICOLON_TOKEN,
            		Token.TokenType.LEFTPAREN_TOKEN}, nextType, Declarations.class);
        }

    }

    private FunDeclaration parseFunDeclPrime(Token.TokenType typeSpec, String id) throws ParserException {
        matchToken(Token.TokenType.LEFTPAREN_TOKEN);
        ArrayList<Param> params = parseParams();
        matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
        CompoundStatement cs = parseCompoundStmt();
        return new FunDeclaration(typeSpec, id, params, cs);
    }

    private ArrayList<Param> parseParams() throws ParserException {
        ArrayList<Param> alp = new ArrayList<Param>();
        switch(nextType){
        case VOID_TOKEN:
            advanceToken();
            return alp;

        case INT_TOKEN:
	        alp.add(parseParam());
	        while (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
	            alp.add(parseParam());
	        }
	        return alp;
        default:
        	throw new ParserException(new Token.TokenType[]{
        			Token.TokenType.VOID_TOKEN, 
        			Token.TokenType.INT_TOKEN}, nextType);
        }
    }

    private Param parseParam() throws ParserException {
        matchToken(Token.TokenType.INT_TOKEN);
        matchTokenNoAdvance(Token.TokenType.ID_TOKEN);
        String id = nextToken.getTokenData().toString();
        advanceToken();
        if (nextType == Token.TokenType.LEFTSQBRACKET_TOKEN) {
            advanceToken();
            matchToken(Token.TokenType.RIGHTSQBRACKET_TOKEN);
        }
        if (nextType == Token.TokenType.COMMA_TOKEN
                || nextType == Token.TokenType.RIGHTPAREN_TOKEN) {
            if (nextType == Token.TokenType.COMMA_TOKEN) {
                advanceToken();
            }
            return new Param(id, false);
        } else {
            throw new ParserException("Parsing Param: Expected , or ), got " + nextType.toString());
        }

    }

    private CompoundStatement parseCompoundStmt() throws ParserException {
        ArrayList<VarDeclaration> localDecls = new ArrayList<VarDeclaration>();
        ArrayList<Statement> stmtList = new ArrayList<Statement>();
        matchToken(Token.TokenType.LEFTCURLYBRACE_TOKEN);
        if (nextType == Token.TokenType.INT_TOKEN) {
            localDecls = parseLocalDecls();
        }
        if ( tokenTypeInArray(nextType, 
        		new Token.TokenType[]{
		        	Token.TokenType.SEMICOLON_TOKEN,
		            Token.TokenType.LEFTPAREN_TOKEN,
		            Token.TokenType.NUM_TOKEN,
		            Token.TokenType.ID_TOKEN,
		            Token.TokenType.LEFTCURLYBRACE_TOKEN,
		            Token.TokenType.IF_TOKEN,
		            Token.TokenType.WHILE_TOKEN,
		            Token.TokenType.RETURN_TOKEN
		        } 
        ) ) {
            stmtList = parseStmtList();
            matchToken(Token.TokenType.RIGHTCURLYBRACE_TOKEN);
            return new CompoundStatement(localDecls, stmtList);
        } else{
            matchToken(Token.TokenType.RIGHTCURLYBRACE_TOKEN);
            return new CompoundStatement(localDecls, stmtList);
        }
    }

    private ArrayList<VarDeclaration> parseLocalDecls() throws ParserException {
        ArrayList<VarDeclaration> returnList = new ArrayList<VarDeclaration>();
        while ( !tokenTypeInArray(nextType, 
        		new Token.TokenType[]{
		        	Token.TokenType.SEMICOLON_TOKEN,
		            Token.TokenType.LEFTPAREN_TOKEN,
		            Token.TokenType.NUM_TOKEN,
		            Token.TokenType.ID_TOKEN,
		            Token.TokenType.LEFTCURLYBRACE_TOKEN,
		            Token.TokenType.IF_TOKEN,
		            Token.TokenType.WHILE_TOKEN,
		            Token.TokenType.RETURN_TOKEN,
		            Token.TokenType.RIGHTCURLYBRACE_TOKEN
		        } 
		)) {
            returnList.add(parseVarDecl());
        }
        return returnList;
    }

    private VarDeclaration parseVarDecl() throws ParserException {
        String returnID;
        int returnNum;
        VarDeclaration returnVarDecl = null;
        matchToken(Token.TokenType.INT_TOKEN);
        matchTokenNoAdvance(Token.TokenType.ID_TOKEN);
        returnID = nextToken.getTokenData().toString();
        advanceToken();
        switch (nextType) {
            case SEMICOLON_TOKEN:
                returnVarDecl = new VarDeclaration(returnID);
                advanceToken();
                break;
            case LEFTSQBRACKET_TOKEN:
                advanceToken();
                if (nextType == Token.TokenType.NUM_TOKEN) {
                    returnNum = (int) nextToken.getTokenData();
                    advanceToken();
                    if (nextType != Token.TokenType.RIGHTSQBRACKET_TOKEN) {
                        throw new ParserException("Parsing VarDecl: Expected ], got " + nextType.toString());
                    }
                    advanceToken();
                    returnVarDecl = new VarDeclaration(returnID, returnNum);
                    break;
                } else {
                    throw new ParserException("Parsing VarDecl: Expected NUM, got " + nextType.toString());
                }
            default:
                throw new ParserException("Parsing VarDecl: Expected [ or ;, got " + nextType.toString());
        }
        return returnVarDecl;
    }

    private ArrayList<Statement> parseStmtList() throws ParserException {
        ArrayList<Statement> returnList = new ArrayList<Statement>();
        while (nextType != Token.TokenType.RIGHTCURLYBRACE_TOKEN) {
            returnList.add(parseStatement());
        }
        return returnList;
    }

    private Statement parseStatement() throws ParserException {
        Statement returnStatement = null;
        switch (nextType) {
            case IF_TOKEN:
                returnStatement = parseIf();
                break;
            case WHILE_TOKEN:
                advanceToken();
                returnStatement = parseWhile();
                break;
            case RETURN_TOKEN:
                advanceToken();
                returnStatement = parseReturn();
                break;
            case NUM_TOKEN:
            case ID_TOKEN:
            case LEFTPAREN_TOKEN:
            case SEMICOLON_TOKEN:
                returnStatement = parseExprStmt();
                break;
            case LEFTCURLYBRACE_TOKEN:
                returnStatement = parseCompoundStmt();
                break;
            default:
                throw new ParserException("Parsing Statement: Expected IF, WHILE,"
                        + " NUM, ID, RETURN, (, ;, or {, got " + nextType.toString());
        }
        return returnStatement;
    }

    private IfStatement parseIf() throws ParserException {
    	advanceToken();
        matchToken(Token.TokenType.LEFTPAREN_TOKEN);
        Expression e = parseExpression();
        matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
        Statement s = parseStatement();
        Statement elseStatement = null;
        if (nextType == Token.TokenType.ELSE_TOKEN) {
            advanceToken();
            elseStatement = parseStatement();
        }
        IfStatement ifStatement = new IfStatement(e, s, elseStatement);
        return ifStatement;
    }

    private WhileStatement parseWhile() throws ParserException {
        matchToken(Token.TokenType.LEFTPAREN_TOKEN);
        Expression e = parseExpression();
        matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
        Statement s = parseStatement();
        return new WhileStatement(e, s);
    }

    private ReturnStatement parseReturn() throws ParserException {
        Expression e = null;
        switch (nextType) {
            case LEFTPAREN_TOKEN:
            case ID_TOKEN:
            case NUM_TOKEN:
                e = parseExpression();
                matchToken(Token.TokenType.SEMICOLON_TOKEN);
                break;
            case SEMICOLON_TOKEN:
                advanceToken();
                break;
            default:
                throw new ParserException("Parsing ReturnStmt: Expected (, ID, NUM, or ;, got " + nextType.toString());
        }
        return new ReturnStatement(e);
    }

    private ExpressionStatement parseExprStmt() throws ParserException {
        Expression e = null;
        switch (nextType) {
            case LEFTPAREN_TOKEN:
            case ID_TOKEN:
            case NUM_TOKEN:
                e = parseExpression();
                matchToken(Token.TokenType.SEMICOLON_TOKEN);
                break;
            case SEMICOLON_TOKEN:
                advanceToken();
                break;
            default:
                throw new ParserException("Parsing ExprStmt: Expected (, ID, NUM, or ;, got " + nextType.toString());
        }
        return new ExpressionStatement(e);
    }

    private Expression parseExpression() throws ParserException {
        Expression e;
        Object data;
        switch (nextType) {
            case LEFTPAREN_TOKEN:
                advanceToken();
                e = parseExpression();
                matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
                Expression exp = parseSimExpr(e);
                return exp;
            case NUM_TOKEN:
                data = nextToken.getTokenData();
                advanceToken();
                return parseSimExpr(new NumExpression((int) data));
            case ID_TOKEN:
                data = nextToken.getTokenData();
                advanceToken();
                Expression varExpression = new VarExpressions((String) data);
                return parseExprPrime(varExpression);
            default:
                throw new ParserException("Parsing Expr: Expected (, NUM, or ID, got " + nextType.toString());
        }
    }

    private Expression parseExprPrime(Expression e) throws ParserException {
        switch (nextType) {
            case ASSIGN_TOKEN:
                advanceToken();
                return new AssignExpression((VarExpressions) e, parseExpression());
            case LEFTPAREN_TOKEN:
                advanceToken();
                ArrayList<Expression> args = parseArgs();
                CallExpression ce = new CallExpression(((VarExpressions) e).getVar(), args);
                return parseSimExpr(ce);
            case LEFTSQBRACKET_TOKEN:
                advanceToken();
                Expression inBrackets = parseExpression();
                matchToken(Token.TokenType.RIGHTSQBRACKET_TOKEN);
                return parseExprDP(new VarExpressions(((VarExpressions) e).getVar(), inBrackets));
            case MULTIPLY_TOKEN:
            case DIVIDE_TOKEN:
            case PLUS_TOKEN:
            case MINUS_TOKEN:
            case LESSTHANEQUALTO_TOKEN:
            case LESSTHAN_TOKEN:
            case GREATERTHAN_TOKEN:
            case GREATERTHANEQUALTO_TOKEN:
            case EQUALTO_TOKEN:
            case NOTEQUAL_TOKEN:
            	Expression simExpression = parseSimExpr(e);
                return simExpression;
            case RIGHTPAREN_TOKEN:
            case COMMA_TOKEN:
            case SEMICOLON_TOKEN:
            case RIGHTSQBRACKET_TOKEN:
            	return e;
            default:
                throw new ParserException ("Error parsing expression'");
        }
    }

    private Expression parseExprDP(VarExpressions e) throws ParserException {
        switch (nextType) {
            case MULTIPLY_TOKEN:
            case DIVIDE_TOKEN:
            case PLUS_TOKEN:
            case MINUS_TOKEN:
            case LESSTHANEQUALTO_TOKEN:
            case LESSTHAN_TOKEN:
            case GREATERTHAN_TOKEN:
            case GREATERTHANEQUALTO_TOKEN:
            case EQUALTO_TOKEN:
            case NOTEQUAL_TOKEN:
                return parseSimExpr(e);
            default:
            	return e;
        }
    }

    private Expression parseSimExpr(Expression e) throws ParserException {
        Expression addExprPrime = parseAddExprPrime(e);
        BinaryExpression.operation o;
        switch (nextType) {
            case LESSTHAN_TOKEN:
                o = BinaryExpression.operation.LESSTHAN;
                advanceToken();
                break;
            case GREATERTHAN_TOKEN:
                o = BinaryExpression.operation.GREATERTHAN;
                advanceToken();
                break;
            case EQUALTO_TOKEN:
                o = BinaryExpression.operation.EQUALTO;
                advanceToken();
                break;
            case NOTEQUAL_TOKEN:
                o = BinaryExpression.operation.NOTEQUAL;
                advanceToken();
                break;
            case LESSTHANEQUALTO_TOKEN:
                o = BinaryExpression.operation.LESSTHANEQUALTO;
                advanceToken();
                break;
            case GREATERTHANEQUALTO_TOKEN:
                o = BinaryExpression.operation.GREATERTHANEQUALTO;
                advanceToken();
                break;
            default:
                return addExprPrime;
        }
        //advanceToken();
        Expression addExpr = parseAddExpr();
        Expression binExpr = new BinaryExpression(addExprPrime, addExpr, o);
        return binExpr;
    }

    private Expression parseAddExprPrime(Expression e) throws ParserException {
    	BinaryExpression.operation o = null;
    	Expression tp = parseTermPrime(e);

    	if (nextType == Token.TokenType.PLUS_TOKEN
    			|| nextType == Token.TokenType.MINUS_TOKEN) {
    		if (nextType == Token.TokenType.PLUS_TOKEN) {
    			o = BinaryExpression.operation.PLUS;
    		} else if (nextType == Token.TokenType.MINUS_TOKEN) {
    			o = BinaryExpression.operation.MINUS;
    		}
    		advanceToken();
            BinaryExpression be = new BinaryExpression(tp, parseTerm(), o);
            while (nextType == Token.TokenType.PLUS_TOKEN
                    || nextType == Token.TokenType.MINUS_TOKEN) {
                if (nextType == Token.TokenType.PLUS_TOKEN) {
                    o = BinaryExpression.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS_TOKEN) {
                    o = BinaryExpression.operation.MINUS;
                }
                be = new BinaryExpression(be, parseTerm(), o);
            }
            return be;
        } else {
            return tp;
        }
    }

    private Expression parseAddExpr() throws ParserException {
    	Expression term = parseTerm();
        BinaryExpression.operation o = null;
        Expression be = term;
        if (nextType == Token.TokenType.PLUS_TOKEN
                || nextType == Token.TokenType.MINUS_TOKEN) {
            while (nextType == Token.TokenType.PLUS_TOKEN
                    || nextType == Token.TokenType.MINUS_TOKEN) {
                if (nextType == Token.TokenType.PLUS_TOKEN) {
                    o = BinaryExpression.operation.PLUS;
                    advanceToken();
                } else if (nextType == Token.TokenType.MINUS_TOKEN) {
                    o = BinaryExpression.operation.MINUS;
                    advanceToken();
                }
                term = parseTerm();
                be = new BinaryExpression(be, term, o);
            }
            return be;
        }
        return term;
    }

    private Expression parseTerm() throws ParserException {
        //term -> factor { mulop factor }
        Expression lhs = parseFactor();

        BinaryExpression.operation o = null;
        BinaryExpression be;
        if (nextType == Token.TokenType.MULTIPLY_TOKEN
                || nextType == Token.TokenType.DIVIDE_TOKEN) {
            if (nextType == Token.TokenType.MULTIPLY_TOKEN) {
                o = BinaryExpression.operation.MULTIPLY;
            } else if (nextType == Token.TokenType.DIVIDE_TOKEN) {
                o = BinaryExpression.operation.DIVIDE;
            }
            advanceToken();
            be = new BinaryExpression(lhs, parseFactor(), o);
            while (nextType == Token.TokenType.MULTIPLY_TOKEN
                    || nextType == Token.TokenType.DIVIDE_TOKEN) {
                if (nextType == Token.TokenType.MULTIPLY_TOKEN) {
                    o = BinaryExpression.operation.MULTIPLY;
                } else if (nextType == Token.TokenType.DIVIDE_TOKEN) {
                    o = BinaryExpression.operation.DIVIDE;
                }
                advanceToken();
                be = new BinaryExpression(be, parseFactor(), o);
                //advanceToken();
            }
            return be;
        } else {
            return lhs;
        }
    }

    private Expression parseTermPrime(Expression e) throws ParserException {

        //term -> factor { mulop factor }
    	BinaryExpression.operation o = null;
    	BinaryExpression be = null;
        if (nextType == Token.TokenType.MULTIPLY_TOKEN
                || nextType == Token.TokenType.DIVIDE_TOKEN) {
            while (nextType == Token.TokenType.MULTIPLY_TOKEN
                    || nextType == Token.TokenType.DIVIDE_TOKEN) {
                if (nextType == Token.TokenType.MULTIPLY_TOKEN) {
                    o = BinaryExpression.operation.MULTIPLY;
                } else if (nextType == Token.TokenType.DIVIDE_TOKEN) {
                    o = BinaryExpression.operation.DIVIDE;
                }
                advanceToken();
                be = new BinaryExpression(e, parseFactor(), o);
                //advanceToken();
            }
            return be;
        } else {
            return e;
        }
    }

    private Expression parseFactor() throws ParserException {
        switch (nextType) {
            case LEFTPAREN_TOKEN:
                advanceToken();
                Expression e = parseExpression();
                advanceToken();
                return e;
            case ID_TOKEN:
                String id = (String) nextToken.getTokenData();
                advanceToken();
                return parseVarcall(id);
            case NUM_TOKEN:
                int num = (int) nextToken.getTokenData();
                advanceToken();
                return new NumExpression(num);
            default:
                throw new ParserException("Parsing Factor: Expected (, ID, or NUM, got " + nextType.toString());
        }
    }

    private Expression parseVarcall(String id) throws ParserException {
        switch (nextType) {
            case LEFTPAREN_TOKEN:
                advanceToken();
                CallExpression e = new CallExpression(id, parseArgs());
                advanceToken();
                if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
                    throw new ParserException("Parsing Varcall: Expected ), got " + nextType.toString());
                }
                return e;
            default:
                return parseVarPrime(id);
        }
    }

    private Expression parseVarPrime(String id) throws ParserException {
        if (nextType == Token.TokenType.LEFTSQBRACKET_TOKEN) {
            advanceToken();
            Expression e = parseExpression();
            advanceToken();
            return new VarExpressions(id, e);
        } else {
            return new VarExpressions(id);
        }
    }

    private ArrayList<Expression> parseArgs() throws ParserException {
        if (nextType == Token.TokenType.LEFTPAREN_TOKEN
                || nextType == Token.TokenType.NUM_TOKEN
                || nextType == Token.TokenType.ID_TOKEN) {
        	ArrayList<Expression> argList = new ArrayList<Expression> ();
        	argList = parseArgList();
        	matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
            return argList;
        } else {
        	matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
            return new ArrayList();
        }
    }

    private ArrayList<Expression> parseArgList() throws ParserException {
        ArrayList<Expression> eList = new ArrayList<>();
        eList.add(parseExpression());
        while (nextType == Token.TokenType.COMMA_TOKEN) {
            advanceToken();
            eList.add(parseExpression());
        }
        return eList;
    }
}
