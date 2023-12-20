package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

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
        //两侧的东西一定都在内存中已经存了,因此直接获得的即可
        int reg3;
        if (MipsBuilder.MIPSBUILDER.hasAllocReg(this)) {
            reg3 = MipsBuilder.MIPSBUILDER.getReg(this);
        } else {
            reg3 = MipsBuilder.MIPSBUILDER.allocReg(this);
        }
        if (getIrInstrType() == IrInstrType.MUL) {
            if (multOptimize(reg3)) {
                return;
            }
        }
        if (getIrInstrType() == IrInstrType.SDIV) {
            if (divOptimize(reg3)) {
                return;
            }
        }
        //operand1
        int reg1;
        if (getOperand1() instanceof IrConstInt) {
            reg1 = 26;
            MipsBuilder.MIPSBUILDER.buildLi(reg1, ((IrConstInt) getOperand1()).getValue());
        } else {
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand1())) {
                reg1 = MipsBuilder.MIPSBUILDER.getReg(getOperand1());
            } else {
                reg1 = MipsBuilder.MIPSBUILDER.allocReg(getOperand1());
                int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
                MipsBuilder.MIPSBUILDER.buildLw(reg1, 29, offset1);
            }
        }
        //operand2
        int reg2;
        if (getOperand2() instanceof IrConstInt) {
            reg2 = 27;
            MipsBuilder.MIPSBUILDER.buildLi(reg2, ((IrConstInt) getOperand2()).getValue());
        } else {
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand2())) {
                reg2 = MipsBuilder.MIPSBUILDER.getReg(getOperand2());
            } else {
                reg2 = MipsBuilder.MIPSBUILDER.allocReg(getOperand2());
                int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
                MipsBuilder.MIPSBUILDER.buildLw(reg2, 29, offset2);
            }
        }
        //开始构建指令
        switch (getIrInstrType()) {
            case ADD:
                MipsBuilder.MIPSBUILDER.buildAddu(reg3, reg1, reg2);
                break;
            case SUB:
                MipsBuilder.MIPSBUILDER.buildSubu(reg3, reg1, reg2);
                break;
            case MUL:
                MipsBuilder.MIPSBUILDER.buildMult(reg1, reg2);
                MipsBuilder.MIPSBUILDER.buildMflo(reg3);
                break;
            case SDIV:
                MipsBuilder.MIPSBUILDER.buildDiv(reg1, reg2);
                MipsBuilder.MIPSBUILDER.buildMflo(reg3);
                break;
            case SREM:
                MipsBuilder.MIPSBUILDER.buildDiv(reg1, reg2);
                MipsBuilder.MIPSBUILDER.buildMfhi(reg3);
                break;
        }
        //在内存中构建位置
        MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
    }

    public String hash() {
        if (getIrInstrType() == IrInstrType.ADD || getIrInstrType() == IrInstrType.MUL) {
            StringBuilder sb = new StringBuilder();
            sb.append(getIrInstrType().toString().toLowerCase());
            sb.append(getType());
            String name1 = getOperand1().getName();
            String name2 = getOperand2().getName();
            if (name1.compareTo(name2) < 0) {
                sb.append(name1);
                sb.append(name2);
            } else {
                sb.append(name2);
                sb.append(name1);
            }
            return sb.toString();
        } else {
            return this.toString().substring(0, this.toString().indexOf('='));
        }
    }


    public boolean is2Power(IrConstInt constInt) {
        int imm = Math.abs(constInt.getValue());
        return (imm & (imm - 1)) == 0;
    }

    public boolean multOptimize(int reg) {
        IrValue left = getOperand1();
        IrValue right = getOperand2();
        if (left instanceof IrConstInt && ((IrConstInt) left).getValue() == 0 ||
                right instanceof IrConstInt && ((IrConstInt) right).getValue() == 0) {
            MipsBuilder.MIPSBUILDER.buildLi(reg, 0);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            return true;
        }
        else if (left instanceof IrConstInt && is2Power((IrConstInt) left)) {
            int offset = (int) (Math.log(((IrConstInt) left).getValue()) / Math.log(2));
            if (getOperand2() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) getOperand2()).getValue());
            } else {
                if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand2())) {
                    MipsBuilder.MIPSBUILDER.buildMove(26, MipsBuilder.MIPSBUILDER.getReg(getOperand2()));
                } else {
                    int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
                    MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset2);
                }
            }
            MipsBuilder.MIPSBUILDER.buildSll(reg, 26, offset);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            return true;
        } else if (right instanceof IrConstInt && is2Power((IrConstInt) right)) {
            int offset = (int) (Math.log(((IrConstInt) right).getValue()) / Math.log(2));
            if (getOperand1() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) getOperand1()).getValue());
            } else {
                if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand1())) {
                    MipsBuilder.MIPSBUILDER.buildMove(26, MipsBuilder.MIPSBUILDER.getReg(getOperand1()));
                } else {
                    int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
                    MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset1);
                }
            }
            MipsBuilder.MIPSBUILDER.buildSll(reg, 26, offset);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            return true;
        } else if (left instanceof IrConstInt && ((IrConstInt) left).getValue() < 5) {
            if (getOperand2() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) getOperand2()).getValue());
                MipsBuilder.MIPSBUILDER.buildLi(27, ((IrConstInt) getOperand2()).getValue());
            } else {
                if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand2())) {
                    MipsBuilder.MIPSBUILDER.buildMove(26, MipsBuilder.MIPSBUILDER.getReg(getOperand2()));
                    MipsBuilder.MIPSBUILDER.buildMove(27, 26);
                } else {
                    int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
                    MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset2);
                    MipsBuilder.MIPSBUILDER.buildMove(27, 26);
                }
            }
            for (int i = 0; i < ((IrConstInt) left).getValue() - 2; i++) {
                MipsBuilder.MIPSBUILDER.buildAddu(26, 26, 27);
            }
            MipsBuilder.MIPSBUILDER.buildAddu(reg, 26, 27);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            return true;
        } else if (right instanceof IrConstInt && ((IrConstInt) right).getValue() < 5) {
            if (getOperand1() instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) getOperand1()).getValue());
                MipsBuilder.MIPSBUILDER.buildLi(27, ((IrConstInt) getOperand1()).getValue());
            } else {
                if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand1())) {
                    MipsBuilder.MIPSBUILDER.buildMove(26, MipsBuilder.MIPSBUILDER.getReg(getOperand1()));
                    MipsBuilder.MIPSBUILDER.buildMove(27, 26);
                } else {
                    int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
                    MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset1);
                    MipsBuilder.MIPSBUILDER.buildMove(27, 26);
                }
            }
            for (int i = 0; i < ((IrConstInt) right).getValue() - 2; i++) {
                MipsBuilder.MIPSBUILDER.buildAddu(26, 26, 27);
            }
            MipsBuilder.MIPSBUILDER.buildAddu(reg, 26, 27);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            return true;
        }
        return false;
    }


    public boolean divOptimize(int reg) {
        IrValue left = getOperand1();
        IrValue right = getOperand2();
        if (left instanceof IrConstInt && ((IrConstInt) left).getValue() == 0) {
            MipsBuilder.MIPSBUILDER.buildLi(reg, 0);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            return true;
        } else if (right instanceof IrConstInt && ((IrConstInt) right).getValue() == 1) {
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(left)) {
                MipsBuilder.MIPSBUILDER.buildMove(reg, MipsBuilder.MIPSBUILDER.getReg(left));
            } else {
                int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                MipsBuilder.MIPSBUILDER.buildLw(reg, 29, offset1);
            }
            return true;
        } else if (left.equals(right)) {
            MipsBuilder.MIPSBUILDER.buildLi(reg, 1);
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
                    MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
                    MipsBuilder.MIPSBUILDER.buildLi(reg, ((IrConstInt) left).getValue());
                } else {
                    MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
                    if (MipsBuilder.MIPSBUILDER.hasAllocReg(left)) {
                        MipsBuilder.MIPSBUILDER.buildMove(reg, MipsBuilder.MIPSBUILDER.getReg(left));
                    } else {
                        int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                        MipsBuilder.MIPSBUILDER.buildLw(reg, 29, offset1);
                    }
                }
                return true;
            } else if (Math.abs(d) == (int) (Math.pow(2, l))) {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(27, ((IrConstInt) left).getValue());
                } else {
                    if (MipsBuilder.MIPSBUILDER.hasAllocReg(left)) {
                        MipsBuilder.MIPSBUILDER.buildMove(27, MipsBuilder.MIPSBUILDER.getReg(left));
                    } else {
                        int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                        MipsBuilder.MIPSBUILDER.buildLw(27, 29, offset1);
                    }
                }
                MipsBuilder.MIPSBUILDER.buildSra(26, 27, (int) (l - 1));
                MipsBuilder.MIPSBUILDER.buildSrl(26, 26, (int) (32 - l));
                MipsBuilder.MIPSBUILDER.buildAdd(26, 26, 27);
                MipsBuilder.MIPSBUILDER.buildSra(26, 26, (int) l);
            } else if (m < (1L << 31)) {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) left).getValue());
                } else {
                    if (MipsBuilder.MIPSBUILDER.hasAllocReg(left)) {
                        MipsBuilder.MIPSBUILDER.buildMove(26, MipsBuilder.MIPSBUILDER.getReg(left));
                    } else {
                        int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                        MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset1);
                    }
                }
                MipsBuilder.MIPSBUILDER.buildLi(27, (int) ((m << 32) >> 32));
                MipsBuilder.MIPSBUILDER.buildMult(26, 27);
                MipsBuilder.MIPSBUILDER.buildMfhi(27);
                MipsBuilder.MIPSBUILDER.buildSra(27, 27, (int) sh);
                MipsBuilder.MIPSBUILDER.buildSrl(26, 26, 31);
                MipsBuilder.MIPSBUILDER.buildAddu(26, 27, 26);
            } else {
                if (left instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) left).getValue());
                } else {
                    if (MipsBuilder.MIPSBUILDER.hasAllocReg(left)) {
                        MipsBuilder.MIPSBUILDER.buildMove(26, MipsBuilder.MIPSBUILDER.getReg(left));
                    } else {
                        int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(left);
                        MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset1);
                    }
                }
                MipsBuilder.MIPSBUILDER.buildLi(27, (int) (m - (1L << 32)));
                MipsBuilder.MIPSBUILDER.buildMult(26, 27);
                MipsBuilder.MIPSBUILDER.buildMfhi(27);
                MipsBuilder.MIPSBUILDER.buildAdd(27, 26, 27);
                MipsBuilder.MIPSBUILDER.buildSra(27, 27, (int) sh);
                MipsBuilder.MIPSBUILDER.buildSrl(26, 26, 31);
                MipsBuilder.MIPSBUILDER.buildAddu(26, 27, 26);
            }
            if (d < 0) {
                MipsBuilder.MIPSBUILDER.buildSubu(26, 0, 26);
            }
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            MipsBuilder.MIPSBUILDER.buildMove(reg, 26);
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
