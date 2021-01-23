package cn.ac.ela.eusauthy.object;

import lombok.Data;

import java.util.UUID;

/**
 * @author ElaBosak
 */
@Data
public class PlayerData {

    public UUID uuid;
    public String secretKey;

}
