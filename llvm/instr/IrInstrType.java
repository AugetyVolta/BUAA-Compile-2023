package llvm.instr;

public enum IrInstrType {
    ADD,
    SUB,
    MUL,
    SDIV,
    SREM, //mod
    ICMP,
    AND,
    OR,
    CALL,
    ALLOCA,
    LOAD,
    STORE,
    GETELEMENTPTR,
    PHI,
    PCOPY,
    MOVE,
    ZEXT,
    TRUNC,
    BR,
    RET,
    GETINT,//getint
    PUTINT,//putint
    PUTCH,//putch
    PUTSTR,//putstr
    EQ,// ==
    NE,// !=
    SLT, // < 有符号比较
    SLE, // <=
    SGE, // >=
    SGT, // >
}
