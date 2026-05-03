package compiler.CodeGenerator;

import compiler.Parser.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator implements Opcodes {

  private ClassWriter classWriter;
  private String className;

  // On donne un numéro à chaque paramètre et variables
  private Map<String, Integer> variableSlots = new HashMap<>();
  private int nextSlot = 0;
  private Map<String, byte[]> generatedClasses = new HashMap<>(); // On stocke le nom de la classe avec son code bianire

  public Map<String, byte[]> generate(Node root, String className) {
    this.generatedClasses.clear();
    this.className = className;

    this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // Compute_frames calcule la taille max de la pile et des variables globales
    // V1_8 est la version de Java
    classWriter.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);

    generateConstructor();
    browse(root); // Lancement du parcours de l'arbre AST

    classWriter.visitEnd();
    generatedClasses.put(className, classWriter.toByteArray()); // On stocke le résultat dans le dictionneaire

    return generatedClasses;
  }

  // On met d'office un constructeur par défaut
  private void generateConstructor() {
    MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  private void browse(Node node) {
    if (node instanceof ProgramNode) {
      for (Node declaration : ((ProgramNode) node).getDeclarations()) {
        browse(declaration);
      }
    }
    else if (node instanceof FunctionNode) { // Vers la génération de méthodes
      generateFunction((FunctionNode) node);
    }
    else if (node instanceof DeclarationNode) {
      generateGlobalVariable((DeclarationNode) node);
    }
    else if (node instanceof CollectionDefNode) { // Gestion des cass de structures
      generateStructClass((CollectionDefNode) node);
    }
  }

  private void generateFunction(FunctionNode node){
    variableSlots.clear(); // On garantit que les variables locales ne gênent pas d'autres d'une autre méthode
    nextSlot = 0;

    String name = node.getName();

    StringBuilder descBuilder = new StringBuilder("(");
    if (name.equals("main")) {
      descBuilder.append("[Ljava/lang/String;");
      variableSlots.put("args", nextSlot++);
    } else if (node.getParameters() != null) {
      for (Node parameter : node.getParameters()) {
        ParameterNode param = (ParameterNode) parameter;
        descBuilder.append("I");
        variableSlots.put(param.getName(), nextSlot++);
      }
    }
    descBuilder.append(")");
    descBuilder.append(name.equals("main") ? "V" : "I");

    MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, name, descBuilder.toString(), null, null);
    methodVisitor.visitCode();

    generateStatement(node.getBody(), methodVisitor);

    if (name.equals("main")) {
      methodVisitor.visitInsn(RETURN);
    } else {
      methodVisitor.visitInsn(ICONST_0);
      methodVisitor.visitInsn(IRETURN);
    }

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();
  }

  private void generateStatement(Node node, MethodVisitor mv) {
    if (node == null) return;

    if (node instanceof LocalBlockNode) {
      for (Node sub : ((LocalBlockNode) node).getLocalNodes()) generateStatement(sub, mv);
    }
    else if (node instanceof AssignmentNode assignment) {
      String varName = ((IdNode) assignment.getIdentifier()).getName();

      // On traite l'expression à droite du "="
      generateExpression(assignment.getExpression(), mv);

      if (variableSlots.containsKey(varName)) {
        // Variable est déjà local, on l'écrase
        mv.visitVarInsn(ISTORE, variableSlots.get(varName));
      } else if (assignment.getType() instanceof EmptyNode) {
        // Si elle n'a pas de type, c'est qu'elle est globale.
        mv.visitFieldInsn(PUTSTATIC, this.className, varName, "I");
      } else {
        // Nouvelle déclaration locale
        variableSlots.put(varName, nextSlot++);
        mv.visitVarInsn(ISTORE, variableSlots.get(varName));
      }
    }

    else if (node instanceof IfNode ifNode) {
      Label elseLabel = new Label();
      Label endLabel = new Label();

      generateExpression(ifNode.getCondition(), mv);

      mv.visitJumpInsn(IFEQ, elseLabel);

      generateStatement(ifNode.getThenCaseBlock(), mv);

      mv.visitJumpInsn(GOTO, endLabel);

      mv.visitLabel(elseLabel);
      if (ifNode.getElseCaseBlock() != null) {
        generateStatement(ifNode.getElseCaseBlock(), mv);
      }

      mv.visitLabel(endLabel);
    }

    else if (node instanceof WhileNode whileNode) {
      Label startLabel = new Label();
      Label endLabel = new Label();

      mv.visitLabel(startLabel);

      generateExpression(whileNode.getCondition(), mv);

      mv.visitJumpInsn(IFEQ, endLabel);

      generateStatement(whileNode.getCodeInNode(), mv);

      mv.visitJumpInsn(GOTO, startLabel);

      mv.visitLabel(endLabel);
    }

    // Cas du println
    else if (node instanceof FunctionCallNode) {
      generateFunctionCall((FunctionCallNode) node, mv);
    }
    // Cas du Return
    else if (node instanceof ReturnNode) {
      generateExpression(((ReturnNode) node).getExpression(), mv);
      mv.visitInsn(IRETURN);
    }
  }

  private void generateFunctionCall(FunctionCallNode functionCallNode, MethodVisitor mv) {
    if (functionCallNode.getName().equals("println")) {
      mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
      generateExpression(functionCallNode.getParams().get(0), mv);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
    }
    else {
      StringBuilder descBuilder = new StringBuilder("(");
      for (Node arg : functionCallNode.getParams()) {
        generateExpression(arg, mv);
        descBuilder.append("I");
      }
      descBuilder.append(")I");

      mv.visitMethodInsn(INVOKESTATIC, this.className, functionCallNode.getName(), descBuilder.toString(), false);
    }
  }

  private void generateExpression(Node node, MethodVisitor mv) {
    if (node == null) return;

    System.out.println("Génération expression pour : " + node.getClass().getSimpleName());

    if (node instanceof IntNode intNode) {
      String value = intNode.getValue();
      mv.visitLdcInsn(value != null ? Integer.parseInt(value) : 0);
    }
    else if (node instanceof FloatNode floatNode) {
      mv.visitLdcInsn(Float.parseFloat(floatNode.getValue()));
    }
    else if (node instanceof StringNode stringNode) {
      mv.visitLdcInsn(stringNode.getContent());
    }
    else if (node instanceof BoolNode boolNode) {
      mv.visitInsn(boolNode.isValue() ? ICONST_1 : ICONST_0);
    }
    else if (node instanceof FunctionCallNode functionCallNode) {
      generateFunctionCall(functionCallNode, mv);
    }
    else if (node instanceof IdNode idNode) {
      String name = idNode.getName();
      if (variableSlots.containsKey(name)) {
        mv.visitVarInsn(ILOAD, variableSlots.get(name));
      } else {
        mv.visitFieldInsn(GETSTATIC, this.className, name, "I");
      }
    }
    else if (node instanceof BinaryNode binaryNode) {
      generateExpression(binaryNode.getLeft(), mv);
      generateExpression(binaryNode.getRight(), mv);

      String op = binaryNode.getOperator();
      switch (op) {
        case "+" -> mv.visitInsn(IADD);
        case "-" -> mv.visitInsn(ISUB);
        case "*" -> mv.visitInsn(IMUL);
        case "/" -> mv.visitInsn(IDIV);
        case "%" -> mv.visitInsn(IREM);
        case "==", "!=", "<", ">", "<=", ">=" -> generateComparison(op, mv);
        default -> throw new UnsupportedOperationException("Opérateur non géré : " + op);
      }
    }
    else {
      throw new UnsupportedOperationException("Le générateur ne reconnaît pas le type de nœud : "
          + node.getClass().getCanonicalName());
    }
  }

  private void generateComparison(String op, MethodVisitor mv) {
    Label trueLabel = new Label();
    Label endLabel = new Label();
    int jumpOpcode = switch (op) {
      case "==" -> IF_ICMPEQ;
      case "!=" -> IF_ICMPNE;
      case "<"  -> IF_ICMPLT;
      case ">"  -> IF_ICMPGT;
      case "<=" -> IF_ICMPLE;
      case ">=" -> IF_ICMPGE;
      default   -> throw new IllegalStateException();
    };
    mv.visitJumpInsn(jumpOpcode, trueLabel);
    mv.visitInsn(ICONST_0);
    mv.visitJumpInsn(GOTO, endLabel);
    mv.visitLabel(trueLabel);
    mv.visitInsn(ICONST_1);
    mv.visitLabel(endLabel);
  }


  private void generateGlobalVariable(DeclarationNode node) {
    String name = node.getName();
    String typeDescriptor = getDescriptor(node.getType());
    // Les variables sont public static puisqu'on est au niveau global du programme
    classWriter.visitField(ACC_PUBLIC + ACC_STATIC, name, typeDescriptor, null, null).visitEnd();
  }

  private String getDescriptor(String type) {
    if (type.equals("INT")) return "I";
    if (type.equals("FLOAT")) return "F";
    if (type.equals("BOOL")) return "Z";
    if (type.equals("STRING")) return "Ljava/lang/String;";
    return "L" + type + ";";
  }

  private void generateStructClass(CollectionDefNode node) {
    String structName = node.getName();
    ClassWriter structClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    structClassWriter.visit(V1_8, ACC_PUBLIC, structName, null, "java/lang/Object", null);

    for (Node prop : node.getProperties()) {
      if (prop instanceof DeclarationNode) {
        DeclarationNode field = (DeclarationNode) prop;
        String desc = getDescriptor(field.getType());
        structClassWriter.visitField(ACC_PUBLIC, field.getName(), desc, null, null).visitEnd();
      }
    }

    MethodVisitor methodVisitor = structClassWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    methodVisitor.visitCode();
    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    methodVisitor.visitInsn(RETURN);
    methodVisitor.visitMaxs(1, 1);
    methodVisitor.visitEnd();

    structClassWriter.visitEnd();
    generatedClasses.put(structName, structClassWriter.toByteArray());
  }
}