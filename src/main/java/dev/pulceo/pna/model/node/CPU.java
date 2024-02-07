package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CPU extends Resource {

    private int MIPS;
    private int cores;
    private int threads;
    private int GFlop;
    private double frequency;
    private int slots;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CPU cpu = (CPU) o;

        if (MIPS != cpu.MIPS) return false;
        if (cores != cpu.cores) return false;
        if (threads != cpu.threads) return false;
        if (GFlop != cpu.GFlop) return false;
        if (Double.compare(frequency, cpu.frequency) != 0) return false;
        return slots == cpu.slots;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + MIPS;
        result = 31 * result + cores;
        result = 31 * result + threads;
        result = 31 * result + GFlop;
        temp = Double.doubleToLongBits(frequency);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + slots;
        return result;
    }
}
