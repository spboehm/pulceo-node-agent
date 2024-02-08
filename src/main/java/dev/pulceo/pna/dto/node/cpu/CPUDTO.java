package dev.pulceo.pna.dto.node.cpu;

import dev.pulceo.pna.model.node.CPU;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CPUDTO {

        private String modelName;
        private int cores;
        private int threads;
        private float bogoMIPS;
        private float MIPS;
        private float GFlop;
        private float minimalFrequency;
        private float averageFrequency;
        private float maximalFrequency;
        private float slots;

        public static CPUDTO fromCPU(CPU cpu) {
            return CPUDTO.builder()
                    .modelName(cpu.getModelName())
                    .cores(cpu.getCores())
                    .threads(cpu.getThreads())
                    .bogoMIPS(cpu.getBogoMIPS())
                    .MIPS(cpu.getMIPS())
                    .GFlop(cpu.getGFlop())
                    .minimalFrequency(cpu.getMinimalFrequency())
                    .averageFrequency(cpu.getAverageFrequency())
                    .maximalFrequency(cpu.getMaximalFrequency())
                    .slots(cpu.getSlots())
                    .build();
        }
}
