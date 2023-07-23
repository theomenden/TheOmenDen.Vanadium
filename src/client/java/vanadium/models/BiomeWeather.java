package vanadium.models;


import net.minecraft.world.level.biome.Biome;

public final record BiomeWeather(boolean hasPrecipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall){}
