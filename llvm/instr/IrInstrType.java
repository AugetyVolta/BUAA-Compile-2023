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
    ZEXT,
    TRUNC,
    BR,
    RET,
    GETINT,//getint
    PUTINT,//putint
    PUTCH,//putch
    EQ,// ==
    NE,// !=
    SLT, // < 有符号比较
    SLE, // <=
    SGE, // >=
    SGT, // >
}
