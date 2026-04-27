package compiler.CodeGenerator;

import compiler.Parser.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator implements Opcodes {

  private ClassWriter cw;
  private String className;

  // On donne un numéro a chaque paramètre et variables
  private Map<String, Integer> variableSlots = new HashMap<>();

  public byte[] generate(Node root, String className) {
    this.className = className;

    this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    cw.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);

    generateConstructor();

    cw.visitEnd();
    return cw.toByteArray();
  }

  private void generateConstructor() {}
}