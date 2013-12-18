option(ZANDER_GET_DEPENDENCIES "Use zander to install the dependencies" ON)

string(TOLOWER "${CMAKE_BUILD_TYPE}" CMAKE_BUILD_TYPE_LOWER)
if(CMAKE_BUILD_TYPE_LOWER STREQUAL "debug")
	set(ZANDER_BUILD_TYPE "debug" CACHE STRING "Build Type")
else()
	set(ZANDER_BUILD_TYPE "release" CACHE STRING "Build Type")
endif()

if(ZANDER_GET_DEPENDENCIES)
	if(MSVC)
		set(ZANDER_COMPILER "msvc12")
	elseif(CMAKE_COMPILER_IS_GNUCXX OR "${CMAKE_CXX_COMPILER} ${CMAKE_CXX_COMPILER_ARG1}" MATCHES ".*clang")
		set(ZANDER_COMPILER "gnu")
	else()
		set(ZANDER_UNKNOWN_COMPILER ON)
	endif()

	if(NOT ZANDER_UNKNOWN_COMPILER)
		message("Working Directory: ${ZANDER_EXT_DIRECTORY}")
		file(MAKE_DIRECTORY ${ZANDER_EXT_DIRECTORY})
		execute_process(COMMAND cmd /c zander get unittest11 ${ZANDER_COMPILER} ${ZANDER_BUILD_TYPE} WORKING_DIRECTORY ${ZANDER_EXT_DIRECTORY})
	endif()
endif()
