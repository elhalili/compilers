CC = gcc
FLAGS = 

SRC_DIR = src
BUILD_DIR = build

TARGET = $(BUILD_DIR)/vm
MAIN_SRC = $(SRC_DIR)/main.c

all: $(TARGET) 

$(TARGET): $(MAIN_SRC)
	- mkdir -p $(BUILD_DIR)
	- $(CC) -o $@ $(FLAGS) $<

clean:
	- rm -rf $(BUILD_DIR)