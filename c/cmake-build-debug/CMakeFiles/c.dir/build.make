# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.27

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /Applications/CLion.app/Contents/bin/cmake/mac/aarch64/bin/cmake

# The command to remove a file.
RM = /Applications/CLion.app/Contents/bin/cmake/mac/aarch64/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c"

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug"

# Include any dependencies generated for this target.
include CMakeFiles/c.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include CMakeFiles/c.dir/compiler_depend.make

# Include the progress variables for this target.
include CMakeFiles/c.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/c.dir/flags.make

CMakeFiles/c.dir/main.c.o: CMakeFiles/c.dir/flags.make
CMakeFiles/c.dir/main.c.o: /Users/simonesamardzhiev/Desktop/My\ projects/Inventory\ Management\ System/Inventory-Management-System/c/main.c
CMakeFiles/c.dir/main.c.o: CMakeFiles/c.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir="/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/c.dir/main.c.o"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -MD -MT CMakeFiles/c.dir/main.c.o -MF CMakeFiles/c.dir/main.c.o.d -o CMakeFiles/c.dir/main.c.o -c "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/main.c"

CMakeFiles/c.dir/main.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing C source to CMakeFiles/c.dir/main.c.i"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/main.c" > CMakeFiles/c.dir/main.c.i

CMakeFiles/c.dir/main.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling C source to assembly CMakeFiles/c.dir/main.c.s"
	/Library/Developer/CommandLineTools/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/main.c" -o CMakeFiles/c.dir/main.c.s

# Object files for target c
c_OBJECTS = \
"CMakeFiles/c.dir/main.c.o"

# External object files for target c
c_EXTERNAL_OBJECTS =

c : CMakeFiles/c.dir/main.c.o
c : CMakeFiles/c.dir/build.make
c : CMakeFiles/c.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --bold --progress-dir="/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug/CMakeFiles" --progress-num=$(CMAKE_PROGRESS_2) "Linking C executable c"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/c.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/c.dir/build: c
.PHONY : CMakeFiles/c.dir/build

CMakeFiles/c.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/c.dir/cmake_clean.cmake
.PHONY : CMakeFiles/c.dir/clean

CMakeFiles/c.dir/depend:
	cd "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug" && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c" "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c" "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug" "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug" "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/Inventory-Management-System/c/cmake-build-debug/CMakeFiles/c.dir/DependInfo.cmake" "--color=$(COLOR)"
.PHONY : CMakeFiles/c.dir/depend

