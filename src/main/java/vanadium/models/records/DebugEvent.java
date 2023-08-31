package vanadium.models.records;

import lombok.*;
import vanadium.models.enums.InternalEventType;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
public final class DebugEvent {
    @Getter
    @Setter
    private InternalEventType debugType;
    @Getter
    @Setter
    private long profileStartTime;
    @Getter
    @Setter
    private long profileEndTime;
    @Getter
    @Setter
    private Coordinates chunkCoordinates;
    @Getter
    @Setter
    private BiomeColorTypes colorType;

}
