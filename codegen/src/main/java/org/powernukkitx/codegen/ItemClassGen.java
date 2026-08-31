package org.powernukkitx.codegen;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.utils.Utils;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://github.com/AllayMC/Allay/blob/master/codegen/src/main/java/org/allaymc/codegen/ItemClassGen.java">Source of inspiration</a>
 *
 * @author Kanelucky
 */
public class ItemClassGen {

    public static void main(String[] args) {
        generate();
    }

    @SneakyThrows
    public static void generate() {
        var typesClass = TypeSpec.classBuilder(TypeNames.ITEM_TYPES).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        addPrivateConstructor(typesClass);
        addRegistryFields(typesClass);

        for (var field : ItemID.class.getFields()) {
            if (field.getType() != String.class) {
                continue;
            }

            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            var name = field.getName();
            var identifier = (String) field.get(null);

            addItemType(typesClass, name, identifier);
        }

        addRegisterMethods(typesClass);
        addGetMethod(typesClass);
        addItemTypeImpl(typesClass);

        var javaFile = JavaFile.builder(TypeNames.ITEM_TYPES.packageName(), typesClass.build())
            .indent(CodeGenConstants.INDENT)
            .skipJavaLangImports(true)
            .build();

        var outputPath =
            Path.of("src/main/java/org/powernukkitx/item/type/" + TypeNames.ITEM_TYPES.simpleName() + ".java");

        System.out.println("Generating " + TypeNames.ITEM_TYPES.simpleName() + ".java ...");

        Utils.writeFileWithCRLF(outputPath, javaFile.toString());
    }

    private static void addPrivateConstructor(TypeSpec.Builder builder) {
        builder.addMethod(
            MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
    }

    private static void addRegistryFields(TypeSpec.Builder builder) {
        var mapType =
            ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), TypeNames.ITEM_TYPE);

        builder.addField(FieldSpec.builder(mapType, "ID_TO_TYPE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("new $T<>()", HashMap.class)
            .build());
    }

    private static void addItemType(TypeSpec.Builder builder, String name, String identifier) {
        builder.addField(FieldSpec.builder(TypeNames.ITEM_TYPE, name, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("register($S)", identifier)
            .build());
    }

    private static void addRegisterMethods(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("register")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeNames.ITEM_TYPE)
            .addParameter(String.class, "identifier")
            .addStatement("return register(new ItemTypeImpl(identifier))")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("register")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeNames.ITEM_TYPE)
            .addParameter(TypeNames.ITEM_TYPE, "itemType")
            .addStatement("$T oldType = ID_TO_TYPE.get(itemType.getIdentifier())", TypeNames.ITEM_TYPE)
            .addStatement("ID_TO_TYPE.putIfAbsent(itemType.getIdentifier(), itemType)")
            .addStatement("return oldType != null ? oldType : itemType")
            .build());
    }

    private static void addGetMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("get")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeNames.ITEM_TYPE)
            .addParameter(String.class, "identifier")
            .addStatement("return ID_TO_TYPE.get(identifier)")
            .build());
    }

    private static void addItemTypeImpl(TypeSpec.Builder builder) {
        builder.addType(TypeSpec.classBuilder("ItemTypeImpl")
            .addAnnotation(TypeNames.DATA)
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .addSuperinterface(TypeNames.ITEM_TYPE)
            .addField(FieldSpec.builder(String.class, "identifier", Modifier.PRIVATE, Modifier.FINAL)
                .build())
            .build());
    }
}
