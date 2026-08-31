package org.powernukkitx.codegen;

import com.palantir.javapoet.ClassName;

public interface TypeNames {
    ClassName DATA = ClassName.get("lombok", "Data");

    ClassName ITEM_TYPE = ClassName.get("org.powernukkitx.item.type", "ItemType");
    ClassName ITEM_TYPES = ClassName.get("org.powernukkitx.item.type", "ItemTypes");

    ClassName BLOCK_TYPE = ClassName.get("org.powernukkitx.block.type", "BlockType");
    ClassName BLOCK_TYPES = ClassName.get("org.powernukkitx.block.type", "BlockTypes");
}
