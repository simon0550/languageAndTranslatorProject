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
  private Map<String, String> globalVariablesTypes = new HashMap<>();
  private Map<String, String> localVariableTypes = new HashMap<>();
  private String currentReturnTypeDesc = "I";
  private Map<String, String> functionReturnTypes = new HashMap<>();
  private Map<String, String> structConstructors = new HashMap<>();

  public Map<String, byte[]> generate(Node root, String className) {
    this.generatedClasses.clear();
    this.functionReturnTypes.clear();
    this.className = className;

    preBrowse(root);

    this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // Compute_frames calcule la taille max de la pile et des variables globales
    // V1_8 est la version de Java
    classWriter.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);

    MethodVisitor clinitVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    clinitVisitor.visitCode();

    generateConstructor();
    browse(root, clinitVisitor); // Lancement du parcours de l'arbre AST

    clinitVisitor.visitInsn(RETURN);
    clinitVisitor.visitMaxs(0, 0);
    clinitVisitor.visitEnd();

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

  private void preBrowse(Node node) {
    if (node instanceof ProgramNode program) {
      for (Node decl : program.getDeclarations()) {
        if (decl instanceof FunctionNode fn) {
          String retDesc = getDescriptor(fn.getRetType());
          functionReturnTypes.put(fn.getName(), retDesc);
        }
        else if (decl instanceof CollectionDefNode structDef) {
          StringBuilder constructorDesc = new StringBuilder("(");
          for (Node prop : structDef.getProperties()) {
            if (prop instanceof DeclarationNode) {
              constructorDesc.append(getDescriptor(((DeclarationNode) prop).getType()));
            }
          }
          constructorDesc.append(")V");
          structConstructors.put(structDef.getName(), constructorDesc.toString());
        }
      }
    }
  }

  private void browse(Node node, MethodVisitor clinit) {
    if (node instanceof ProgramNode program) {
      for (Node declaration : program.getDeclarations()) {
        browse(declaration, clinit);
      }
    }
    else if (node instanceof AssignmentNode assignment) {
      String varName = ((IdNode) assignment.getIdentifier()).getName();

      String typeName = "int";
      if (assignment.getType() instanceof TypeNode typeNode) {
        typeName = typeNode.getType();
      } else if (assignment.getType() instanceof IdNode idNode) {
        typeName = idNode.getName();
      }
      String typeDesc = getDescriptor(typeName);

      classWriter.visitField(ACC_PUBLIC + ACC_STATIC, varName, typeDesc, null, null).visitEnd();
      globalVariablesTypes.put(varName, typeDesc);

      generateExpression(assignment.getExpression(), clinit);
      clinit.visitFieldInsn(PUTSTATIC, this.className, varName, typeDesc);
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

  private void generateFunction(FunctionNode node) {
    variableSlots.clear();
    localVariableTypes.clear();
    nextSlot = 0;
    String name = node.getName();

    StringBuilder descBuilder = new StringBuilder("(");

    if (name.equals("main")) {
      descBuilder.append("[Ljava/lang/String;");
      variableSlots.put("args", nextSlot++);
    } else if (node.getParameters() != null) {
      for (Node parameter : node.getParameters()) {
        ParameterNode param = (ParameterNode) parameter;
        String typeDesc = getDescriptor(param.getType());

        descBuilder.append(typeDesc);
        variableSlots.put(param.getName(), nextSlot);
        localVariableTypes.put(param.getName(), typeDesc);
        nextSlot++;
      }
    }
    descBuilder.append(")");

    String returnTypeDesc = name.equals("main") ? "V" : getDescriptor(node.getRetType());
    this.currentReturnTypeDesc = returnTypeDesc;

    String fullDesc = descBuilder.toString() + returnTypeDesc;
    MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, name, fullDesc, null, null);    methodVisitor.visitCode();

    generateStatement(node.getBody(), methodVisitor);

    if (name.equals("main") || returnTypeDesc.equals("V")) {
      methodVisitor.visitInsn(RETURN);
    } else {
      if (returnTypeDesc.equals("F")) {
        methodVisitor.visitInsn(FCONST_0);
        methodVisitor.visitInsn(FRETURN);
      } else if (returnTypeDesc.startsWith("L") || returnTypeDesc.startsWith("[")) {
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitInsn(ARETURN);
      } else {
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitInsn(IRETURN);
      }
    }

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();
  }

  private void generateStatement(Node node, MethodVisitor mv) {
    if (node == null) return;
    // On fait un simple appel récursif lorqu'on a qu'un simple bloc
    if (node instanceof LocalBlockNode) {
      for (Node sub : ((LocalBlockNode) node).getLocalNodes()) generateStatement(sub, mv);
    }

    else if (node instanceof AssignmentNode assignment) {
      String varName = ((IdNode) assignment.getIdentifier()).getName();
      generateExpression(assignment.getExpression(), mv); // On calcule la valeur à droite du "="

      if (variableSlots.containsKey(varName)) { // Variable locale
        String typeDescriptor = localVariableTypes.get(varName);
        storeVariable(mv, typeDescriptor, variableSlots.get(varName));
      }
      else if (globalVariablesTypes.containsKey(varName)) { // Variable globale (statique)
        String typeDescriptor = globalVariablesTypes.get(varName);
        mv.visitFieldInsn(PUTSTATIC, this.className, varName, typeDescriptor);
      }
      else {
        // Cas d'une déclaration implicite
        int slot = nextSlot++;
        String typeDescriptor = "I";
        variableSlots.put(varName, slot);
        localVariableTypes.put(varName, typeDescriptor);
        storeVariable(mv, typeDescriptor, slot);
      }
    }

    else if (node instanceof DeclarationNode declaration) {
      String varName = declaration.getName();
      String descriptor = getDescriptor(declaration.getType());

      if (!variableSlots.containsKey(varName)) {
        int slot = nextSlot++;
        variableSlots.put(varName, slot);
        localVariableTypes.put(varName, descriptor);

        if (descriptor.equals("F")) {
          mv.visitInsn(FCONST_0);
          mv.visitVarInsn(FSTORE, slot);
        } else if (descriptor.equals("Ljava/lang/String;") || descriptor.startsWith("L") || descriptor.startsWith("[")) {
          mv.visitInsn(ACONST_NULL);
          mv.visitVarInsn(ASTORE, slot);
        } else {
          mv.visitInsn(ICONST_0);
          mv.visitVarInsn(ISTORE, slot);
        }
      }
    }

    else if (node instanceof IfNode ifNode) {
      Label elseLabel = new Label();
      Label endLabel = new Label();

      generateExpression(ifNode.getCondition(), mv);
      mv.visitJumpInsn(IFEQ, elseLabel); // Si le résultat est faux, saut à l'étiquette else direct
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

    else if (node instanceof FunctionCallNode) {
      generateFunctionCall((FunctionCallNode) node, mv);
    }
    else if (node instanceof ReturnNode) {
      generateExpression(((ReturnNode) node).getExpression(), mv);

      if (currentReturnTypeDesc.equals("F")) mv.visitInsn(FRETURN);
      else if (currentReturnTypeDesc.startsWith("L") || currentReturnTypeDesc.startsWith("[")) mv.visitInsn(ARETURN);
      else if (currentReturnTypeDesc.equals("V")) mv.visitInsn(RETURN);
      else mv.visitInsn(IRETURN);
    } else if (node instanceof ForNode forNode) {
      Label startLabel = new Label();
      Label endLabel = new Label();

      String varName = ((IdNode) forNode.getInit()).getName();
      int varSlot = variableSlots.get(varName);
      generateExpression(forNode.getStart(), mv);
      mv.visitVarInsn(ISTORE, varSlot);
      mv.visitLabel(startLabel);
      mv.visitVarInsn(ILOAD, varSlot);
      generateExpression(forNode.getEnd(), mv);
      mv.visitJumpInsn(IF_ICMPGT, endLabel);
      generateStatement(forNode.getBody(), mv);
      generateExpression(forNode.getStep(), mv);
      mv.visitVarInsn(ISTORE, varSlot);
      mv.visitJumpInsn(GOTO, startLabel);
      mv.visitLabel(endLabel);
    }
    else if (node instanceof StringNode stringNode) {
      String text = stringNode.getContent();
      if (text.startsWith("\"") && text.endsWith("\"")) {
        text = text.substring(1, text.length() - 1);
      }
      mv.visitLdcInsn(text);

    }
  }

  // On génère l'instrcution en fonction du type de descripteur
  private void storeVariable(MethodVisitor mv, String typeDescriptor, int slot) {
    if (typeDescriptor.equals("F")) mv.visitVarInsn(FSTORE, slot);
    else if (typeDescriptor.startsWith("L") || typeDescriptor.startsWith("[")) mv.visitVarInsn(ASTORE, slot);
    else mv.visitVarInsn(ISTORE, slot);
  }

  private void generateFunctionCall(FunctionCallNode functionCallNode, MethodVisitor mv) {
    String name = functionCallNode.getName();

    if (name.equals("println") || name.equals("print") || name.equals("write") || name.equals("print_INT") || name.equals("print_FLOAT")) {

      mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

      String javaMethodName = name.equals("println") ? "println" : "print";

      if (functionCallNode.getParams().isEmpty()) {
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false);
      } else {
        Node arg = functionCallNode.getParams().get(0);

        generateExpression(arg, mv);
        String argTypeDesc = "I";

        if (arg instanceof StringNode) {
          argTypeDesc = "Ljava/lang/String;";
        } else if (arg instanceof FloatNode) {
          argTypeDesc = "F";
        } else if (arg instanceof BoolNode) {
          argTypeDesc = "Z";
        } else if (arg instanceof IdNode) {
          String varName = ((IdNode) arg).getName();
          if (localVariableTypes.containsKey(varName)) {
            argTypeDesc = localVariableTypes.get(varName);
          } else if (globalVariablesTypes.containsKey(varName)) {
            argTypeDesc = globalVariablesTypes.get(varName);
          }
        }

        String methodSignature = "(" + argTypeDesc + ")V";

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", javaMethodName, methodSignature, false);
      }
    }
    else if (name.equals("read_INT")) {
      mv.visitMethodInsn(INVOKESTATIC, "AppRuntime", "read_INT", "()I", false);
    }
    else if (structConstructors.containsKey(name)) {
      mv.visitTypeInsn(NEW, name);
      mv.visitInsn(DUP);
      for (Node arg : functionCallNode.getParams()) {
        generateExpression(arg, mv);
      }
      mv.visitMethodInsn(INVOKESPECIAL, name, "<init>", structConstructors.get(name), false);
    }
    else {
      StringBuilder descBuilder = new StringBuilder("(");
      for (Node arg : functionCallNode.getParams()) {
        generateExpression(arg, mv);
        descBuilder.append("I");
      }
      descBuilder.append(")");

      String returnTypeDesc = functionReturnTypes.getOrDefault(name, "I");
      descBuilder.append(returnTypeDesc);

      mv.visitMethodInsn(INVOKESTATIC, this.className, name, descBuilder.toString(), false);
    }
  }

  private void generateExpression(Node node, MethodVisitor mv) {
    if (node == null) {
      throw new RuntimeException("node is null");
    };

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
        String type = localVariableTypes.get(name);
        if (type != null && (type.startsWith("L") || type.startsWith("["))) {
          mv.visitVarInsn(ALOAD, variableSlots.get(name));
        } else {
          mv.visitVarInsn(ILOAD, variableSlots.get(name));
        }
      } else {
        String typeDescriptor = globalVariablesTypes.getOrDefault(name, "I");
        mv.visitFieldInsn(GETSTATIC, this.className, name, typeDescriptor);
      }
    }
    else if (node instanceof ArrayNode arrayNode) {
      generateExpression(arrayNode.getSize(), mv);

      String baseType = arrayNode.getType();
      String descriptor = getDescriptor(baseType);

      if (descriptor.equals("I")) {
        mv.visitIntInsn(NEWARRAY, T_INT);
      } else if (descriptor.equals("F")) {
        mv.visitIntInsn(NEWARRAY, T_FLOAT);
      } else if (descriptor.equals("Z")) {
        mv.visitIntInsn(NEWARRAY, T_BOOLEAN);
      } else {
        String internalName = descriptor;
        if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
          internalName = descriptor.substring(1, descriptor.length() - 1);
        }
        mv.visitTypeInsn(ANEWARRAY, internalName);
      }
    }
    else if (node instanceof BinaryNode binaryNode) {
      generateExpression(binaryNode.getLeft(), mv);
      generateExpression(binaryNode.getRight(), mv);

      boolean isFloat = isFloatExpression(binaryNode.getLeft()) || isFloatExpression(binaryNode.getRight());

      String op = binaryNode.getOperator();
      switch (op) {
        case "+" -> mv.visitInsn(isFloat ? FADD : IADD);
        case "-" -> mv.visitInsn(isFloat ? FSUB : ISUB);
        case "*" -> mv.visitInsn(isFloat ? FMUL : IMUL);
        case "/" -> mv.visitInsn(isFloat ? FDIV : IDIV);
        case "%" -> mv.visitInsn(isFloat ? FREM : IREM);
        case "==", "!=", "<", ">", "<=", ">=", "=/=" -> {
          if (isFloat) {
            generateFloatComparison(op, mv);
          } else {
            generateComparison(op, mv);
          }
        }
        default -> throw new UnsupportedOperationException("Opérateur non géré : " + op);
      }
    }
    else if (node instanceof DotNode dotNode) {
      generateExpression(dotNode.getNode(), mv);

      String fieldName = dotNode.getFieldName();

      String ownerClass = "Point";
      String descriptor = "I";

      mv.visitFieldInsn(GETFIELD, ownerClass, fieldName, descriptor);
    }

    else if (node instanceof TableAccessNode tableAccess) {
      generateExpression(tableAccess.getName(), mv);
      generateExpression(tableAccess.getValue(), mv);
      mv.visitInsn(AALOAD);
    }

    else {
      throw new UnsupportedOperationException("Le générateur ne reconnaît pas le type de nœud : "
          + node.getClass().getCanonicalName());
    }
  }

  private boolean isFloatExpression(Node node) {
    if (node instanceof FloatNode) return true;
    if (node instanceof IdNode id) {
      String type = localVariableTypes.get(id.getName());
      if (type == null) type = globalVariablesTypes.get(id.getName());
      return "F".equals(type);
    }
    if (node instanceof BinaryNode bin) {
      return isFloatExpression(bin.getLeft()) || isFloatExpression(bin.getRight());
    }
    if (node instanceof FunctionCallNode call) {
      return "F".equals(functionReturnTypes.get(call.getName()));
    }
    return false;
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
      case "=/=" -> IF_ICMPNE;
      default   -> throw new UnsupportedOperationException();
    };
    mv.visitJumpInsn(jumpOpcode, trueLabel);
    mv.visitInsn(ICONST_0);
    mv.visitJumpInsn(GOTO, endLabel);
    mv.visitLabel(trueLabel);
    mv.visitInsn(ICONST_1);
    mv.visitLabel(endLabel);
  }


  private void generateFloatComparison(String op, MethodVisitor mv) {
    Label trueLabel = new Label();
    Label endLabel = new Label();

    mv.visitInsn(FCMPL);

    int jumpOpcode = switch (op) {
      case "==" -> IFEQ;
      case "!=", "=/=" -> IFNE;
      case "<"  -> IFLT;
      case ">"  -> IFGT;
      case "<=" -> IFLE;
      case ">=" -> IFGE;
      default -> throw new UnsupportedOperationException("Opérateur de comparaison inconnu : " + op);
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
    globalVariablesTypes.put(name, typeDescriptor);
    classWriter.visitField(ACC_PUBLIC + ACC_STATIC, name, typeDescriptor, null, null).visitEnd();
  }

  private String getDescriptor(String type) {
    if (type == null || type.isEmpty()) return "I";

    String upper = type.toUpperCase().trim();

    if (upper.contains("ARRAY") || type.contains("[]")) {
      String baseType = type.replaceAll("(?i)ARRAY", "").replace("[]", "").trim();
      return "[" + getDescriptor(baseType);
    }

    switch (upper) {
      case "INT": return "I";
      case "FLOAT": return "F";
      case "BOOL": return "Z";
      case "STRING": return "Ljava/lang/String;";
      case "VOID":   return "V";
      default:
        return "L" + type + ";";
    }
  }

  private void generateStructClass(CollectionDefNode node) {
    String structName = node.getName();
    ClassWriter structClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    structClassWriter.visit(V1_8, ACC_PUBLIC, structName, null, "java/lang/Object", null);

    StringBuilder constructorDesc = new StringBuilder("(");

    for (Node prop : node.getProperties()) {
      if (prop instanceof DeclarationNode) {
        DeclarationNode field = (DeclarationNode) prop;
        String desc = getDescriptor(field.getType());


        structClassWriter.visitField(ACC_PUBLIC, field.getName(), desc, null, null).visitEnd();
        constructorDesc.append(desc);
      }
    }
    constructorDesc.append(")V");

    MethodVisitor mv = structClassWriter.visitMethod(ACC_PUBLIC, "<init>", constructorDesc.toString(), null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

    int paramSlot = 1;

    for (Node prop : node.getProperties()) {
      if (prop instanceof DeclarationNode field) {
          String desc = getDescriptor(field.getType());
        //empile this pour faire ref a l'objet
        mv.visitVarInsn(ALOAD, 0);
        //empile le parametre
        if (desc.equals("F")) {
          mv.visitVarInsn(FLOAD, paramSlot);
        } else if (desc.startsWith("L") || desc.startsWith("[")) {
          mv.visitVarInsn(ALOAD, paramSlot);
        } else {
          mv.visitVarInsn(ILOAD, paramSlot);
        }
        // dépile le parametre et this pour creer this.x = x par exemple
        mv.visitFieldInsn(PUTFIELD, structName, field.getName(), desc);
        paramSlot++;
      }
    }
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    structClassWriter.visitEnd();
    generatedClasses.put(structName, structClassWriter.toByteArray());
  }
}