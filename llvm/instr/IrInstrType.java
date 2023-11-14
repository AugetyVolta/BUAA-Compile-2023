package llvm.instr;

public enum IrInstrType {
    ADD,
    SUB,
    MUL,
    SDIV,
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
    EQ,// ==
    NE,// !=
    SLT, // < 有符号比较
    SLE, // <=
    SGE, // >=
    SGT, // >
}
