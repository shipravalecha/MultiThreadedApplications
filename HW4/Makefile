CLASSES = BitonicSequential.class BitonicParallel.class BitonicBarrier.class Pair.class RandomArrayGenerator.class
JAVAFLAGS = -J-Xmx48m


all: $(CLASSES)

%.class : %.java
	javac $(JAVAFLAGS) $<

clean:
	@rm -f *.class
