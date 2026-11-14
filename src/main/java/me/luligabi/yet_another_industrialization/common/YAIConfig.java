package me.luligabi.yet_another_industrialization.common;

// синк/порт 26.1.2: убраны аннотации tesseract.config (ConfigKey/SubSection/Range/ConfigComment),
// т.к. tesseract_api вынесено из рантайма (MI 2.6.0 несовместимо с tesseract_api).
public interface YAIConfig {

    MachineDiagnoser machineDiagnoser();

    interface MachineDiagnoser {
        int cooldownTicks();
    }

    MachineRemover machineRemover();

    interface MachineRemover {
        int cooldownTicks();
        long capacity();
        long singleBlockRemoveCost();
        long multiblockBaseRemoveCost();
        long multiblockBlockRemoveCost();
        long multiblockBlockMaxSize();
    }

    boolean villagerTrades();

    // port-26.1.2: tesseract ConfigManager убран из рантайма (tesseract_api-конфиги несовместимы с MI 2.6) —
    // дефолты апстрима YAIConfig.kt (коммит9a1e383: villagerTrades = true)
    YAIConfig DEFAULT = new YAIConfig() {
        private final MachineDiagnoser diagnoser = new MachineDiagnoser() {
            @Override
            public int cooldownTicks() {
                return 60;
            }
        };

        private final MachineRemover remover = new MachineRemover() {
            @Override
            public int cooldownTicks() {
                return 60;
            }

            @Override
            public long capacity() {
                return 65536L;
            }

            @Override
            public long singleBlockRemoveCost() {
                return 5L;
            }

            @Override
            public long multiblockBaseRemoveCost() {
                return 100L;
            }

            @Override
            public long multiblockBlockRemoveCost() {
                return 25L;
            }

            @Override
            public long multiblockBlockMaxSize() {
                return 1000000L;
            }
        };

        @Override
        public MachineDiagnoser machineDiagnoser() {
            return diagnoser;
        }

        @Override
        public MachineRemover machineRemover() {
            return remover;
        }

        @Override
        public boolean villagerTrades() {
            return true;
        }
    };
}
