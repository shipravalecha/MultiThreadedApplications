CLASSES = Observation.class ColoredGrid.class HW5Main.class ReduceTask.class ScanTask.class GeneralScan.class HeatmapParallel.class
JAVAFLAGS = -J-Xmx48m

all: $(CLASSES)

%.class : %.java
	javac $(JAVAFLAGS) $<

clean:
	@rm -f *.class
