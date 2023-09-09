package vanadium.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vanadium.biomeblending.blending.BlendingChunk;
import vanadium.models.enums.InternalEventType;
import vanadium.models.records.BiomeColorTypes;
import vanadium.models.records.Coordinates;
import vanadium.models.records.DebugEvent;
import vanadium.models.records.Summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static vanadium.models.enums.InternalEventType.COLOR;

public final class DebugUtils {
    public static final int INITIAL_FRAMES = 12288;
    public static int eventCount = 0;
    public static volatile boolean shouldMeasurePerformance = false;
    public static List<DebugEvent> events = Lists.newArrayList();
    public static ReentrantLock lock = new ReentrantLock();

    public static AtomicLong colorTypeHit = new AtomicLong();
    public static AtomicLong colorTypeMiss = new AtomicLong();
    public static AtomicLong threadLocalHit = new AtomicLong();
    public static AtomicLong threadLocalMiss = new AtomicLong();
    public static AtomicLong blendingCacheHit = new AtomicLong();
    public static AtomicLong blendingCacheMiss = new AtomicLong();

    public static void registerDebugCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> benchmarkCommands = Commands
                .literal("vanadium")
                .then(Commands.literal("toggleBenchmark")
                .executes(
                        context -> {
                            boolean isBenchmarking = DebugUtils.toggle();
                            Player player = Minecraft.getInstance().player;

                            if(isBenchmarking) {
                                if(player != null) {
                                    player.displayClientMessage(
                                            Component.literal("Benchmark started. Stop with /vanadium toggleBenchmark"), false);
                                }
                            }
                            else {
                                if(player != null) {
                                    player.displayClientMessage(Component.literal("Stopped benchmark"), false);
                                }

                                Summary summary = DebugUtils.collateDebuggingResults();

                                StringBuilder sb = getSummary(summary);

                                if(player != null) {
                                    player.displayClientMessage(Component.literal(sb.toString()), false);
                                }

                                DebugUtils.teardown();
                            }

                            return 0;
                        }
                ));

        dispatcher.register(benchmarkCommands);
    }

    @NotNull
    private static StringBuilder getSummary(Summary summary) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Call Count: %d", summary.totalCalls()));
        sb.append(String.format("Wall Time: %.2f s", summary.elapsedTimeInSeconds()));
        sb.append(String.format("Calls/sec: %.2f", summary.callsPerSecond()));
        sb.append(String.format("Avg. CPU time: %.2f ns", summary.averageTime()));
        sb.append(String.format("Avg. 1%%: %.2f ns", summary.averageSinglePercentTime()));
        sb.append(String.format("Total CPU time: %.2f ms", summary.totalCpuTimeInMilliseconds()));
        sb.append(String.format("Total Subevent CPU time: %.2f ms", summary.totalSubEventCpuTimeInMilliseconds()));
        sb.append(String.format("Avg. Subevent: %.2f ns", summary.averageSubEventTime()));
        sb.append(String.format("Avg. Subevent 1%%: %.2f ns", summary.averageSubEventSinglePercentTime()));
        return sb;
    }

    public static Summary collateDebuggingResults() {
        var colorGenEvents = new ArrayList<DebugEvent>();
        var subEvents = new ArrayList<DebugEvent>();

        IntStream.range(0, eventCount)
                .forEach(i ->
                {
                    DebugEvent event = events.get(i);

                    var eventDebugType = event.getDebugType();

                    switch(eventDebugType){
                        case COLOR -> colorGenEvents.add(event);
                        case SUBEVENT -> subEvents.add(event);
                    }
                });

        long startTime = Long.MAX_VALUE;
        long endTime = Long.MIN_VALUE;

        colorGenEvents
                .forEach(event -> {
                    if(event.getProfileStartTime() < startTime) {
                        event.setProfileStartTime(startTime);
                    }

                    if(event.getProfileEndTime() > endTime) {
                        event.setProfileEndTime(endTime);
                    }
                });

        long elapsedTime = endTime - startTime;

        sortDebugEvents(colorGenEvents);

        sortDebugEvents(subEvents);

        int colorGenerationEventCount = colorGenEvents.size();
        double averageTime = getAverageElapsedTime(colorGenEvents, colorGenerationEventCount);
        double averageSinglePercentage = getAverageElapsedTime(colorGenEvents, (colorGenerationEventCount + 99));
        double averageSubEventTime = getAverageElapsedTime(subEvents, subEvents.size());
        double averageSingleSubEventPercentage = getAverageElapsedTime(subEvents, (subEvents.size() + 99));
        double totalCPUTimeInMilliseconds = (double)averageTime * (double)colorGenerationEventCount * 1e-6;
        double totalSubeventCPUTimeInMilliseconds = averageSubEventTime * (double)subEvents.size() * 1e-6;

        return new Summary(
                colorGenerationEventCount,
                elapsedTime,
                (double)elapsedTime * 1e-9,
                (double)averageTime * (double) colorGenerationEventCount * 1e-6,
                averageTime,
                averageSinglePercentage,
                totalCPUTimeInMilliseconds,
                totalSubeventCPUTimeInMilliseconds,
                averageSubEventTime,
                averageSingleSubEventPercentage
        );
    }

    public static boolean toggle() {
        if(!shouldMeasurePerformance) {
            initialize();
            shouldMeasurePerformance = true;
            return true;
        }

        shouldMeasurePerformance = false;
        return false;
    }

    public static void teardown() {
        events.clear();
        eventCount = 0;
    }

    @Nullable
    public static DebugEvent putColorEvent(Coordinates chunkCoordinates, BiomeColorTypes colorType) {
        DebugEvent event = null;

        if(DebugUtils.shouldMeasurePerformance) {
            event = putDebugEvent();
            event.setDebugType(COLOR);
            event.setProfileStartTime(System.nanoTime());
            event.setChunkCoordinates(chunkCoordinates);
            event.setColorType(colorType);
        }
        return event;
    }

    @Nullable
    public static DebugEvent putSubEvent(InternalEventType eventType) {
        DebugEvent event = null;

        if(DebugUtils.shouldMeasurePerformance) {
            event = putDebugEvent();
            event.setDebugType(eventType);
            event.setProfileStartTime(System.nanoTime());
        }

        return event;
    }

    public static void endEventProfile(DebugEvent event) {
        if(event == null) {
            return;
        }

        event.setProfileEndTime(System.nanoTime());
    }

    public static void countColorTypes(int colorType, int lastColorType) {
        if(colorType == lastColorType) {
            colorTypeHit.getAndIncrement();
            return;
        }

        colorTypeMiss.getAndIncrement();
    }

    public static void countThreadLocalChunks(BlendingChunk chunk) {
        if(chunk != null) {
            threadLocalHit.getAndIncrement();
            return;
        }

        threadLocalMiss.getAndIncrement();
    }

    public static void countBlendingCaches(BlendingChunk chunk) {
        if(chunk != null) {
            blendingCacheHit.getAndIncrement();
            return;
        }

        blendingCacheMiss.getAndIncrement();
    }

    private static void initialize() {
        lock.lock();
        events = new ArrayList<>(INITIAL_FRAMES);
        events.addAll(Collections.nCopies(INITIAL_FRAMES -1, new DebugEvent()));
        lock.unlock();
    }

    private static void reallocateEventBuffer() {
        events.addAll(Collections.nCopies(events.size(), new DebugEvent()));
    }

    private static void sortDebugEvents(ArrayList<DebugEvent> colorGenEvents) {
        colorGenEvents.sort(
                (a,b) -> {
                    long timeA = a.getProfileEndTime() - a.getProfileStartTime();
                    long timeB = b.getProfileEndTime() - b.getProfileStartTime();

                    int result = 0;

                    if(timeA != timeB) {
                        result = (timeA > timeB) ? -1 : 1;
                    }

                    return result;
                }
        );
    }

    private static DebugEvent putDebugEvent() {
        lock.lock();

        if(eventCount >= events.size()) {
            reallocateEventBuffer();
        }

        var result = events.get(eventCount);

        ++eventCount;

        lock.unlock();
        return result;
    }

    private static double getAverageElapsedTime(ArrayList<DebugEvent> events, int count) {
        long accumulatedElapsedTime = IntStream
                .range(0, count)
                .mapToObj(events::get)
                .mapToLong(event -> event.getProfileEndTime() - event.getProfileStartTime())
                .sum();

        return ((double)accumulatedElapsedTime / (double)(count));
    }
}
