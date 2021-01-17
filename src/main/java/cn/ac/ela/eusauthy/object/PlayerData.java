package cn.ac.ela.eusauthy.object;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {

    public UUID uuid;
    public String secretKey;

}
