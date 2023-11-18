package mips;

import llvm.IrConstInt;
import llvm.instr.IrInstr;
import mips.instr.i.*;
import mips.instr.j.MipsJ;
import mips.instr.j.MipsJal;
import mips.instr.other.MipsComment;
import mips.instr.other.MipsSyscall;
import mips.instr.r.*;

import java.util.ArrayList;

public class MipsBuilder {
    public static MipsBuilder MIPSBUILDER = new MipsBuilder();
    private final MipsModule mipsModule;//全局只有一个mipsModule

    public MipsBuilder() {
        this.mipsModule = new MipsModule();
    }

    public MipsModule getMipsModule() {
        return mipsModule;
    }

    //构建整数data
    public void buildMipsGlobalData(String name, int dim, int length, ArrayList<IrConstInt> initValues) {
        MipsGlobalData globalData = new MipsGlobalData(name, dim, length, initValues);
        mipsModule.addGlobalData(globalData);
    }

    //构建字符串data
    public void buildMipsGlobalString(String name, String constString) {
        MipsGlobalData globalData = new MipsGlobalData(name, constString);
        mipsModule.addGlobalData(globalData);
    }

    //构建label
    public void buildMipsLabel(String name) {
        MipsLabel mipsLabel = new MipsLabel(name);
        mipsModule.addTextData(mipsLabel);
    }

    //构建otherInstr
    //构建comment
    public void buildComment(IrInstr irInstr) {
        MipsComment comment = new MipsComment(irInstr);
        mipsModule.addTextData(comment);
    }

    //构建syscall
    public void buildSyscall() {
        mipsModule.addTextData(new MipsSyscall());
    }

    //构建RInstr
    //构建add
    public void buildAdd(int rd, int rs, int rt) {
        MipsAdd add = new MipsAdd(rd, rs, rt);
        mipsModule.addTextData(add);
    }

    //构建sub
    public void buildSub(int rd, int rs, int rt) {
        MipsSub sub = new MipsSub(rd, rs, rt);
        mipsModule.addTextData(sub);
    }

    //构建seq
    public void buildSeq(int rd, int rs, int rt) {
        MipsSeq seq = new MipsSeq(rd, rs, rt);
        mipsModule.addTextData(seq);
    }

    //构建sge
    public void buildSge(int rd, int rs, int rt) {
        MipsSge sge = new MipsSge(rd, rs, rt);
        mipsModule.addTextData(sge);
    }

    //构建sgt
    public void buildSgt(int rd, int rs, int rt) {
        MipsSgt sgt = new MipsSgt(rd, rs, rt);
        mipsModule.addTextData(sgt);
    }

    //构建sle
    public void buildSle(int rd, int rs, int rt) {
        MipsSle sle = new MipsSle(rd, rs, rt);
        mipsModule.addTextData(sle);
    }

    //构建slt
    public void buildSlt(int rd, int rs, int rt) {
        MipsSlt slt = new MipsSlt(rd, rs, rt);
        mipsModule.addTextData(slt);
    }

    //构建sne
    public void buildSne(int rd, int rs, int rt) {
        MipsSne sne = new MipsSne(rd, rs, rt);
        mipsModule.addTextData(sne);
    }

    //构建move
    public void buildMove(int dst, int src) {
        MipsMove move = new MipsMove(dst, src);
        mipsModule.addTextData(move);
    }

    //构建mult
    public void buildMult(int rs, int rt) {
        MipsMult mult = new MipsMult(rs, rt);
        mipsModule.addTextData(mult);
    }

    //构建div
    public void buildDiv(int rs, int rt) {
        MipsDiv div = new MipsDiv(rs, rt);
        mipsModule.addTextData(div);
    }

    //构建mfhi
    public void buildMfhi(int rd) {
        MipsMfhi mfhi = new MipsMfhi(rd);
        mipsModule.addTextData(mfhi);
    }

    //构建mflo
    public void buildMflo(int rd) {
        MipsMflo mflo = new MipsMflo(rd);
        mipsModule.addTextData(mflo);
    }

    //构建jr
    public void buildJr(int rs) {
        MipsJr jr = new MipsJr(rs);
        mipsModule.addTextData(jr);
    }

    //构建IInstr
    //构建addi
    public void buildAddi(int rt, int rs, int imm) {
        MipsAddi addi = new MipsAddi(rt, rs, imm);
        mipsModule.addTextData(addi);
    }

    //构建subi
    public void buildSubi(int rt, int rs, int imm) {
        MipsSubi subi = new MipsSubi(rt, rs, imm);
        mipsModule.addTextData(subi);
    }

    //构建beq
    public void buildBeq(int rs, int rt, String label) {
        MipsBeq beq = new MipsBeq(rs, rt, label);
        mipsModule.addTextData(beq);
    }

    //构建bne
    public void buildBne(int rs, int rt, String label) {
        MipsBne bne = new MipsBne(rs, rt, label);
        mipsModule.addTextData(bne);
    }

    //构建la
    public void buildLa(String label) {
        MipsLa la = new MipsLa(label);
        mipsModule.addTextData(la);
    }

    //构建li
    public void buildLa(int rt, int imm) {
        MipsLi li = new MipsLi(rt, imm);
        mipsModule.addTextData(li);
    }

    //构建lw
    public void buildLw(int rt, int base, int offset) {
        MipsLw lw = new MipsLw(rt, base, offset);
        mipsModule.addTextData(lw);
    }

    //构建sw
    public void buildSw(int rt, int base, int offset) {
        MipsSw sw = new MipsSw(rt, base, offset);
        mipsModule.addTextData(sw);
    }

    //构建JInstr
    //构建j
    public void buildJ(String labelName) {
        MipsJ mipsJ = new MipsJ(labelName);
        mipsModule.addTextData(mipsJ);
    }

    //构建jal
    public void buildJal(String labelName) {
        MipsJal mipsJal = new MipsJal(labelName);
        mipsModule.addTextData(mipsJal);
    }
}
