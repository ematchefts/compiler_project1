package compiler.parser;

import java.util.*;
import compiler.scanner.*;

public class CMinusParser implements Parser {

    private CMinusScanner_jflex scan;

    private Token nextToken;
    private Token.TokenType nextType;

    public CMinusParser(CMinusScanner_jflex scanner) {
        scan = scanner;
    }

    private Token advanceToken() {
        nextToken = scan.getNextToken();
        nextType = nextToken.getTokenType();
        return nextToken;
    }

    @Override
    public Program parse() throws ParserException {
        ArrayList<Declarations> declList = new ArrayList();
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
        Declarations returnDecl;
        Token.TokenType typespec;
        String id;
        switch (nextType) {
            case INT_TOKEN:
                typespec = Token.TokenType.INT_TOKEN;
                break;
            case VOID_TOKEN:
                typespec = Token.TokenType.VOID_TOKEN;
                break;
            default:
                throw new ParserException("Parsing decl: Expected int or void, got " + nextType.toString());
        }
        advanceToken();
        if (nextType == Token.TokenType.ID_TOKEN) {
            id = nextToken.getTokenData().toString();
        } else {
            throw new ParserException("Parsing decl: Expected id, got " + nextType.toString());
        }
        advanceToken();
        switch (typespec) {
            case VOID_TOKEN:
                FunDeclaration fd = parseFunDecl(typespec, id);
                returnDecl = fd;
                break;
            case INT_TOKEN:
                Declarations dp = parseDeclPrime(id);
                returnDecl = dp;
                break;
            default:
                throw new ParserException(new Token.TokenType[]{
                		Token.TokenType.VOID_TOKEN, 
                		Token.TokenType.INT_TOKEN}, typespec);
        }
        return returnDecl;
    }

    private FunDeclaration parseFunDecl(Token.TokenType typespec, String id) throws ParserException {
        ArrayList<Param> ps;
        CompoundStatement cs;
        if (nextType != Token.TokenType.LEFTPAREN_TOKEN) {
            throw new ParserException("Parsing fun-decl: Expected (, got " + nextType.toString());
        }
        advanceToken();
        ps = parseParams();
        if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
            throw new ParserException("Parsing fun-decl: Expected ), got " + nextType.toString());
        }
        advanceToken();

        cs = parseCompoundStmt();
        return new FunDeclaration(typespec, id, ps, cs);
    }

    private Declarations parseDeclPrime(String id) throws ParserException {
        if (nextType == Token.TokenType.LEFTSQBRACKET_TOKEN) {
            advanceToken();
            int num;
            if (nextType == Token.TokenType.NUM_TOKEN) {
                num = (int) nextToken.getTokenData();
            } else {
                throw new ParserException(Token.TokenType.NUM_TOKEN, nextType, Declarations.class);
            }
            advanceToken();
            if (nextType == Token.TokenType.RIGHTSQBRACKET_TOKEN) {
                advanceToken();
            } else {
                throw new ParserException(Token.TokenType.RIGHTSQBRACKET_TOKEN, nextType, Declarations.class);
            }
            if (nextType == Token.TokenType.SEMICOLON_TOKEN) {
                advanceToken();
            } else {
                throw new ParserException(Token.TokenType.SEMICOLON_TOKEN, nextType, Declarations.class);
            }
            return new VarDeclaration(id, num);
        } else if (nextType == Token.TokenType.SEMICOLON_TOKEN) {
            advanceToken();
            return new VarDeclaration(id);
        } else if (nextType == Token.TokenType.LEFTPAREN_TOKEN) {
            return parseFunDeclPrime(Token.TokenType.NUM_TOKEN, id);
        } else {
            throw new ParserException(new Token.TokenType[]{
            		Token.TokenType.LEFTSQBRACKET_TOKEN, 
            		Token.TokenType.SEMICOLON_TOKEN,
            		Token.TokenType.LEFTPAREN_TOKEN}, nextType, Declarations.class);
        }

    }

    private FunDeclaration parseFunDeclPrime(Token.TokenType typeSpec, String id) throws ParserException {
        if (nextType == Token.TokenType.LEFTPAREN_TOKEN) {
            advanceToken();
            ArrayList<Param> params = parseParams();
            if (nextType == Token.TokenType.RIGHTPAREN_TOKEN) {
                advanceToken();
                CompoundStatement cs = parseCompoundStmt();
                return new FunDeclaration(typeSpec, id, params, cs);
            } else {
                throw new ParserException(Token.TokenType.LEFTPAREN_TOKEN, nextType, FunDeclaration.class);
            }
        } else {
        	throw new ParserException(Token.TokenType.LEFTPAREN_TOKEN, nextType, FunDeclaration.class);
        }
    }

    private ArrayList<Param> parseParams() throws ParserException {
        ArrayList<Param> alp = new ArrayList();
        //advanceToken();
        if (nextType == Token.TokenType.VOID_TOKEN) {
            advanceToken();
            return alp;
        }
        alp.add(parseParam());
        while (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
            alp.add(parseParam());
        }
        return alp;
    }

    private Param parseParam() throws ParserException {
        Param p;
        if (nextType == Token.TokenType.INT_TOKEN) {
            advanceToken();
            if (nextType == Token.TokenType.ID_TOKEN) {
                String id = nextToken.getTokenData().toString();
                advanceToken();
                if (nextType == Token.TokenType.LEFTSQBRACKET_TOKEN) {
                    advanceToken();
                    if (nextType == Token.TokenType.RIGHTSQBRACKET_TOKEN) {
                        advanceToken();
                        return new Param(id, true);
                    } else {
                        throw new ParserException(Token.TokenType.RIGHTSQBRACKET_TOKEN, nextType, Param.class);
                    }
                } else if (nextType == Token.TokenType.COMMA_TOKEN
                        || nextType == Token.TokenType.RIGHTPAREN_TOKEN) {
                    if (nextType == Token.TokenType.COMMA_TOKEN) {
                        advanceToken();
                    }
                    return new Param(id, false);
                } else {
                    throw new ParserException("Parsing Param: Expected , or ), got " + nextType.toString());
                }
            } else {
                throw new ParserException("Parsing Param: Expected IDENT, got " + nextType.toString());
            }
        } else {
            throw new ParserException("Parsing Param: Expected INT, got " + nextType.toString());
        }

    }

    private CompoundStatement parseCompoundStmt() throws ParserException {
        ArrayList<VarDeclaration> localDecls = new ArrayList<>();
        ArrayList<Statement> stmtList = new ArrayList<>();
        if (nextType == Token.TokenType.LEFTCURLYBRACE_TOKEN) {
            advanceToken();
        } else {
            throw new ParserException("Parsing CompoundStatement: Expected {, got " + nextType.toString());
        }
        if (nextType == Token.TokenType.INT_TOKEN
                || nextType == Token.TokenType.VOID_TOKEN) {
            localDecls = parseLocalDecls();
        }
        if (nextType == Token.TokenType.SEMICOLON_TOKEN
                || nextType == Token.TokenType.LEFTPAREN_TOKEN
                || nextType == Token.TokenType.NUM_TOKEN
                || nextType == Token.TokenType.ID_TOKEN
                || nextType == Token.TokenType.LEFTCURLYBRACE_TOKEN
                || nextType == Token.TokenType.IF_TOKEN
                || nextType == Token.TokenType.WHILE_TOKEN
                || nextType == Token.TokenType.RETURN_TOKEN) {
            stmtList = parseStmtList();
            if (nextType == Token.TokenType.RIGHTCURLYBRACE_TOKEN) {
                advanceToken();
                return new CompoundStatement(localDecls, stmtList);
            } else {
                throw new ParserException("Parsing CompoundStatement: Expected }, got " + nextType.toString());
            }
        } else if (nextType == Token.TokenType.RIGHTCURLYBRACE_TOKEN) {
            advanceToken();
            return new CompoundStatement(localDecls, stmtList);
        } else {
            throw new ParserException("Parsing CompoundStatement: Expected {, got " + nextType.toString());
        }
    }

    private ArrayList<VarDeclaration> parseLocalDecls() throws ParserException {
        ArrayList<VarDeclaration> returnList = new ArrayList();
        while (nextType != Token.TokenType.IF_TOKEN
                && nextType != Token.TokenType.ID_TOKEN
                && nextType != Token.TokenType.NUM_TOKEN
                && nextType != Token.TokenType.WHILE_TOKEN
                && nextType != Token.TokenType.RETURN_TOKEN
                && nextType != Token.TokenType.LEFTPAREN_TOKEN
                && nextType != Token.TokenType.LEFTCURLYBRACE_TOKEN
                && nextType != Token.TokenType.SEMICOLON_TOKEN
                && nextType != Token.TokenType.RIGHTCURLYBRACE_TOKEN) {
            returnList.add(parseVarDecl());
        }
        return returnList;
    }

    private VarDeclaration parseVarDecl() throws ParserException {
        String returnID;
        int returnNum;
        VarDeclaration returnVarDecl = null;
        if (nextType == Token.TokenType.INT_TOKEN) {
            advanceToken();
            if (nextType == Token.TokenType.ID_TOKEN) {
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
            } else {
                throw new ParserException("Parsing VarDecl: Expected ID, got " + nextType.toString());
            }
        } else {
            throw new ParserException("Parsing VarDecl: Expected INT, got " + nextType.toString());
        }
        return returnVarDecl;
    }

    private ArrayList<Statement> parseStmtList() throws ParserException {
        ArrayList<Statement> returnList = new ArrayList();
        while (nextType != Token.TokenType.RIGHTCURLYBRACE_TOKEN) {
            returnList.add(parseStatement());
            //advanceToken();
        }
        return returnList;
    }

    private Statement parseStatement() throws ParserException {
        Statement returnStatement = null;
        switch (nextType) {
            case IF_TOKEN:
                advanceToken();
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
        if (nextType != Token.TokenType.LEFTPAREN_TOKEN) {
            throw new ParserException("Parsing IfStmt: Expected (, got " + nextType.toString());
        }
        advanceToken();
        Expression e = parseExpression();
        if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
            throw new ParserException("Parsing IfStmt: Expected ), got " + nextType.toString());
        }
        advanceToken();
        Statement s = parseStatement();
        Statement elseStatement = null;
        if (nextType == Token.TokenType.ELSE_TOKEN) {
            advanceToken();
            elseStatement = parseStatement();
        }
        return new IfStatement(e, s, elseStatement);
    }

    private WhileStatement parseWhile() throws ParserException {
        if (nextType != Token.TokenType.LEFTPAREN_TOKEN) {
            throw new ParserException("Parsing WhileStmt: Expected (, got " + nextType.toString());
        }
        advanceToken();
        Expression e = parseExpression();
        if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
            throw new ParserException("Parsing WhileStmt: Expected ), got " + nextType.toString());
        }
        advanceToken();
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
                advanceToken();
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
                if (nextType != Token.TokenType.SEMICOLON_TOKEN) {
                    throw new ParserException("Parsing ExprStmt: Expected ;, got " + nextType.toString());
                }
                advanceToken();
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
                advanceToken();
                if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
                    throw new ParserException("Parsing Expr: Expected ), got " + nextType.toString());
                }
                return parseSimExpr(e);
            case NUM_TOKEN:
                data = nextToken.getTokenData();
                advanceToken();
                return parseSimExpr(new NumExpression((int) data));
            case ID_TOKEN:
                data = nextToken.getTokenData();
                advanceToken();
                return parseExprPrime(new VarExpressions((String) data));
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
                CallExpression ce = new CallExpression(((VarExpressions) e).getVar(), parseArgs());
                if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
                    throw new ParserException("Parsing Expr': Expected ), got " + nextType.toString());
                }
                advanceToken();
                return parseSimExpr(ce);
            case LEFTSQBRACKET_TOKEN:
                advanceToken();
                Expression inBrackets = parseExpression();
                advanceToken();
                if (nextType != Token.TokenType.RIGHTSQBRACKET_TOKEN) {
                    throw new ParserException("Parsing Expr': Expected ], got " + nextType.toString());
                }
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
                return parseSimExpr(e);
            default:
                return e;
        }
    }

    private Expression parseExprDP(VarExpressions e) throws ParserException {
        switch (nextType) {
            case ASSIGN_TOKEN:
                advanceToken();
                return new AssignExpression(e, parseExpression());
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
                break;
            case GREATERTHAN_TOKEN:
                o = BinaryExpression.operation.GREATERTHAN;
                break;
            case EQUALTO_TOKEN:
                o = BinaryExpression.operation.EQUALTO;
                break;
            case NOTEQUAL_TOKEN:
                o = BinaryExpression.operation.NOTEQUAL;
                break;
            case LESSTHANEQUALTO_TOKEN:
                o = BinaryExpression.operation.LESSTHANEQUALTO;
                break;
            case GREATERTHANEQUALTO_TOKEN:
                o = BinaryExpression.operation.GREATERTHANEQUALTO;
                break;
            default:
                return addExprPrime;
        }
        advanceToken();
        return new BinaryExpression(addExprPrime, parseAddExpr(), o);
    }

    private Expression parseAddExprPrime(Expression e) throws ParserException {
        BinaryExpression.operation o = null;
        Expression tp;
        if (nextType == Token.TokenType.MULTIPLY_TOKEN
                || nextType == Token.TokenType.DIVIDE_TOKEN) {
            //advanceToken();
            tp = parseTermPrime(e);
            Expression be = tp;
            //advanceToken();
            while (nextType == Token.TokenType.PLUS_TOKEN
                    || nextType == Token.TokenType.MINUS_TOKEN) {
                if (nextType == Token.TokenType.PLUS_TOKEN) {
                    o = BinaryExpression.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS_TOKEN) {
                    o = BinaryExpression.operation.MINUS;
                }
                advanceToken();
                be = new BinaryExpression(be, parseTerm(), o);
                //advanceToken();
            }
            return be;
        } else if (nextType == Token.TokenType.PLUS_TOKEN
                || nextType == Token.TokenType.MINUS_TOKEN) {
            if (nextType == Token.TokenType.PLUS_TOKEN) {
                o = BinaryExpression.operation.PLUS;
            } else if (nextType == Token.TokenType.MINUS_TOKEN) {
                o = BinaryExpression.operation.MINUS;
            }
            advanceToken();
            BinaryExpression be = new BinaryExpression(e, parseTerm(), o);
            //advanceToken();
            while (nextType == Token.TokenType.PLUS_TOKEN
                    || nextType == Token.TokenType.MINUS_TOKEN) {
                if (nextType == Token.TokenType.PLUS_TOKEN) {
                    o = BinaryExpression.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS_TOKEN) {
                    o = BinaryExpression.operation.MINUS;
                }
                be = new BinaryExpression(be, parseTerm(), o);
                advanceToken();
            }
            return be;
        } else {
            return e;
        }
    }

    private Expression parseAddExpr() throws ParserException {
        Expression term = parseTerm();
        //advanceToken();
        BinaryExpression.operation o = null;
        Expression be = term;
        if (nextType == Token.TokenType.PLUS_TOKEN
                || nextType == Token.TokenType.MINUS_TOKEN) {
            while (nextType == Token.TokenType.PLUS_TOKEN
                    || nextType == Token.TokenType.MINUS_TOKEN) {
                if (nextType == Token.TokenType.PLUS_TOKEN) {
                    o = BinaryExpression.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS_TOKEN) {
                    o = BinaryExpression.operation.MINUS;
                }
                be = new BinaryExpression(be, parseTerm(), o);
                advanceToken();
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
                be = new BinaryExpression(lhs, parseFactor(), o);
                advanceToken();
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
            return parseArgList();
        } else {
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
