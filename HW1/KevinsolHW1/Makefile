CPPFLAGS = -std=c++11 -Wall -Werror -pedantic -ggdb -pthread -O2

hw1 : hw1.cpp ThreadGroup.h
	g++ $(CPPFLAGS) $< -o $@

hw1_setup : hw1_setup.cpp ThreadGroup.h
	g++ $(CPPFLAGS) $< -o $@

example : example.cpp ThreadGroup.h
	g++ $(CPPFLAGS) $< -o $@

all : example hw1_setup hw1 hw1_setup.zip hw1.zip
	@echo "Made it all!"

clean :
	rm -f hw1_setup example hw1 *.zip

hw1_setup.zip : hw1_setup.cpp ThreadGroup.h example.cpp Makefile
	zip $@ $^

hw1.zip : hw1.cpp hw1_setup.cpp ThreadGroup.h example.cpp Makefile
	zip $@ $^
