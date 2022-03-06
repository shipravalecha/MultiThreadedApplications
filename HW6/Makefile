CLASSES = Observation.class ColoredGrid.class HW6Threshold.class HW6Decay.class HW6Sampling.class ReduceTask.class ReduceTaskPass2.class Step7SamplingTask.class Step8DecayTask.class ScanTask.class ScanTaskPass2.class HeatMap.class GeneralScan.class HeatmapParallel.class HeatmapParallelPass2.class
JAVAFLAGS = -J-Xmx48m

all: $(CLASSES)

%.class : %.java
	javac $(JAVAFLAGS) $<

clean:
	@rm -f *.class
