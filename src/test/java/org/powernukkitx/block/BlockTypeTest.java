package org.powernukkitx.block;

import org.junit.jupiter.api.Test;
import org.powernukkitx.block.type.BlockTypes;

public class BlockTypeTest {
    @Test
    void test() {
        System.out.println(BlockTypes.JUKEBOX.getIdentifier());
        System.out.println(BlockTypes.AIR.getIdentifier());
        System.out.println(BlockTypes.get("minecraft:lmao"));
        System.out.println(BlockTypes.get("minecraft:jungle_button"));
        System.out.println(BlockTypes.get("minecraft:jungle_door").getIdentifier());
    }
}
