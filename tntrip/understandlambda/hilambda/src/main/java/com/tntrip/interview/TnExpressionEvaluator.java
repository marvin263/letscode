package com.tntrip.interview;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class TnExpressionEvaluator {
    private static class Token {
        int start;
        String str;
        TokenType type;
        OperatorEnum _operator;

        private enum TokenType {
            VARIABLE, OPERATOR, LEFT_PARENTHESIS, RIGHT_PARENTHESIS
        }

        private Token() {

        }

        OperatorEnum operator() {
            if (type != TokenType.OPERATOR) {
                throw new RuntimeException("Cannot get operator. token=" + this);
            }
            if (_operator == null) {
                _operator = OperatorEnum.fromString(str);
            }
            return _operator;
        }

        double value(Map<String, Double> variables) {
            if (type != TokenType.VARIABLE) {
                throw new RuntimeException("Cannot get value. token=" + this);
            }
            if (!variables.containsKey(str)) {
                throw new RuntimeException("No value provided. token=" + this);
            }
            return variables.get(str);
        }

        public static Token create(int start, TokenType type) {
            Token t = new Token();
            t.start = start;
            t.type = type;
            return t;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", str='" + str + '\'' +
                    ", start=" + start +
                    '}';
        }
    }

    private static List<Token> lexerToGetToken(String strExp) {
        List<Token> tokens = new ArrayList<>();
        int length = strExp.length();
        StringBuilder sb = new StringBuilder();
        char prevC = 0;
        Token t = null;
        for (int i = 0; i < length; i++) {
            char c = strExp.charAt(i);
            switch (c) {
                case '$':
                    if (t != null) {
                        addChar(sb, t, i, c);
                    } else {
                        // story begins
                    }
                    break;
                case '{':
                    if (t != null) {
                        switch (t.type) {
                            case VARIABLE:
                                throw new RuntimeException(" '{' should appear only once for VARIABLE type. index=" + i);
                            case OPERATOR:
                                if (prevC == '$') {
                                    sb.delete(sb.length() - 1, sb.length());
                                    endToken(tokens, sb, t);

                                    t = Token.create(i, Token.TokenType.VARIABLE);
                                    sb.append(prevC);
                                    sb.append(c);
                                } else {
                                    throw new RuntimeException(" '{' is not legal for TokenType=" + t.type + ". index=" + i);
                                }
                                break;
                            case LEFT_PARENTHESIS:
                            case RIGHT_PARENTHESIS:
                            default:
                                throw new RuntimeException("Should never be here. index=" + i);
                        }
                    } else {
                        if (prevC == '$') {
                            t = Token.create(i, Token.TokenType.VARIABLE);
                            sb.append(prevC);
                            sb.append(c);
                        } else {
                            throw new RuntimeException(" '{' must follow '$'. index=" + i);
                        }
                    }
                    break;
                case '}':
                    if (t == null || t.type != Token.TokenType.VARIABLE) {
                        throw new RuntimeException("'}' is legal only for variable. index=" + i);
                    } else {
                        if (prevC == '{') {
                            throw new RuntimeException("variable name is empty. it's illegal. index=" + i);
                        } else {
                            addChar(sb, t, i, c);
                            endToken(tokens, sb, t);
                            t = null;
                        }
                    }
                    break;
                case '(':
                case ')':
                    if (t != null) {
                        endToken(tokens, sb, t);
                        t = null;
                    }
                    t = Token.create(i, (c == '(' ? Token.TokenType.LEFT_PARENTHESIS : Token.TokenType.RIGHT_PARENTHESIS));
                    endToken(tokens, sb, t);
                    t = null;
                    break;
                case '.':
                    if (t == null) {
                        // operator begins
                    } else if (t.type == Token.TokenType.VARIABLE) {
                        addChar(sb, t, i, c);
                    } else {
                        throw new RuntimeException(" '.' is not allowed for any token type. curTokenType=" + t.type +
                                ". index=" + i);
                    }
                    break;
                default:
                    if (t != null) {
                        addChar(sb, t, i, c);
                    }
                    // operator
                    else {
                        t = Token.create(i, Token.TokenType.OPERATOR);
                        addChar(sb, t, i, c);
                    }
                    break;
            }
            prevC = c;
        }
        if (t != null) {
            throw new RuntimeException("t does not end. t=" + t);
        }
        return tokens;
    }

    private static void endToken(List<Token> tokens, StringBuilder sb, Token t) {
        t.str = sb.toString();
        tokens.add(t);
        sb.delete(0, sb.length());
    }

    private static void addChar(StringBuilder sb, Token t, int i, char c) {
        switch (t.type) {
            case VARIABLE:
                if (isVariableChar(c)) {
                    sb.append(c);
                } else {
                    throw new RuntimeException("'" + c + "' is not legal for VARIABLE character. index=" + i);
                }
                break;
            case OPERATOR:
                if (isOperatorChar(c)) {
                    sb.append(c);
                } else {
                    throw new RuntimeException("'" + c + "' is not legal for OPERATOR character. index=" + i);
                }
                break;
            case LEFT_PARENTHESIS:
            case RIGHT_PARENTHESIS:
                sb.append(c);
                break;
            default:
                throw new RuntimeException("Should never be here. index=" + i);
        }
    }

    // 字符0-9
    private static boolean isDigital(char c) {
        return c >= 48 && c <= 57;
    }


    // 大小写 字母
    private static boolean isLetter(char c) {
        //A--Z
        return (c >= 65 && c <= 90) ||
                //a-z
                (c >= 97 && c <= 122);
    }

    // 包括 ${}@_.
    private static boolean isVariableChar(char c) {
        return isLetter(c) ||
                isDigital(c) ||
                (c == '$' || c == '{' || c == '}' || c == '@' || c == '_' || c == '.');
    }

    // 操作符特殊字符: + - * / ^ ~   $_ 
    private static boolean isOperatorChar(char c) {
        return isLetter(c) ||
                isDigital(c) || (
                c == '$' || c == '_' ||
                        c == '+' || c == '-' ||
                        c == '*' || c == '/' ||
                        c == '^' || c == '~');
    }

    private static void var(StringBuilder sb, String str) {
        sb.append("${").append(str).append("}");
    }

    private static void plain(StringBuilder sb, String str) {
        sb.append(str);
    }

    private static class ExpAndVariable {
        String strExp;
        Map<String, Double> variables;

        public static ExpAndVariable create(String strExp, Map<String, Double> variables) {
            ExpAndVariable eav = new ExpAndVariable();
            eav.strExp = strExp;
            eav.variables = variables;
            return eav;
        }
    }

    private static ExpAndVariable buildExpression1() {
        String strExp = "${a}-${b}*${c}*${d}^${e}~${f}-${g}^(${h}+${i})";
        Map<String, Double> variables = new HashMap<>();
        variables.put("${a}", 1d);
        variables.put("${b}", 2d);
        variables.put("${c}", 3d);
        variables.put("${d}", 4d);
        variables.put("${e}", 5d);
        variables.put("${f}", 6d);
        variables.put("${g}", 7d);
        variables.put("${h}", 1d);
        variables.put("${i}", 2d);

        return ExpAndVariable.create(strExp, variables);
    }

    private static ExpAndVariable buildExpression2() {
        String strExp = "${a}.add(log1p(${b}.add(${c}.add(${d}).multiply(${e}))))";
        Map<String, Double> variables = new HashMap<>();
        variables.put("${a}", 1d);
        variables.put("${b}", 2d);
        variables.put("${c}", 3d);
        variables.put("${d}", 4d);
        variables.put("${e}", 5d);
        return ExpAndVariable.create(strExp, variables);
    }

    private static ExpAndVariable buildExpression3() {
        // 1+2*3^4-6==157
        String strExp = "${a}+${b}*${c}^${d}-${e}";
        Map<String, Double> variables = new HashMap<>();
        variables.put("${a}", 1d);
        variables.put("${b}", 2d);
        variables.put("${c}", 3d);
        variables.put("${d}", 4d);
        variables.put("${e}", 6d);
        return ExpAndVariable.create(strExp, variables);
    }

    private static ExpAndVariable buildExpression4() {
        // 1+2*3==9
        String strExp = "${a}.add(${b}).multiply(${c})";
        Map<String, Double> variables = new HashMap<>();
        variables.put("${a}", 1d);
        variables.put("${b}", 2d);
        variables.put("${c}", 3d);
        return ExpAndVariable.create(strExp, variables);
    }


    public static void main(String[] args) {
//        ExpAndVariable exp1 = buildExpression1();
        ExpAndVariable exp2 = buildExpression2();
//        ExpAndVariable exp3 = buildExpression3();
//        ExpAndVariable exp4 = buildExpression4();

//        System.out.println(letsEvaluate(exp1.strExp, exp1.variables));
        System.out.println(letsEvaluate(exp2.strExp, exp2.variables));
//        System.out.println(letsEvaluate(exp3.strExp, exp3.variables));
//        System.out.println(letsEvaluate(exp4.strExp, exp4.variables));

    }

    private static double letsEvaluate(String strExp, Map<String, Double> variables) {
        List<Token> tokens = lexerToGetToken(strExp);
        double rst = evaluate(tokens, variables);
        return new BigDecimal(rst).setScale(6, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue();
    }


    private static double evaluate(List<Token> tokens, Map<String, Double> variables) {
        Map<Integer, Integer> matchingParenthesis = matchingParenthesis(tokens);
        return doEvaluate(tokens, 0, tokens.size() - 1, variables, matchingParenthesis);
    }


    private static double doEvaluate(List<Token> tokens, int start, int end, Map<String, Double> variables, Map<Integer, Integer> matchingParenthesis) {
        Stack<Double> operandStack = new Stack<>();
        Stack<OperatorEnum> operatorStack = new Stack<>();

        int pos = start;
        while (pos <= end) {
            Token t = tokens.get(pos);
            switch (t.type) {
                case VARIABLE:
                    operandStack.push(t.value(variables));
                    pos++;
                    break;
                case OPERATOR:
                    OperatorEnum curOpr = t.operator();
                    computeIfNeeded(operandStack, operatorStack, curOpr);
                    operatorStack.push(curOpr);
                    pos++;
                    break;
                case LEFT_PARENTHESIS:
                    double subExpValue = doEvaluate(tokens, pos + 1, matchingParenthesis.get(pos) - 1, variables, matchingParenthesis);
                    pos = matchingParenthesis.get(pos) + 1;
                    operandStack.push(subExpValue);
                    break;
                case RIGHT_PARENTHESIS:
                    throw new RuntimeException("Should not appear right parenthesis");
                default:
                    break;
            }
        }

        while (!operatorStack.empty()) {
            computeWithTopOperator(operandStack, operatorStack);
        }

        // good
        if (operatorStack.size() == 0 && operandStack.size() == 1) {
            return operandStack.pop();
        } else {
            throw new RuntimeException(format("Expected left operator count=0, operand count=1, but actual operator count={0}, operand count={1}. tokens={2}",
                    operatorStack.size(), operandStack.size(), displayTokens(tokens, start, end)));
        }
    }

    private static String displayTokens(List<Token> tokens, int start, int end) {
        return concat(tokens.subList(start, end + 1).stream().map(e -> e.str).collect(Collectors.toList()), "", "");
    }

    private static void computeIfNeeded(Stack<Double> operandStack, Stack<OperatorEnum> operatorStack, OperatorEnum curOpr) {
        OperatorEnum topOpr = null;
        while (operatorStack.size() >= 1) {
            // 只是peek下，computeWithOperatorOperand会pop出来
            topOpr = operatorStack.peek();
            // 最外面的操作符 优先级更高
            if (curOpr.higherPriority(topOpr)) {
                break;
            }
            // topOpr >= curOpr
            else {
                computeWithTopOperator(operandStack, operatorStack);
            }
        }
    }

    private static void computeWithTopOperator(Stack<Double> operandStack, Stack<OperatorEnum> operatorStack) {
        if (operatorStack.size() <= 0) {
            throw new RuntimeException("operatorStack.size()==0, lack operator");
        }
        // 弹出一个操作符
        OperatorEnum opr = operatorStack.pop();
        int operandCount = opr.operandCount();
        if (operandStack.size() < operandCount) {
            throw new RuntimeException(format("Lack operand. operator={0}, expectOperandCount={1}, actualOperandCount={2}", opr, operandCount, operandStack.size()));
        } else {
            double[] args = new double[operandCount];
            for (int i = operandCount - 1; i >= 0; i--) {
                // 弹出操作数
                args[i] = operandStack.pop();
            }
            double rst = opr.compute(args);
            // 计算的结果，再压回操作数
            operandStack.push(rst);
        }
    }

    private static Map<Integer, Integer> matchingParenthesis(List<Token> tokens) {
        Map<Integer, Integer> matchingParenthesis = new HashMap<>();
        Stack<Integer> s = new Stack<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.type == Token.TokenType.LEFT_PARENTHESIS) {
                s.push(i);
            } else if (t.type == Token.TokenType.RIGHT_PARENTHESIS) {
                if (s.empty()) {
                    throw new RuntimeException("Parenthesis of formula not match, please check");
                }
                matchingParenthesis.put(s.pop(), i);
            } else {

            }
        }
        if (!s.empty()) {
            throw new RuntimeException("Parenthesis of formula not match, please check");
        }
        return matchingParenthesis;
    }

    /**
     * Formats a string using {@link MessageFormat#format(String, Object[])}.
     * <p>
     * This is a convenience method.
     * </p>
     *
     * @param pattern
     * @param argument2
     * @return the formated string
     */
    public static String format(final String pattern, final Object... argument2) {
        return MessageFormat.format(pattern, argument2);
    }

    public static void concat(StringBuilder sb, List<String> list,
                              String normal, String lastSpecial) {
        concat(sb, list.toArray(new String[0]), normal, lastSpecial);
    }

    public static String concat(List<String> list, String normal,
                                String lastSpecial) {
        StringBuilder sb = new StringBuilder("");
        concat(sb, list.toArray(new String[0]), normal, lastSpecial);
        return sb.toString();
    }

    public static void concat(StringBuilder sb, String[] array, String normal,
                              String lastSpecial) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            sb.append(array[i]);
            if (i == length - 1) {
                sb.append(lastSpecial);
            } else {
                sb.append(normal);
            }
        }
    }
}