package gay.solonovamax.beaconsoverhaul;

public interface MutableTieredBeacon extends TieredBeacon {
    void setTier(final PotencyTier tier);
}
