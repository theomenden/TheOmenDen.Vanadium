package vanadium.models;

import net.minecraft.world.biome.Biome;

public final record BiomeWeather(boolean hasPrecipitation, float temperatrue, Biome.TemperatureModifier temperatureModifier, float downfall){}
