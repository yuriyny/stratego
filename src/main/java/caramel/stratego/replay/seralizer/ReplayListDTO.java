package caramel.stratego.replay.seralizer;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReplayListDTO {
    List<ReplayDTO> replayDTOList = new ArrayList<>();
}
