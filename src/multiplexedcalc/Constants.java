package multiplexedcalc;

public class Constants {
    public static final String IP_ADDRESS = "127.0.0.1";
    public static final int PORT = 6969;
    //Operations
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "div";

    //help message
    public static final String HELP = "Welcome to MultiplexedCalc.\n" +
            "Command list:\n" +
            "'help' --> show this help message\n" +
            "'add [op1] [op2]' --> show result of [op1] + [op2]\n" +
            "'sub [op1] [op2]' --> show result of [op1] - [op2]\n" +
            "'mul [op1] [op2]' --> show result of [op1] * [op2]\n" +
            "'div [op1] [op2]' --> show result of [op1] / [op2]";
}
