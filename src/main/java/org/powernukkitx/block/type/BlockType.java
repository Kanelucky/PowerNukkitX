package org.powernukkitx.block.type;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.registry.Registries;

public interface BlockType {
    String getIdentifier();

    default BlockProperties getProperties() {
        return Registries.BLOCK.getBlockProperties(getIdentifier());
    }

    default BlockState getDefaultState() {
        return getProperties().getDefaultState();
    }
}
