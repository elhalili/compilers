CXX = clang++
FLAGS = -g -O3

NAME = toy

$(NAME): src/main.cpp build
	- $(CXX) -o build/$(NAME) src/main.cpp $(FLAGS) 

build: 
	- mkdir -p build

clean: 
	- rm -rf build
