CLASSES = QuickSortSequential.class QuickSortParallel.class QuickSort.class RandomArrayGenerator.class ForkJoin.class
JAVAFLAGS = -J-Xmx48m


all: $(CLASSES)

%.class : %.java
	javac $(JAVAFLAGS) $<

clean:
	@rm -f *.class
