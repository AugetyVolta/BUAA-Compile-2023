package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

import java.awt.datatransfer.MimeTypeParseException;
import java.util.ArrayList;

public class IrBinaryInstr extends IrInstr {

    public IrBinaryInstr(String name, IrValueType irValueType, IrInstrType irInstrType, IrValue operand1, IrValue operand2) {
        super(name, irValueType, irInstrType);
        modifyOperand(operand1, 0);
        modifyOperand(operand2, 1);
    }

    public IrValue getOperand1() {
        return getOperand(0);
    }

    public IrValue getOperand2() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s", getName(), getIrInstrType().toString().toLowerCase(), getType(), getOperand1().getName(), getOperand2().getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        if (getIrInstrType() == IrInstrType.MUL) {
            if (multOptimize()) {
                return;
            }
        }
        if (getIrInstrType() == IrInstrType.SDIV) {
            if (divOptimize()) {
                return;
            }
        }
        //两侧的东西一定都在内存中已经存了,因此直接获得的即可
        //operand1
        if (getOperand1() instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand1()).getValue());
        } else {
            int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
        }
        //operand2
        if (getOperand2() instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(9, ((IrConstInt) getOperand2()).getValue());
        } else {
            int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
            MipsBuilder.MIPSBUILDER.buildLw(9, 29, offset2);
        }
        //开始构建指令
        switch (getIrInstrType()) {
            case ADD:
                MipsBuilder.MIPSBUILDER.buildAddu(10, 8, 9);
                break;
            case SUB:
                MipsBuilder.MIPSBUILDER.buildSubu(10, 8, 9);
                break;
            case MUL:
                MipsBuilder.MIPSBUILDER.buildMult(8, 9);
                MipsBuilder.MIPSBUILDER.buildMflo(10);
                break;
            case SDIV:
                MipsBuilder.MIPSBUILDER.buildDiv(8, 9);
                MipsBuilder.MIPSBUILDER.buildMflo(10);
                break;
            case SREM:
                MipsBuilder.MIPSBUILDER.buildDiv(8, 9);
                MipsBuilder.MIPSBUILDER.buildMfhi(10);
                break;
        }
        //将指令的值存入内存
        int resultOffset = MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
        MipsBuilder.MIPSBUILDER.buildSw(10, 29, resultOffset);
    }

    public boolean is2Power(IrConstInt constInt) {
        int imm = Math.abs(constInt.getValue());
        return (imm & (imm - 1)) == 0;
    }

    public boolean multOptimize() {
        IrValue left = getOperand1();
        IrValue right = getOperand2();
        if (left instanceof IrConstInt && ((IrConstInt) left).getValue() == 0 ||
                right instanceof IrConstInt && ((IrConstInt) right).getValue() == 0) {
            MipsBuilder.MIPSBUILDER.buildLi(8, 0);
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        } else if (left instanceof IrConstInt && is2Power((IrConstInt) left)) {
            int offset = (int) (Math.log(((IrConstInt) left).getValue()) / Math.log(2));
            if (getOperand2() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand2()).getValue());
            } else {
                int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
                MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset2);
            }
            MipsBuilder.MIPSBUILDER.buildSll(8, 8, offset);
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        } else if (right instanceof IrConstInt && is2Power((IrConstInt) right)) {
            int offset = (int) (Math.log(((IrConstInt) right).getValue()) / Math.log(2));
            if (getOperand1() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand1()).getValue());
            } else {
                int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
                MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
            }
            MipsBuilder.MIPSBUILDER.buildSll(8, 8, offset);
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        } else if (left instanceof IrConstInt && ((IrConstInt) left).getValue() < 5) {
            if (getOperand2() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand2()).getValue());
                MipsBuilder.MIPSBUILDER.buildLi(9, ((IrConstInt) getOperand2()).getValue());
            } else {
                int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
                MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset2);
                MipsBuilder.MIPSBUILDER.buildMove(9, 8);
            }
            for (int i = 0; i < ((IrConstInt) left).getValue() - 1; i++) {
                MipsBuilder.MIPSBUILDER.buildAddu(8, 8, 9);
            }
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        } else if (right instanceof IrConstInt && ((IrConstInt) right).getValue() < 5) {
            if (getOperand1() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand1()).getValue());
                MipsBuilder.MIPSBUILDER.buildLi(9, ((IrConstInt) getOperand1()).getValue());
            } else {
                int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
                MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
                MipsBuilder.MIPSBUILDER.buildMove(9, 8);
            }
            for (int i = 0; i < ((IrConstInt) right).getValue() - 1; i++) {
                MipsBuilder.MIPSBUILDER.buildAddu(8, 8, 9);
            }
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        }
        return false;
    }


    public boolean divOptimize() {
        IrValue left = getOperand1();
        IrValue right = getOperand2();
        if (left instanceof IrConstInt && ((IrConstInt) left).getValue() == 0) {
            MipsBuilder.MIPSBUILDER.buildLi(8, 0);
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        } else if (right instanceof IrConstInt) {
            int d = ((IrConstInt) right).getValue();
            long m;
            long sh;
            long l;
            ArrayList<Long> returns = chooseMultiplier(Math.abs(d), 31);
            m = returns.get(0);
            sh = returns.get(1);
            l = returns.get(2);
            if (Math.abs(d) == 1) {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) left).getValue());
                    MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
                } else {
                    int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                    MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
                    MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
                }
                return true;
            } else if (Math.abs(d) == (int) (Math.pow(2, l))) {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(9, ((IrConstInt) left).getValue());
                } else {
                    int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                    MipsBuilder.MIPSBUILDER.buildLw(9, 29, offset1);
                }
                MipsBuilder.MIPSBUILDER.buildSra(8, 9, (int) (l - 1));
                MipsBuilder.MIPSBUILDER.buildSrl(8, 8, (int) (32 - l));
                MipsBuilder.MIPSBUILDER.buildAdd(8, 8, 9);
                MipsBuilder.MIPSBUILDER.buildSra(8, 8, (int) l);
            } else if (m < (1L << 31)) {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) left).getValue());
                } else {
                    int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                    MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
                }
                MipsBuilder.MIPSBUILDER.buildLi(9, (int) ((m << 32) >> 32));
                MipsBuilder.MIPSBUILDER.buildMult(8, 9);
                MipsBuilder.MIPSBUILDER.buildMfhi(9);
                MipsBuilder.MIPSBUILDER.buildSra(9, 9, (int) sh);
                MipsBuilder.MIPSBUILDER.buildSrl(8, 8, 31);
                MipsBuilder.MIPSBUILDER.buildAddu(8, 9, 8);
            } else {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) left).getValue());
                } else {
                    int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                    MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
                }
                MipsBuilder.MIPSBUILDER.buildLi(9, (int) (m - (1L << 32)));
                MipsBuilder.MIPSBUILDER.buildMult(8, 9);
                MipsBuilder.MIPSBUILDER.buildMfhi(9);
                MipsBuilder.MIPSBUILDER.buildAdd(9, 8, 9);
                MipsBuilder.MIPSBUILDER.buildSra(9, 9, (int) sh);
                MipsBuilder.MIPSBUILDER.buildSrl(8, 8, 31);
                MipsBuilder.MIPSBUILDER.buildAddu(8, 9, 8);
            }
            if (d < 0) {
                MipsBuilder.MIPSBUILDER.buildSubu(8, 0, 8);
            }
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
            return true;
        }
        return false;
    }

    public ArrayList<Long> chooseMultiplier(int d, int prec) {
        long l = (long) Math.ceil(Math.log(d) / Math.log(2));
        long sh = l;
        long m_low = (long) (Math.floor(Math.pow(2, 32 + l) / d));
        long m_high = (long) Math.floor((Math.pow(2, 32 + l) + Math.pow(2, 32 + l - prec)) / d);
        while (m_low / 2 < m_high / 2 && sh > 0) {
            m_low = m_low / 2;
            m_high = m_high / 2;
            sh = sh - 1;
        }
        ArrayList<Long> returns = new ArrayList<>();
        returns.add(m_high);
        returns.add(sh);
        returns.add(l);
        return returns;
    }
}
