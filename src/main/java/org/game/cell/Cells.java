package org.game.cell;

import org.types.Colour;

import java.util.EnumMap;
import java.util.Map;

public final class Cells {
    public static final CellType AIR
            = new CellType(CellID.AIR, new Colour(0, 0, 0), false,0);
    public static final CellType DIRT
            = new CellType(CellID.DIRT, new Colour(117, 58, 28), true,10);
    public static final CellType GRASS
            = new CellType(CellID.GRASS, new Colour(30, 125, 21), true, 10);
    public static final CellType STONE
            = new CellType(CellID.STONE, new Colour(71, 71, 71), true,15);
    public static final CellType IRON
            = new CellType(CellID.IRON, new Colour(153, 96, 66), true,30);

    private static Map<CellID, CellType> CELL_MAP = create_cell_map();

    private static Map<CellID, CellType> create_cell_map() {
        Map<CellID, CellType> map = new EnumMap<>(CellID.class);

        map.put(CellID.AIR, AIR);

        map.put(CellID.DIRT, DIRT);
        map.put(CellID.GRASS, GRASS);
        map.put(CellID.STONE, STONE);
        map.put(CellID.IRON, IRON);

        return Map.copyOf(map);
    }


    public static CellType get(CellID id) {
        return CELL_MAP.get(id);
    }
}
