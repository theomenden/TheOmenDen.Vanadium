package vanadium.models.records;

public record Summary(
        long totalCalls,
        long elapsedTime,
        double elapsedTimeInSeconds,
        double callsPerSecond,
        double averageTime,
        double averageSinglePercentTime,
        double totalCpuTimeInMilliseconds,
        double totalSubEventCpuTimeInMilliseconds,
        double averageSubEventTime,
        double averageSubEventSinglePercentTime
) {
}
