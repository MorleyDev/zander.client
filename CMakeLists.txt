cmake_minimum_required (VERSION 2.6) 
project(ZANDER CXX) 

option(BUILD_CLIENT_EXE "Build zander the client executable" ON)
option(ENABLE_TESTS "Enable building of tests and usage of CTest" ON)
option(BUILD_UNIT_TESTS "Build the unit tests for the zander client" ON)
option(BUILD_SPEC_TESTS "Build the specification tests for the zander client" ON)
option(BUILD_SMOKE_TESTS "Build the smoke tests for the zander client" ON)

include(cxx11.cmake)

set(ZANDER_DEPENDENCIES unittest11)
include(ext.cmake)

if (BUILD_CLIENT_EXE)
	add_subdirectory(src)
endif()

if (ENABLE_TESTS)
	enable_testing()
	
	if (BUILD_UNIT_TESTS)
		add_subdirectory(test)
	endif()
	
	if (BUILD_SPEC_TESTS)
		add_subdirectory(spec)
	endif()
	
	if (BUILD_SMOKE_TESTS)
		add_subdirectory(smoke)
	endif()
endif()
