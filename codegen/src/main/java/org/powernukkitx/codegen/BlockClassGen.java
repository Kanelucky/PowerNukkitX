package org.powernukkitx.codegen;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.utils.Utils;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * <><a href="https://github.com/AllayMC/Allay/blob/master/codegen/src/main/java/org/allaymc/codegen/BlockClassGen.java">Source of inspiration</a></>
 *
 * @author Kanelucky | AllayMC
 */
public class BlockClassGen {

    public static void main(String[] args) {
        generate();
    }

    @SneakyThrows
    public static void generate() {
        var typesClass = TypeSpec.classBuilder(TypeNames.BLOCK_TYPES).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        addPrivateConstructor(typesClass);
        addRegistryFields(typesClass);

        for (var field : BlockID.class.getFields()) {
            if (field.getType() != String.class) {
                continue;
            }

            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            var name = field.getName();
            var identifier = (String) field.get(null);

            addBlockType(typesClass, name, identifier);
        }

        addRegisterMethods(typesClass);
        addGetMethod(typesClass);
        addBlockTypeImpl(typesClass);

        var javaFile = JavaFile.builder(TypeNames.BLOCK_TYPES.packageName(), typesClass.build())
                .indent(CodeGenConstants.INDENT)
                .skipJavaLangImports(true)
                .build();

        var outputPath =
                Path.of("src/main/java/org/powernukkitx/block/type/" + TypeNames.BLOCK_TYPES.simpleName() + ".java");

        System.out.println("Generating " + TypeNames.BLOCK_TYPES.simpleName() + ".java ...");

        Utils.writeFileWithCRLF(outputPath, javaFile.toString());
    }

    private static void addPrivateConstructor(TypeSpec.Builder builder) {
        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
    }

    private static void addRegistryFields(TypeSpec.Builder builder) {
        var mapType =
                ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), TypeNames.BLOCK_TYPE);

        builder.addField(FieldSpec.builder(mapType, "ID_TO_TYPE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T<>()", HashMap.class)
                .build());
    }

    private static void addBlockType(TypeSpec.Builder builder, String name, String identifier) {
        builder.addField(FieldSpec.builder(TypeNames.BLOCK_TYPE, name, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("register($S)", identifier)
                .build());
    }

    private static void addRegisterMethods(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeNames.BLOCK_TYPE)
                .addParameter(String.class, "identifier")
                .addStatement("return register(new BlockTypeImpl(identifier))")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeNames.BLOCK_TYPE)
                .addParameter(TypeNames.BLOCK_TYPE, "blockType")
                .addStatement("$T oldType = ID_TO_TYPE.get(blockType.getIdentifier())", TypeNames.BLOCK_TYPE)
                .addStatement("ID_TO_TYPE.putIfAbsent(blockType.getIdentifier(), blockType)")
                .addStatement("$T.register(blockType.getIdentifier())", TypeNames.ITEM_TYPES)
                .addStatement("return oldType != null ? oldType : blockType")
                .build());
    }

    private static void addGetMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeNames.BLOCK_TYPE)
                .addParameter(String.class, "identifier")
                .addStatement("return ID_TO_TYPE.get(identifier)")
                .build());
    }

    private static void addBlockTypeImpl(TypeSpec.Builder builder) {
        builder.addType(TypeSpec.classBuilder("BlockTypeImpl")
                .addAnnotation(TypeNames.DATA)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addSuperinterface(TypeNames.BLOCK_TYPE)
                .addField(FieldSpec.builder(String.class, "identifier", Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .build());
    }
}
